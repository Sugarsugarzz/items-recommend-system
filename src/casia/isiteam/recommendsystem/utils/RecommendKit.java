package casia.isiteam.recommendsystem.utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collection;

/**
 * 供算法调用的工具方法
 */
public class RecommendKit {

    private static final Logger logger = LogManager.getLogger(LogManager.ROOT_LOGGER_NAME);

    /**
     * 在当日基础上增加/减少天数后的日期字符串
     * @param beforeDays 增加/减少的天数
     * @return yyyy-MM-dd 日期字符串
     */
    public static String getInRecDate(int beforeDays) {
        return getSpecificDayFormat(beforeDays);
    }

    /**
     * 过滤用户已经看过的新闻
     * @param recNewsList 新闻推荐列表
     * @param userID 用户ID
     */
    public static void filterBrowsedNews(Collection<Long> recNewsList, Long userID) {
        // 未实现
    }

    /**
     * 过滤已向用户推荐过的新闻
     * @param recNewsList 新闻推荐列表
     * @param userID 用户ID
     */
    public static void filterRecommendedNews(Collection<Long> recNewsList, Long userID) {
        // 未实现
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
}
