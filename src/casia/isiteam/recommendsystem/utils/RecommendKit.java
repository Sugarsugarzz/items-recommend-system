package casia.isiteam.recommendsystem.utils;

import casia.isiteam.recommendsystem.model.NewsLog;
import casia.isiteam.recommendsystem.model.Recommendation;
import com.jfinal.plugin.activerecord.DbKit;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

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
