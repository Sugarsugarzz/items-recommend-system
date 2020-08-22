package casia.isiteam.recommendsystem.utils;

import casia.isiteam.recommendsystem.model.NewsLog;
import casia.isiteam.recommendsystem.model.Recommendation;
import casia.isiteam.recommendsystem.model.User;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 供算法调用的工具方法
 */
public class RecommendKit {

    private static final Logger logger = LogManager.getLogger(LogManager.ROOT_LOGGER_NAME);

    // 推荐新闻的时效性天数，即从推荐当天开始到之前的 beforeDays 天的新闻仍然具有时效性，予以推荐
    private static final int beforeDays = ConfigGetKit.getInt("recommendBeforeDays");

    /**
     * 在当日基础上增加/减少天数后的日期字符串
     * @return yyyy-MM-dd 日期字符串
     */
    public static String getInRecDate() {
        return getSpecificDayFormat(beforeDays);
    }

    /**
     * 在当日基础上增加/减少天数后的日期字符串
     * @param beforeDays 增加/减少的天数
     * @return yyyy-MM-dd 日期字符串
     */
    public static String getInRecDate(int beforeDays) {
        return getSpecificDayFormat(beforeDays);
    }

    /**
     * getInRecDate 方法的具体实现
     */
    public static String getSpecificDayFormat(int beforeDays) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, beforeDays);  // 增加/减少天数，取决beforeDays的正负
        return sdf.format(calendar.getTime());
    }

    /**
     * 在当日基础上增加/减少天数后的日期时间戳，便于推荐算法在比较时间前后时调用
     * @param beforeDays 增加/减少的天数
     * @return 日期时间戳
     */
    public static Timestamp getInRecTimestamp(int beforeDays) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, beforeDays);
        return new Timestamp(calendar.getTime().getTime());
    }

    /**
     * 获取 用户与偏好列表 的键值对
     * @param userIDs 用户ID列表
     */
    public static Map<Long, String> getUserPreListMap(Collection<Long> userIDs) {
        Map<Long, String> map = new HashMap<>();
        List<User> users = DBKit.getUserPrefList(userIDs);
        for (User user : users) {
            map.put(user.getId(), user.getPref_list());
        }

        return map;
    }

    /**
     * 过滤用户已经看过的新闻
     * @param recNewsList 新闻推荐列表
     * @param userID 用户ID
     */
    public static void filterBrowsedNews(Collection<Long> recNewsList, Long userID) {

        List<NewsLog> userBrowsedNews = DBKit.getUserBrowsedNews(userID);
        for (NewsLog newsLog : userBrowsedNews) {
            System.out.println("用户浏览过的新闻id - " + newsLog.getNews_id() );
            recNewsList.remove(newsLog.getNews_id());
        }
    }

    /**
     * 过滤已向用户推荐过的新闻
     * @param recNewsList 新闻推荐列表
     * @param userID 用户ID
     */
    public static void filterRecommendedNews(Collection<Long> recNewsList, Long userID) {

        List<Recommendation> userRecommendedNews = DBKit.getUserRecommendedNews(userID, getInRecDate());
        for (Recommendation recommendation : userRecommendedNews) {
            System.out.println("已向用户推荐过的新闻id - " + recommendation.getNews_id());
            recNewsList.remove(recommendation.getNews_id());
        }
    }

    /**
     * 去除推荐结果中超出数量限制的部分
     * @param toBeRecommended 推荐候选列表
     * @param recNum 最大推荐数量限制
     */
    public static void removeOverSizeNews(Set<Long> toBeRecommended, int recNum) {

        if (toBeRecommended.size() <= recNum)
            return;

        int i = 0;
        Iterator<Long> iterator = toBeRecommended.iterator();
        while (iterator.hasNext()) {
            if (i >= recNum) {
                iterator.remove();
            }
            iterator.next();
            i++;
        }
    }

    /**
     * 将推荐结果存入 recommendations 表
     * @param userID 用户ID
     * @param recommendNewsIDs 待存入的推荐新闻ID列表
     * @param algorithm_type 标注推荐结果来自哪个推荐算法
     */
    public static void insertRecommendations(Long userID, Collection<Long> recommendNewsIDs, int algorithm_type) {

        for (Long recommendNewsID : recommendNewsIDs) {
            System.out.println("本次向用户推荐的新闻id - " + recommendNewsID);
            DBKit.saveRecommendation(userID, recommendNewsID, algorithm_type);
        }
    }
}
