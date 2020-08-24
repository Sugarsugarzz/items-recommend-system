package casia.isiteam.recommendsystem.algorithms.random;

import casia.isiteam.recommendsystem.algorithms.RecommendAlgorithm;
import casia.isiteam.recommendsystem.model.News;
import casia.isiteam.recommendsystem.utils.ConfigGetKit;
import casia.isiteam.recommendsystem.utils.DBKit;
import casia.isiteam.recommendsystem.utils.RecommendKit;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Timestamp;
import java.util.*;

/**
 * 冷冷启动，无用户浏览记录，其他三种算法推荐数量均较少时，从各领域选取最新新闻作为补充
 */
public class RandomRecommender implements RecommendAlgorithm {

    private static final Logger logger = LogManager.getLogger(LogManager.ROOT_LOGGER_NAME);

    // 热点新闻的有效时间
    private static final int beforeDays = ConfigGetKit.getInt("randomBeforeDays");
    // 其他三种算法推荐数量不够（一般冷启动），使用随机补充推荐
    private static final int totalRecNum = ConfigGetKit.getInt("totalNum");

    /**
     * RR 算法 推荐主函数
     * @param userIDs 用户ID列表
     */
    @Override
    public void recommend(List<Long> userIDs) {

        logger.info("基于随机推荐 start at " + new Date());
        // 统计利用 RR算法 推荐的新闻数量
        int count = 0;

        // 获取当日时间戳
        Timestamp todayTimestamp = RecommendKit.getCertainTimestamp(0, 0, 0);
        for (Long userID : userIDs) {
            // 获取当日已经用其他三种算法为当前用户推荐的数量，若数量达不到推荐总数要求，则随机补充推荐
            long todayRecCount = DBKit.getUserTodayRecommendationCount(todayTimestamp, userID);
            System.out.println("用户ID：" + userID + "\n当日已向该用户推荐新闻： " + todayRecCount + " 条");

            // 计算差值（即需要用RR算法推荐的新闻数量）
            int delta = totalRecNum - (int) todayRecCount;
            System.out.println("需要随机算法补充的新闻数量为： " + delta + " 条");

            // 获取时效内各领域的新闻
            List<News> newsList = DBKit.getGroupNewsByPublishTime(RecommendKit.getInRecDate(beforeDays));

            // 初始化最终推荐新闻列表
            Set<Long> toBeRecommended = new HashSet<>();
            if (delta > 0) {
                int i = Math.min(delta, newsList.size());
                while (i-- > 0)
                    toBeRecommended.add(newsList.get(i).getId());
            }

            // 过滤用户已浏览的新闻
            RecommendKit.filterBrowsedNews(toBeRecommended, userID);
            // 过滤已推荐过的新闻
            RecommendKit.filterRecommendedNews(toBeRecommended, userID);
            // 将本次推荐的新闻，存入表中
            RecommendKit.insertRecommendations(userID, toBeRecommended, RecommendAlgorithm.RR);
            logger.info("成功推荐列表：" + toBeRecommended);

            System.out.println("================================================");
            count += toBeRecommended.size();
        }

        logger.info("基于随机推荐 has contributed " + (userIDs.size() == 0 ? 0 : count/userIDs.size()) + " recommendations on average");
        logger.info("基于随机推荐 end at " + new Date());

    }
}