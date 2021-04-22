package casia.isiteam.recommendsystem.service.cb;

import casia.isiteam.recommendsystem.common.RecConfig;
import casia.isiteam.recommendsystem.model.Item;
import casia.isiteam.recommendsystem.model.ItemLog;
import casia.isiteam.recommendsystem.model.User;
import casia.isiteam.recommendsystem.utils.DBKit;
import casia.isiteam.recommendsystem.utils.RecommendKit;
import casia.isiteam.recommendsystem.utils.TFIDF;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.ansj.app.keyword.Keyword;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

@Slf4j
@Component
public class UserPrefRefresher {

    @Autowired
    RecConfig recConfig;

    public void refresher(List<Long> userIds, int infoType) {

        // 给没有偏好的用户设定默认偏好
        List<User> users = DBKit.getUserPrefList(userIds, infoType);
        for (User user : users) {
            if (StrUtil.isEmpty(RecommendKit.getPrefList(user, infoType))) {
                DBKit.updateUserPrefList(new JSONObject().toJSONString(), user.getId(), infoType);
            }
        }

        // 对用户偏好进行衰减
        decayUserPrefList(userIds, infoType);

        // 获取当日用户浏览记录  （用户ID - 浏览项ID列表）
        Map<Long, List<Long>> usersTodayBrowsedMap = getTodayBrowsedMap(userIds, infoType);
        if (usersTodayBrowsedMap.size() == 0) {
            log.info("当日无用户浏览记录，用户偏好无需更新。");
            return;
        }

        // 获取当日活跃用户的偏好  （用户ID - 偏好字段String）
        Map<Long, String> usersPrefListMap = RecommendKit.getUserPreListMap(usersTodayBrowsedMap.keySet(), infoType);
        // 获取  （信息项ID - 信息项关键词列表）
        Map<Long, List<Keyword>> itemsKeywordsMap = getItemsTfidfMap(usersTodayBrowsedMap, infoType);

        for (Long userId : usersTodayBrowsedMap.keySet()) {
            // 获取用户偏好
            Map<String, Object> map = JSONObject.parseObject(usersPrefListMap.get(userId));
            // 遍历用户浏览过的每个信息项，将关键词和TF-IDF值更新到偏好中
            List<Long> itemIds = usersTodayBrowsedMap.get(userId);
            for (Long itemId : itemIds) {
                List<Keyword> keywords = itemsKeywordsMap.get(itemId);
                if (ObjectUtil.isEmpty(keywords))  {
                    continue;
                }
                for (Keyword keyword : keywords) {
                    String word = keyword.getName();
                    if (map.containsKey(word)) {
                        map.put(word, Double.parseDouble(map.get(word).toString()) + keyword.getScore());
                    } else {
                        map.put(word, keyword.getScore());
                    }
                }
            }
            usersPrefListMap.put(userId, JSON.toJSONString(map));
        }

        // 更新偏好到表中
        for (Long userId : usersPrefListMap.keySet()) {
            DBKit.updateUserPrefList(usersPrefListMap.get(userId), userId, infoType);
        }
    }

    /**
     * 对用户偏好进行衰减
     */
    private void decayUserPrefList(List<Long> userIds, int infoType) {

        List<User> users = DBKit.getUserPrefList(userIds, infoType);
        for (User user : users) {
            // （关键词 - TF-IDF值）Map
            Map<String, Object> map = new HashMap<>();
            try {
                 map = JSONObject.parseObject(RecommendKit.getPrefList(user, infoType));
            } catch (Exception e) {
                log.error("用户偏好解析失败！偏好为：{}", RecommendKit.getPrefList(user, infoType));
            }

            // 计算更新衰减后的TF-IDF值，剔除低于阈值的关键词
            Iterator<Map.Entry<String, Object>> iter = map.entrySet().iterator();
            while (iter.hasNext()) {
                Map.Entry<String, Object> entry = iter.next();
                double result = Double.parseDouble(entry.getValue().toString()) * recConfig.getDecayNum();
                if (result < recConfig.getPrefThreshold()) {
                    iter.remove();
                } else {
                    entry.setValue(result);
                }
            }
            // 更新衰减后的用户偏好
            DBKit.updateUserPrefList(JSON.toJSONString(map), user.getId(), infoType);
        }
    }

    /**
     * 获取当日有浏览行为的用户ID及其浏览过的ID列表
     */
    private Map<Long, List<Long>> getTodayBrowsedMap(List<Long> userIds, int infoType) {
        Map<Long, List<Long>> map = new HashMap<>();
        List<ItemLog> todayBrowsedList = DBKit.getBrowsedItemsByDateAndUsers(RecommendKit.getSpecificDayFormat(-1), infoType, userIds);
        for (ItemLog itemLog : todayBrowsedList) {
            if (!map.containsKey(itemLog.getUser_id())) {
                map.put(itemLog.getUser_id(), new ArrayList<>());
            }
            map.get(itemLog.getUser_id()).add(itemLog.getRef_data_id());
        }
        return map;
    }

    /**
     * 获取当日所有被浏览过的信息项出来，并获取关键词及对应TF-IDF值
     */
    private Map<Long, List<Keyword>> getItemsTfidfMap(Map<Long, List<Long>> usersTodayBrowsedMap, int infoType) {
        Map<Long, List<Keyword>> map = new HashMap<>();
        List<Item> itemList = DBKit.getItemsByIDs(convertToItemIdsSet(usersTodayBrowsedMap), infoType);
        for (Item item : itemList) {
            map.put(RecommendKit.getItemId(item, infoType), TFIDF.getKeywordsByTFIDE(RecommendKit.getItemName(item, infoType), recConfig.getTfidfKeywordsNum()));
        }
        return map;
    }

    /**
     * usersTodayBrowsedMap  -->  ItemIDs Set
     */
    private Set<Long> convertToItemIdsSet(Map<Long, List<Long>> map) {
        Set<Long> itemIdsSet = new HashSet<>();
        for (List<Long> itemIds : map.values()) {
            itemIdsSet.addAll(itemIds);
        }
        return itemIdsSet;
    }


}
