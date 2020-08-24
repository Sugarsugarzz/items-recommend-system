package casia.isiteam.recommendsystem.algorithms.hr;

import casia.isiteam.recommendsystem.algorithms.RecommendAlgorithm;
import casia.isiteam.recommendsystem.utils.ConfigGetKit;
import casia.isiteam.recommendsystem.utils.DBKit;
import casia.isiteam.recommendsystem.utils.RecommendKit;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Timestamp;
import java.util.*;

/**
 * 基于热点新闻推荐算法实现，通常在协同过滤和基于内容推荐结果数量较少时进行数目的补充
 */
public class HotRecommender implements RecommendAlgorithm {

    private static final Logger logger = LogManager.getLogger(LogManager.ROOT_LOGGER_NAME);

    // 热点新闻的有效时间
    private static final int beforeDays = ConfigGetKit.getInt("hotBeforeDays");
    // 推荐系统每日为每位用户生成的推荐结果的总数，当CF与CB算法生成的推荐结果不足此数时，由该算法补充
    private static final int totalRecNum = ConfigGetKit.getInt("totalNum");
    // 将每天生成的 热点新闻ID，按照新闻的热点程度，从高到低放入List
    private static List<Long> topHotNewsList = new ArrayList<>();


    /**
     * HR算法 推荐主函数
     * @param userIDs 用户ID列表
     */
    @Override
    public void recommend(List<Long> userIDs) {

        logger.info("基于热点新闻推荐 start at " + new Date());
        // 统计利用 HR算法 推荐的新闻数量
        int count = 0;

        // 获取当日时间戳
        Timestamp todayTimestamp = RecommendKit.getCertainTimestamp(0, 0, 0);
        for (Long userID : userIDs) {
            // 获取当日已经用 CF 和 CB 算法为当前用户推荐的新闻数量，若数量达不到单日最低推荐数量要求，则用热点新闻补充
            long todayRecCount = DBKit.getUserTodayRecommendationCount(todayTimestamp, userID);
            System.out.println("用户ID：" + userID + "\n当日已向该用户推荐新闻： " + todayRecCount + " 条");

            // 计算差值（即需要用HR算法推荐的新闻数量）
            int delta = totalRecNum - (int) todayRecCount;
            System.out.println("需要热点推荐算法补充的新闻数量为： " + delta + " 条");

            // 初始化最终推荐新闻列表
            Set<Long> toBeRecommended = new HashSet<>();
            if (delta > 0) {
                int i = Math.min(delta, topHotNewsList.size());
                while (i-- > 0)
                    toBeRecommended.add(topHotNewsList.get(i));
            }

            // 过滤用户已浏览的新闻
            RecommendKit.filterBrowsedNews(toBeRecommended, userID);
            // 过滤已推荐过的新闻
            RecommendKit.filterRecommendedNews(toBeRecommended, userID);
            // 将本次推荐的新闻，存入表中
            RecommendKit.insertRecommendations(userID, toBeRecommended, RecommendAlgorithm.HR);
            logger.info("成功推荐列表：" + toBeRecommended);

            System.out.println("================================================");
            count += toBeRecommended.size();
        }

        logger.info("基于热点新闻推荐 has contributed " + (userIDs.size() == 0 ? 0 : count/userIDs.size()) + " recommendations on average");
        logger.info("基于热点新闻推荐 end at " + new Date());
    }

    /**
     * 生成当日的热点新闻列表
     */
    public static void formTodayTopHotNewsList() {

        // 清空热点新闻List
        topHotNewsList.clear();
        // 根据有效起始日期，获取热点新闻ID列表
        List<Long> hotNewsIDs = DBKit.getHotNewsIDs(RecommendKit.getInRecDate(beforeDays));
        // 将ID添加到热点新闻List中
        topHotNewsList.addAll(hotNewsIDs);
    }

}
