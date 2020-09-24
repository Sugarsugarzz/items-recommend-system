package casia.isiteam.recommendsystem.algorithms.all.random;

import casia.isiteam.recommendsystem.algorithms.RecommendAlgorithm;
import casia.isiteam.recommendsystem.main.Recommender;
import casia.isiteam.recommendsystem.model.Item;
import casia.isiteam.recommendsystem.utils.ConfigKit;
import casia.isiteam.recommendsystem.utils.DBKit;
import casia.isiteam.recommendsystem.utils.RecommendKit;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;

public class RandomRecommender implements RecommendAlgorithm {

    private static final Logger logger = LogManager.getLogger(LogManager.ROOT_LOGGER_NAME);
    // 推荐总数量
    private static final int Rec_Num = ConfigKit.getInt("RandomRecommendNum");
    private static Map<Integer, List<Long>> randomItemsMap = new HashMap<>();

    static {
        // 获取每个类型的随机项
        logger.info("正在生成 随机项....");
        for (Integer infoType : Recommender.infoTypes) {
            randomItemsMap.put(infoType, new ArrayList<>());
            List<Long> randomItemIDs = DBKit.getRandomItemsByInfoType(infoType, Rec_Num);
            randomItemsMap.get(infoType).addAll(randomItemIDs);
            // 生成默认推荐项
            for (Long itemID : randomItemIDs) {
                Recommender.defaultCandidates.add(new long[] {itemID, infoType});
            }
        }
        logger.info("生成 随机项 完毕！");
    }

    @Override
    public void recommend(List<Long> userIDs, int infoType) {

        logger.info(infoType + "  基于随机推荐 开始于 " + new Date());
        for (Long userID : userIDs) {
            // 初始化
            RecommendKit.initToBeRecommended(userID, infoType);
            // 添加生成的推荐词条项
            int i = Math.min(Rec_Num, randomItemsMap.get(infoType).size());
            while (i-- > 0) {
                Recommender.toBeRecommended.get(userID).get(infoType).add(randomItemsMap.get(infoType).get(i));
            }
        }
        logger.info(infoType + "  基于随机推荐 结束于 " + new Date());
    }
}
