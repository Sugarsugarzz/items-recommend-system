package casia.isiteam.recommendsystem.service.cb;

import casia.isiteam.recommendsystem.common.Candidates;
import casia.isiteam.recommendsystem.common.RecConfig;
import casia.isiteam.recommendsystem.model.Item;
import casia.isiteam.recommendsystem.utils.DBKit;
import casia.isiteam.recommendsystem.utils.RecommendKit;
import casia.isiteam.recommendsystem.utils.TFIDF;
import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.ansj.app.keyword.Keyword;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;


@Slf4j
@Service
public class ContentBasedRecommendService {

    @Autowired
    RecConfig recConfig;
    @Autowired
    UserPrefRefresher userPrefRefresher;

    public void recommend(List<Long> userIds, int infoType) {

        log.info("信息类型：{} - 基于内容的推荐 Start.", infoType);

        try {
            // 更新用户偏好
            userPrefRefresher.refresher(userIds, infoType);
            log.info("用户偏好更新完成（衰减+递增）！");

            // （信息项ID - 信息项关键词列表）
            Map<Long, List<Keyword>> itemsKeywordsMap = new HashMap<>();
            // （用户ID - 偏好列表 String）
            Map<Long, String> usersPrefListMap = RecommendKit.getUserPreListMap(userIds, infoType);
            // 获取所有信息项
            List<Item> itemList = DBKit.getItems(infoType);

            // itemList -->  itemsKeywordsMap
            for (Item item : itemList) {
                itemsKeywordsMap.put(RecommendKit.getItemId(item, infoType), TFIDF.getKeywordsByTFIDE(RecommendKit.getItemName(item, infoType), recConfig.getTfidfKeywordsNum()));
            }

            // 遍历用户，为每个用户生成匹配的信息项
            for (Long userId : userIds) {
                // 获取用户偏好
                Map<String, Object> map = JSONObject.parseObject(usersPrefListMap.get(userId));
                // 该用户无偏好则跳过
                if (ObjectUtil.isEmpty(map))  {
                    continue;
                }
                // 暂存与用户偏好匹配的信息项， （信息项ID - 匹配值）
                Map<Long, Double> tempMatchMap = new LinkedHashMap<>();
                // 遍历信息项，获取每个信息项与用户的匹配度
                for (Long itemId : itemsKeywordsMap.keySet()) {
                    Double matchValue = getMatchValue(map, itemsKeywordsMap.get(itemId));
                    if (matchValue > 1000) {
                        tempMatchMap.put(itemId, matchValue);
                    }
                }
                // 初始化
                RecommendKit.initToBeRecommended(userId, infoType);
                // 处理
                List<Map.Entry<Long, Double>> list = new ArrayList<>(tempMatchMap.entrySet());
                list.sort((o1, o2) -> (int) (o2.getValue() - o1.getValue()));
                Candidates.toBeRecommended.get(userId).get(infoType).addAll(list.stream().map(Map.Entry::getKey).limit(recConfig.getCbRecommendNum()).collect(Collectors.toList()));
            }

        } catch (Exception e) {
            log.error("信息类型：{} - 基于内容的推荐 失败：{}", infoType, e);
        }

        log.info("信息类型：{} - 基于内容的推荐 End.", infoType);

    }

    /**
     * 计算用户与该信息项的匹配值
     */
    private Double getMatchValue(Map<String, Object> map, List<Keyword> keywords) {
        Set<String> prefKeywords = map.keySet();
        double matchValue = 0;
        for (Keyword keyword : keywords) {
            if (prefKeywords.contains(keyword.getName())) {
                matchValue += keyword.getScore() * Double.parseDouble(map.get(keyword.getName()).toString());
            }
        }
        return matchValue;
    }

    /**
     * 根据 Value 对 Map 排序
     */
    private Map<Long, Double> sortMapByValue(Map<Long, Double> map) {
        if (map.size() == 0)  {
            return map;
        }
        List<Map.Entry<Long, Double>> list = new ArrayList<>(map.entrySet());
        list.sort((o1, o2) -> (int) (o2.getValue() - o1.getValue()));
        Map<Long, Double> resultMap = new LinkedHashMap<>();
        for (Map.Entry<Long, Double> entry : list) {
            resultMap.put(entry.getKey(), entry.getValue());
        }
        return resultMap;
    }
}
