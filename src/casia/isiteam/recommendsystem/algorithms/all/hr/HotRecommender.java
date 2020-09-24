package casia.isiteam.recommendsystem.algorithms.all.hr;

import casia.isiteam.recommendsystem.algorithms.RecommendAlgorithm;
import casia.isiteam.recommendsystem.main.Recommender;
import casia.isiteam.recommendsystem.utils.ConfigKit;
import casia.isiteam.recommendsystem.utils.DBKit;
import casia.isiteam.recommendsystem.utils.RecommendKit;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Timestamp;
import java.util.*;

public class HotRecommender implements RecommendAlgorithm {

    private static final Logger logger = LogManager.getLogger(LogManager.ROOT_LOGGER_NAME);

    private static final int beforeDays = ConfigKit.getInt("HotBeforeDays");
    private static final int Rec_Num = ConfigKit.getInt("HotRecommendNum");
    private static Map<Integer, List<Long>> hotItemsMap = new HashMap<>();

    static {
        // 获取每个类型的热点项
        logger.info("正在生成 热点项....");
        for (Integer infoType : Recommender.infoTypes) {
            hotItemsMap.put(infoType, new ArrayList<>());
            List<Long> hotItemIDs = DBKit.getHotItemIDs(RecommendKit.getInRecDate(beforeDays), infoType, Rec_Num);
            hotItemsMap.get(infoType).addAll(hotItemIDs);
            // 生成默认推荐项
            for (Long itemID : hotItemIDs) {
                Recommender.defaultCandidates.add(new long[] {itemID, infoType});
            }
        }
        logger.info("生成 热点项 完毕！");
    }

    @Override
    public void recommend(List<Long> userIDs, int infoType) {

        logger.info(infoType + "  基于热点推荐 开始于 " + new Date());

        Timestamp todayTimestamp = RecommendKit.getCertainTimestamp(0, 0, 0);
        for (Long userID : userIDs) {
            // 初始化
            RecommendKit.initToBeRecommended(userID, infoType);
            // 添加生成的推荐词条项
            int i = Math.min(Rec_Num, hotItemsMap.get(infoType).size());
            while (i-- > 0)
                Recommender.toBeRecommended.get(userID).get(infoType).add(hotItemsMap.get(infoType).get(i));
        }
        logger.info(infoType + "  基于热点推荐 结束于 " + new Date());
    }
}
