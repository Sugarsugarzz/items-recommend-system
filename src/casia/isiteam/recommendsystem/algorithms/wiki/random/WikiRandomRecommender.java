package casia.isiteam.recommendsystem.algorithms.wiki.random;

import casia.isiteam.recommendsystem.algorithms.RecommendAlgorithm;
import casia.isiteam.recommendsystem.model.Item;
import casia.isiteam.recommendsystem.utils.ConfigKit;
import casia.isiteam.recommendsystem.utils.DBKit;
import casia.isiteam.recommendsystem.utils.RecommendKit;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Timestamp;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 冷冷启动，无用户浏览记录，其他三种算法推荐数量均较少时，从wiki_info表中随机取词条作为补充
 */
public class WikiRandomRecommender implements RecommendAlgorithm {

    private static final Logger logger = LogManager.getLogger(LogManager.ROOT_LOGGER_NAME);

    // 热点信息项的有效时间
    private static final int beforeDays = ConfigKit.RandomBeforeDays;
    // 其他三种算法推荐数量不够（一般冷启动），使用随机补充推荐
    private static final int totalRecNum = ConfigKit.TotalNum;

    /**
     * RR 算法 推荐主函数
     * @param userIDs 用户ID列表
     */
    @Override
    public void recommend(List<Long> userIDs) {

        logger.info("基于随机推荐 start at " + new Date());
        // 统计利用 RR算法 推荐的信息项数量
        int count = 0;

        // 获取当日时间戳
        Timestamp todayTimestamp = RecommendKit.getCertainTimestamp(0, 0, 0);
        for (Long userID : userIDs) {

            // 获取当日已经用其他三种算法为当前用户推荐的数量，若数量达不到推荐总数要求，则随机补充推荐
            long todayRecCount = DBKit.getRecommendationCountByUserAndTime(todayTimestamp, userID, RecommendAlgorithm.WIKI);
            logger.info("用户ID：" + userID + "\n当日已向该用户推荐信息项： " + todayRecCount + " 条");

            // 计算差值（即需要用RR算法推荐的信息项数量）
            int delta = totalRecNum - (int) todayRecCount;
            logger.info("需要随机算法补充的信息项数量为： " + delta + " 条");

            // 获取时效内的随机wiki词条项
            List<Item> itemList = DBKit.getGroupWikiItemsByPublishTime(RecommendKit.getInRecDate(beforeDays));

            // 初始化最终推荐信息项列表
            Set<Long> toBeRecommended = new HashSet<>();
            if (delta > 0) {
                int i = Math.min(delta, itemList.size());
                while (i-- > 0)
                    toBeRecommended.add(itemList.get(i).getWiki_info_id());
            }
            logger.info("本次随机推荐为该用户生成：" + toBeRecommended.size() + " 条");

            // 过滤用户已浏览的信息项
            RecommendKit.filterBrowsedItems(toBeRecommended, userID, RecommendAlgorithm.WIKI);
            // 过滤已推荐过的信息项
            RecommendKit.filterRecommendedItems(toBeRecommended, userID, RecommendAlgorithm.WIKI);
            // 将本次推荐的信息项，存入表中
            RecommendKit.insertRecommendations(userID, toBeRecommended, RecommendAlgorithm.RR, RecommendAlgorithm.WIKI);
            logger.info("本次向用户 " + userID +" 成功推荐：" + toBeRecommended);

            count += toBeRecommended.size();
        }

        logger.info("基于随机推荐 has contributed " + (userIDs.size() == 0 ? 0 : count/userIDs.size()) + " recommendations on average");
        logger.info("基于随机推荐 end at " + new Date());

    }
}
