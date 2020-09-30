package casia.isiteam.recommendsystem.algorithms.all.cb;

import casia.isiteam.recommendsystem.model.Item;
import casia.isiteam.recommendsystem.model.ItemLog;
import casia.isiteam.recommendsystem.model.User;
import casia.isiteam.recommendsystem.utils.ConfigKit;
import casia.isiteam.recommendsystem.utils.DBKit;
import casia.isiteam.recommendsystem.utils.RecommendKit;
import casia.isiteam.recommendsystem.utils.TFIDF;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.ansj.app.keyword.Keyword;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;

public class UserPrefRefresher {

    private static final Logger logger = LogManager.getLogger(LogManager.ROOT_LOGGER_NAME);

    // TD-IDF提取关键词词数
    private static final int KEY_WORDS_NUM = ConfigKit.getInt("TFIDFKeywordsNum");
    // 偏好衰减系数
    private static final double DECAY_NUM = ConfigKit.getDouble("DecayNum");
    // 关键词阈值
    private static final int PREF_THRESHOLD = ConfigKit.getInt("PrefThreshold");

    public void refresher(List<Long> userIDs, int infoType) {

        // 给没有偏好的用户设定默认偏好
        List<User> users = DBKit.getUserPrefList(userIDs, infoType);
        for (User user : users) {
            if (RecommendKit.getPrefList(user, infoType) == null || RecommendKit.getPrefList(user, infoType).equals(""))
                DBKit.updateUserPrefList(new JSONObject().toJSONString(), user.getId(), infoType);
        }

        // 对用户偏好进行衰减
        decayUserPrefList(userIDs, infoType);

        // 获取当日用户浏览记录  （用户ID - 浏览项ID列表）
        Map<Long, ArrayList<Long>> usersTodayBrowsedMap = getTodayBrowsedMap(userIDs, infoType);
        if (usersTodayBrowsedMap.size() == 0) {
            logger.info("当日无用户浏览记录，用户偏好无需更新。");
            return;
        }

        // 获取当日活跃用户的偏好  （用户ID - 偏好列表 String）
        Map<Long, String> usersPrefListMap = RecommendKit.getUserPreListMap(usersTodayBrowsedMap.keySet(), infoType);
        // 获取  （信息项ID - 信息项关键词列表）
        Map<Long, List<Keyword>> itemsKeywordsMap = getItemsTFIDFMap(usersTodayBrowsedMap, infoType);

        for (Long userID : usersTodayBrowsedMap.keySet()) {
            // 获取用户偏好
            Map<String, Object> map = JSONObject.parseObject(usersPrefListMap.get(userID));
            // 遍历用户浏览过的每个信息项，将关键词和TF-IDF值更新到偏好中
            ArrayList<Long> itemIDs = usersTodayBrowsedMap.get(userID);
            for (Long itemID : itemIDs) {
                List<Keyword> keywords = itemsKeywordsMap.get(itemID);
                if (keywords == null)  continue;
                for (Keyword keyword : keywords) {
                    String word = keyword.getName();
                    if (map.containsKey(word)) {
                        map.put(word, Double.parseDouble(map.get(word).toString()) + keyword.getScore());
                    } else {
                        map.put(word, keyword.getScore());
                    }
                }
            }
            usersPrefListMap.put(userID, JSON.toJSONString(map));
        }

        // 更新偏好到表中
        for (Long userID : usersPrefListMap.keySet()) {
            DBKit.updateUserPrefList(usersPrefListMap.get(userID), userID, infoType);
        }
    }

    /**
     * 对用户偏好进行衰减
     */
    private void decayUserPrefList(List<Long> userIDs, int infoType) {

        List<User> users = DBKit.getUserPrefList(userIDs, infoType);
        for (User user : users) {
            // （关键词 - TF-IDF值）Map
            Map<String, Object> map = JSONObject.parseObject(RecommendKit.getPrefList(user, infoType));
            // 待剔除关键词
            List<String> keywordsToDelete = new ArrayList<>();
            // 计算更新衰减后的TF-IDF值
            for (String keyword : map.keySet()) {
                double result = Double.parseDouble(map.get(keyword).toString()) * DECAY_NUM;
                if (result < PREF_THRESHOLD) {
                    keywordsToDelete.add(keyword);
                } else {
                    map.put(keyword, result);
                }
            }
            // 剔除低于阈值的关键词
            for (String keyword : keywordsToDelete)  map.remove(keyword);
            // 更新衰减后的用户偏好
            DBKit.updateUserPrefList(JSON.toJSONString(map), user.getId(), infoType);
        }
    }

    /**
     * 获取当日有浏览行为的用户ID及其浏览过的ID列表
     */
    private Map<Long, ArrayList<Long>> getTodayBrowsedMap(List<Long> userIDs, int infoType) {
        Map<Long, ArrayList<Long>> map = new HashMap<>();
        List<ItemLog> todayBrowsedList = DBKit.getBrowsedItemsByDate(RecommendKit.getSpecificDayFormat(-1), infoType);
        for (ItemLog itemLog : todayBrowsedList) {
            if (!userIDs.contains(itemLog.getUser_id()))  continue;
            if (!map.containsKey(itemLog.getUser_id()))
                map.put(itemLog.getUser_id(), new ArrayList<>());
            map.get(itemLog.getUser_id()).add(itemLog.getRef_data_id());
        }
        return map;
    }

    /**
     * 获取当日所有被浏览过的信息项出来，并获取关键词及对应TF-IDF值
     */
    private Map<Long, List<Keyword>> getItemsTFIDFMap(Map<Long, ArrayList<Long>> usersTodayBrowsedMap, int infoType) {
        Map<Long, List<Keyword>> map = new HashMap<>();
        List<Item> itemList = DBKit.getItemsByIDs(convertToItemIDsSet(usersTodayBrowsedMap), infoType);
        for (Item item : itemList) {
            map.put(RecommendKit.getItemId(item, infoType), TFIDF.getKeywordsByTFIDE(RecommendKit.getItemName(item, infoType), KEY_WORDS_NUM));
        }
        return map;
    }

    /**
     * usersTodayBrowsedMap  -->  ItemIDs Set
     */
    private Set<Long> convertToItemIDsSet(Map<Long, ArrayList<Long>> map) {
        Set<Long> itemIDsSet = new HashSet<>();
        for (ArrayList<Long> itemIDs : map.values()) {
            itemIDsSet.addAll(itemIDs);
        }
        return itemIDsSet;
    }


}
