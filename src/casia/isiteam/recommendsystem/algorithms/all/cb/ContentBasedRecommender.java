package casia.isiteam.recommendsystem.algorithms.all.cb;

import casia.isiteam.recommendsystem.algorithms.RecommendAlgorithm;
import casia.isiteam.recommendsystem.main.Recommender;
import casia.isiteam.recommendsystem.model.Item;
import casia.isiteam.recommendsystem.utils.ConfigKit;
import casia.isiteam.recommendsystem.utils.DBKit;
import casia.isiteam.recommendsystem.utils.RecommendKit;
import casia.isiteam.recommendsystem.utils.TFIDF;
import com.alibaba.fastjson.JSONObject;
import org.ansj.app.keyword.Keyword;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;

public class ContentBasedRecommender implements RecommendAlgorithm {

    private static final Logger logger = LogManager.getLogger(LogManager.ROOT_LOGGER_NAME);

    // TD-IDF提取关键词词数
    private static final int KEY_WORDS_NUM = ConfigKit.getInt("TFIDFKeywordsNum");
    // 利用CB推荐的总数
    private static final int recNum = ConfigKit.getInt("CBRecommendNum");


    @Override
    public void recommend(List<Long> userIDs, int infoType) {

        logger.info(infoType + "  基于内容的推荐 开始于 " + new Date());

        try {
            // 更新用户偏好
            new UserPrefRefresher().refresher(userIDs, infoType);
            logger.info("用户偏好更新完成（衰减+递增） 于 " + new Date());

            // （信息项ID - 信息项关键词列表）
            Map<Long, List<Keyword>> itemsKeywordsMap = new HashMap<>();
            // （用户ID - 偏好列表 String）
            Map<Long, String> usersPrefListMap = RecommendKit.getUserPreListMap(userIDs, infoType);
            // 获取所有信息项
            List<Item> itemList = DBKit.getItems(infoType);

            // itemList -->  itemsKeywordsMap
            for (Item item : itemList) {
                itemsKeywordsMap.put(RecommendKit.getItemId(item, infoType), TFIDF.getKeywordsByTFIDE(RecommendKit.getItemName(item, infoType), KEY_WORDS_NUM));
            }

            // 遍历用户，为每个用户生成匹配的信息项
            for (Long userID : userIDs) {
                // 获取用户偏好
                Map<String, Object> map = JSONObject.parseObject(usersPrefListMap.get(userID));
                // 该用户无偏好则跳过
                if (map.isEmpty())  continue;
                // 暂存与用户偏好匹配的信息项， （信息项ID - 匹配值）
                Map<Long, Double> tempMatchMap = new LinkedHashMap<>();
                // 遍历信息项，获取每个信息项与用户的匹配度
                for (Long itemID : itemsKeywordsMap.keySet()) {
                    tempMatchMap.put(itemID, getMatchValue(map, itemsKeywordsMap.get(itemID)));
                }
                // 初始化
                RecommendKit.initToBeRecommended(userID, infoType);
                // 处理
                removeLowMatchItem(tempMatchMap);
//                tempMatchMap = sortMapByValue(tempMatchMap);
                Recommender.toBeRecommended.get(userID).get(infoType).addAll(tempMatchMap.keySet());
            }

        } catch (Exception e) {
            logger.error(infoType + "  基于内容的推荐 失败：" + e);
        }

        logger.info(infoType + "  基于内容的推荐 结束于 " + new Date());

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
     * 清除匹配值小于 1000 的信息项
     */
    private void removeLowMatchItem(Map<Long, Double> map) {
        map.keySet().removeIf(itemID -> map.get(itemID) < 1000);
    }

    /**
     * 根据 Value 对 Map 排序
     */
    private Map<Long, Double> sortMapByValue(Map<Long, Double> map) {
        if (map.size() == 0)  return map;
        List<Map.Entry<Long, Double>> list = new ArrayList<>(map.entrySet());
        list.sort((o1, o2) -> (int) (o2.getValue() - o1.getValue()));
        Map<Long, Double> resultMap = new LinkedHashMap<>();
        for (Map.Entry<Long, Double> entry : list) {
            resultMap.put(entry.getKey(), entry.getValue());
        }
        return resultMap;
    }

}
