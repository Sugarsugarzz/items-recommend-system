package casia.isiteam.recommendsystem.algorithms.toutiao.hr;

import casia.isiteam.recommendsystem.algorithms.RecommendAlgorithm;
import casia.isiteam.recommendsystem.utils.ConfigGetKit;
import casia.isiteam.recommendsystem.utils.DBKit;
import casia.isiteam.recommendsystem.utils.RecommendKit;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Timestamp;
import java.util.*;

/**
 * 基于热点信息项推荐算法实现，通常在协同过滤和基于内容推荐结果数量较少时进行数目的补充
 */
public class HotRecommender implements RecommendAlgorithm {

    private static final Logger logger = LogManager.getLogger(LogManager.ROOT_LOGGER_NAME);

    // 热点信息项的有效时间
    private static final int beforeDays = ConfigGetKit.getInt("hotBeforeDays");
    // 推荐系统每日为每位用户生成的推荐结果的总数，当CF与CB算法生成的推荐结果不足此数时，由该算法补充
    private static final int totalRecNum = ConfigGetKit.getInt("totalNum");
    // 将每天生成的 热点信息项ID，按照信息项的热点程度，从高到低放入List
    private static List<Long> topHotItemList = new ArrayList<>();


    /**
     * HR算法 推荐主函数
     * @param userIDs 用户ID列表
     */
    @Override
    public void recommend(List<Long> userIDs) {

        logger.info("基于热点信息项推荐 start at " + new Date());
        // 统计利用 HR算法 推荐的信息项数量
        int count = 0;

        // 获取当日时间戳
        Timestamp todayTimestamp = RecommendKit.getCertainTimestamp(0, 0, 0);
        for (Long userID : userIDs) {
            // 获取当日已经用 CF 和 CB 算法为当前用户推荐的信息项数量，若数量达不到单日最低推荐数量要求，则用热点信息项补充
            long todayRecCount = DBKit.getRecommendationCountByUserAndTime(todayTimestamp, userID, RecommendAlgorithm.TOUTIAO);
            System.out.println("用户ID：" + userID + "\n当日已向该用户推荐信息项： " + todayRecCount + " 条");

            // 计算差值（即需要用HR算法推荐的信息项数量）
            int delta = totalRecNum - (int) todayRecCount;
            System.out.println("需要热点推荐算法补充的信息项数量为： " + delta + " 条");

            // 初始化最终推荐信息项列表
            Set<Long> toBeRecommended = new HashSet<>();
            if (delta > 0) {
                int i = Math.min(delta, topHotItemList.size());
                while (i-- > 0)
                    toBeRecommended.add(topHotItemList.get(i));
            }
            logger.info("本次热点推荐为该用户生成：" + toBeRecommended.size() + " 条");

            // 过滤用户已浏览的信息项
            RecommendKit.filterBrowsedItems(toBeRecommended, userID, RecommendAlgorithm.TOUTIAO);
            // 过滤已推荐过的信息项
            RecommendKit.filterRecommendedItems(toBeRecommended, userID, RecommendAlgorithm.TOUTIAO);
            // 将本次推荐的信息项，存入表中
            RecommendKit.insertRecommendations(userID, toBeRecommended, RecommendAlgorithm.HR, RecommendAlgorithm.TOUTIAO);
            logger.info("本次向用户 " + userID +" 成功推荐：" + toBeRecommended);

            System.out.println("================================================");
            count += toBeRecommended.size();
        }

        logger.info("基于热点信息项推荐 has contributed " + (userIDs.size() == 0 ? 0 : count/userIDs.size()) + " recommendations on average");
        logger.info("基于热点信息项推荐 end at " + new Date());
    }

    /**
     * 生成时效期内的热点信息项列表
     */
    public static void formTopHotItemList() {

        // 清空热点信息项List
        topHotItemList.clear();
        // 根据有效起始日期，获取热点信息项ID列表
        List<Long> hotItemIDs = DBKit.getHotItemIDs(RecommendKit.getInRecDate(beforeDays), RecommendAlgorithm.TOUTIAO);
        // 将ID添加到热点信息项List中
        topHotItemList.addAll(hotItemIDs);
    }

}
