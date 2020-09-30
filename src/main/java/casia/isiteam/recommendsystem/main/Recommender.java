package casia.isiteam.recommendsystem.main;


import casia.isiteam.recommendsystem.algorithms.all.cb.ContentBasedRecommender;
import casia.isiteam.recommendsystem.algorithms.all.cf.UserBasedCollaborativeFilteringRecommender;
import casia.isiteam.recommendsystem.algorithms.all.hr.HotRecommender;
import casia.isiteam.recommendsystem.algorithms.all.latest.LatestRecommender;
import casia.isiteam.recommendsystem.algorithms.all.random.RandomRecommender;
import casia.isiteam.recommendsystem.utils.DBKit;
import casia.isiteam.recommendsystem.utils.RecommendKit;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;

/**
 * 设定/启动推荐任务的类
 */
public class Recommender {

    private static final Logger logger = LogManager.getLogger(LogManager.ROOT_LOGGER_NAME);

    // 推荐结果列表
    public static Map<Long, Map<Integer, Set<Long>>> toBeRecommended = new HashMap<>();
    public static List<long[]> defaultCandidates = new ArrayList<>();
    public static List<long[]> latestCandidates = new ArrayList<>();
    // 词条类型
    public static List<Integer> infoTypes = new ArrayList<>(Arrays.asList(1, 2, 3, 4, 5));


    /**
     * 为特定用户执行一次推荐
     * @param userIDs 特定用户ID列表
     */
    public void executeInstantJobForCertainUsers(List<Long> userIDs) {
        executeInstantJob(userIDs);
    }

    /**
     * 为所有用户执行一次推荐
     */
    public void executeInstantJobForAllUsers() {
        executeInstantJob(DBKit.getAllUserIDs());
    }

    /**
     * 执行一次推荐
     * @param userIDs 用户id列表
     */
    private void executeInstantJob(List<Long> userIDs) {

        // 清除时效外的推荐项
        DBKit.deleteRecommendationByDate(RecommendKit.getInRecDate());
        // 清空推荐项
        RecommendKit.emptyRecommendations();

        // 生成推荐项
        HotRecommender.formHotItems();
        LatestRecommender.formLatestItems();
        RandomRecommender.formRandomItems();
        for (Integer infoType : infoTypes) {
            new UserBasedCollaborativeFilteringRecommender().recommend(userIDs, infoType);
            new ContentBasedRecommender().recommend(userIDs, infoType);
            new HotRecommender().recommend(userIDs, infoType);
            new RandomRecommender().recommend(userIDs, infoType);
            new LatestRecommender().recommend(userIDs, infoType);
        }
        // 默认推荐项
        logger.info("正在生成 默认推荐项...");
        DBKit.deleteDefaultRecommendationByDate(RecommendKit.getInRecDate());
        Collections.shuffle(defaultCandidates);
        DBKit.saveDefaultRecommendation(defaultCandidates);
        Collections.shuffle(latestCandidates);
        DBKit.saveDefaultRecommendation(latestCandidates);

        // 存推荐结果  混排！
        int count = 0;
        for (Long userID : toBeRecommended.keySet()) {
            logger.info(String.format("正在生成 推荐项 第 %d/%d 个用户（UserID：%d） ...", ++count, toBeRecommended.keySet().size(), userID));
            List<long[]> candidates = new ArrayList<>();
            for (Integer infoType : toBeRecommended.get(userID).keySet()) {
                RecommendKit.filterBrowsedItems(Recommender.toBeRecommended.get(userID).get(infoType), userID, infoType);
                for (Long itemID : Recommender.toBeRecommended.get(userID).get(infoType)) {
                    candidates.add(new long[] {itemID, infoType});
                }
            }
            Collections.shuffle(candidates);
            // 存入
            DBKit.saveRecommendation(userID, candidates);
        }

        logger.info("本次推荐结束于 " + new Date());
    }
}
