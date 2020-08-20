package casia.isiteam.recommendsystem.algorithms.cb;

import casia.isiteam.recommendsystem.model.News;
import casia.isiteam.recommendsystem.model.NewsLog;
import casia.isiteam.recommendsystem.model.User;
import casia.isiteam.recommendsystem.utils.ConfigGetKit;
import casia.isiteam.recommendsystem.utils.DBKit;
import casia.isiteam.recommendsystem.utils.RecommendKit;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.hadoop.yarn.webapp.hamlet.Hamlet;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;

/**
 * 用户偏好更新器
 * 当用户浏览新的新闻时，对用户的偏好列表（pref_list）进行更新
 */
@SuppressWarnings("unchecked")
public class UserPrefRefresher {

    private static final Logger logger = LogManager.getLogger(LogManager.ROOT_LOGGER_NAME);

    // TFIDF算法提取关键词的次数
    private static final int KEY_WORDS_NUM = ConfigGetKit.getInt("TFIDFKeywordsNum");
    // 用户偏好每日衰减系数
    private static final double decayNum = ConfigGetKit.getDouble("decayNum");

    /**
     * 定期根据用户前一天的浏览记录，对用户的偏好关键词列表TD-IDF值进行衰减后，将用户前一天看的新闻的关键词及TD-IDF值更新到偏好列表中。
     * @param userIDs 用户ID列表
     */
    public void refresher(Collection<Long> userIDs) {

        // 先对用户偏好关键词列表的TD-IDF值进行衰减更新
        decayUserPref(userIDs);

        // 当日用户浏览记录  用户ID - 浏览新闻的ID列表
        Map<Long, ArrayList<Long>> todayBrowsedMap = getTodayBrowsedMap();
        // 仅对当日有浏览记录的用户偏好进行更新；当日无浏览记录的，无需后面的更新步骤
        if (todayBrowsedMap.size() == 0)
            return;

        // 获取当日活跃用户的偏好  用户ID - 偏好列表 pre_list
        Map<Long, String> userPrefListMap = getUserPreListMap(todayBrowsedMap.keySet());

        // 获取 新闻ID - 新闻模块ID 与 新闻ID - 新闻关键词列表（两种）
        Map<String, Object> newsMap = getNewsTFIDFMap();

        // 遍历用户浏览记录，更新用户偏好关键词列表
        // 未实现



        // 将更新好的偏好关键词列表存入表中
        // 未实现


    }

    /**
     * 对用户偏好进行衰减更新 具体实现
     */
    public void decayUserPref(Collection<Long> userIDs) {

        // 获取所有用户的 ID 和 偏好列表 pre_list
        List<User> users = DBKit.getUserPrefList(userIDs);

        for (User user : users) {

            // key - 新闻模块id， value - 关键词偏好列表Map
            Map<String, Object> map = JSONObject.parseObject(user.getPref_list());

            // 待删除的关键词列表
            List<String> keywordsToDelete = new ArrayList<>();

            for (String module : map.keySet()) {
                // key - 关键词， value - TF-IDF值
                Map<String, Object> keywordsMap = (Map<String, Object>) map.get(module);
                // 更新每个关键词衰减后的TF-IDF值
                for (String k : keywordsMap.keySet()) {
                    double result = Double.parseDouble(keywordsMap.get(k).toString()) * decayNum;
                    if (result < 15) {
                        keywordsToDelete.add(k);
                    } else {
                        keywordsMap.put(k, result);
                    }
                }
                // 剔除低于阈值的关键词
                for (String keyword : keywordsToDelete) {
                    keywordsMap.remove(keyword);
                }
            }

            // 更新表中的用户偏好
            DBKit.updateUserPrefList(JSON.toJSONString(map), user.getId());
        }
    }

    /**
     * 提取出当日有浏览行为的用户ID及其浏览过的新闻ID列表
     */
    public Map<Long, ArrayList<Long>> getTodayBrowsedMap() {
        Map<Long, ArrayList<Long>> map = new HashMap<>();

        List<NewsLog> browsedList = DBKit.getTodayBrowsedNews(RecommendKit.getSpecificDayFormat(0));
        for (NewsLog newsLog : browsedList) {
            if (!map.containsKey(newsLog.getUser_id())) {
                map.put(newsLog.getUser_id(), new ArrayList<>());
            }
            map.get(newsLog.getUser_id()).add(newsLog.getNews_id());
        }

        return map;
    }

    /**
     * 获取 用户与偏好列表 的键值对
     * @param userIDs 用户ID列表
     */
    public Map<Long, String> getUserPreListMap(Collection<Long> userIDs) {
        Map<Long, String> map = new HashMap<>();
        List<User> users = DBKit.getUserPrefList(userIDs);
        for (User user : users) {
            map.put(user.getId(), user.getPref_list());
        }

        return map;
    }

    /**
     * 将当日所有被浏览过的新闻提取出来，进行TF-IDF求值操作，并对用户偏好关键词列表进行更新
     */
    public Map<String, Object> getNewsTFIDFMap() {
        Map<String, Object> map = new HashMap<>();

        List<News> newsList = DBKit.getNewsByIDs(getTodayBrowsedNewsSet());
        for (News news : newsList) {
            map.put(String.valueOf(news.getId()), TFIDF.getKeywordsByTFIDE(news.getTitle(), news.getContent(), KEY_WORDS_NUM));
            map.put(news.getId() + "-ModuleID", news.getModule_id());
        }

        return map;
    }

    /**
     * 获取当日浏览的新闻ID集合
     */
    public Set<Long> getTodayBrowsedNewsSet() {
        Set<Long> newsIDsSet = new HashSet<>();
        Map<Long, ArrayList<Long>> map = getTodayBrowsedMap();
        for (ArrayList<Long> newsIDs : map.values()) {
            newsIDsSet.addAll(newsIDs);
        }

        return newsIDsSet;
    }

}
