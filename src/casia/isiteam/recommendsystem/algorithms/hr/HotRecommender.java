package casia.isiteam.recommendsystem.algorithms.hr;

import casia.isiteam.recommendsystem.algorithms.RecommendAlgorithm;
import casia.isiteam.recommendsystem.utils.DBKit;
import casia.isiteam.recommendsystem.utils.RecommendKit;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Timestamp;
import java.util.*;

/**
 * 基于热点新闻的推荐，通常在协同过滤和基于内容推荐结果数量较少时进行数目的补充
 */
public class HotRecommender implements RecommendAlgorithm {

    private static final Logger logger = LogManager.getLogger(LogManager.ROOT_LOGGER_NAME);

    // 热点新闻的有效时间
    public static int beforeDays = -10;
    // 推荐系统每日为每位用户生成的推荐结果的总数，当CF与CB算法生成的推荐结果不足此数时，由该算法补充
    public static int TOTAL_REC_NUM = 20;
    // 将每天生成的 热点新闻ID，按照新闻的热点程度，从高到低放入List
    private static List<Long> topHotNewsList = new ArrayList<>();


    /**
     * 推荐主函数
     * @param userIDs 用户ID列表
     */
    @Override
    public void recommend(List<Long> userIDs) {

        logger.info("基于热点新闻推荐 start at " + new Date());
        // 统计利用 hr算法 推荐的新闻数量
        int count = 0;
        Timestamp todayTimestamp = getCertainTimestamp(0, 0, 0);
        for (Long userID : userIDs) {
            // 获取当日已经用CF和CB算法为当前用户推荐的新闻数量，若数量达不到单日最低推荐数量要求，则用热点新闻补充
            long todayRecCount = DBKit.getUserTodayRecommendationCount(todayTimestamp, userID);
            System.out.println("用户ID：" + userID + "\n 当日已向用户推荐新闻： " + todayRecCount + " 条");

            // 计算差值（即需要用hr算法推荐的新闻数量）
            int delta = TOTAL_REC_NUM - (int) todayRecCount;
            System.out.println("需要热点推荐算法补充的新闻数量为： " + delta);

            // 初始化最终推荐新闻列表
            Set<Long> toBeRecommended = new HashSet<>();
            if (delta > 0) {
                int i = Math.min(delta, topHotNewsList.size());
                while (i-- > 0)
                    toBeRecommended.add(topHotNewsList.get(i));
            }

            // 过滤用户已浏览的新闻
            RecommendKit.filterBrowsedNews(toBeRecommended, userID);
            // 过滤已向用户推荐过的新闻
            RecommendKit.filterRecommendedNews(toBeRecommended, userID);
            // 将本次推荐的新闻，存入表中
            RecommendKit.insertRecommendations(userID, toBeRecommended, RecommendAlgorithm.HR);

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


    /**
     * topHotNewsList 的 Getter
     */
    public static List<Long> getTopHotNewsList() {
        return topHotNewsList;
    }


    /**
     * 获取特定的时间戳
     */
    private static Timestamp getCertainTimestamp(int hour, int minute, int second) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, second);
        return new Timestamp(calendar.getTime().getTime());
    }

    public static void main(String[] args) {

        formTodayTopHotNewsList();
        System.out.println("打印一下 tophotnewslist");
        for (Long hot : getTopHotNewsList()) {
            System.out.print(hot + " - ");
        }
        new HotRecommender().recommend(DBKit.getAllUserIDs());



    }

}
