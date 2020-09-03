package casia.isiteam.recommendsystem.main;

import casia.isiteam.recommendsystem.algorithms.toutiao.cb.ContentBasedRecommender;
import casia.isiteam.recommendsystem.algorithms.toutiao.cf.UserBasedCollaborativeFilteringRecommender;
import casia.isiteam.recommendsystem.algorithms.toutiao.hr.HotRecommender;
import casia.isiteam.recommendsystem.algorithms.toutiao.random.RandomRecommender;
import casia.isiteam.recommendsystem.algorithms.wiki.cb.WikiContentBasedRecommender;
import casia.isiteam.recommendsystem.algorithms.wiki.cf.WikiUserBasedCollaborativeFilteringRecommender;
import casia.isiteam.recommendsystem.algorithms.wiki.hr.WikiHotRecommender;
import casia.isiteam.recommendsystem.algorithms.wiki.random.WikiRandomRecommender;
import casia.isiteam.recommendsystem.utils.DBKit;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Date;
import java.util.List;

/**
 * 设定/启动推荐任务的类
 */
public class RecommendJobSetter {

    private static final Logger logger = LogManager.getLogger(LogManager.ROOT_LOGGER_NAME);

    boolean isEnableCF, isEnableCB, isEnableHR, isEnableRR;
    int infoType;

    /**
     * 构造方法
     * @param isEnableCF 是否启用协同过滤
     * @param isEnableCB 是否启用基于内容推荐
     * @param isEnableHR 是否启用热点推荐
     * @param isEnableRR 是否启用随机推荐
     * @param infoType 头条 1，百科 2
     */
    public RecommendJobSetter(boolean isEnableCF, boolean isEnableCB, boolean isEnableHR, boolean isEnableRR, int infoType) {
        // 初始化算法选择
        this.isEnableCF = isEnableCF;
        this.isEnableCB = isEnableCB;
        this.isEnableHR = isEnableHR;
        this.isEnableRR = isEnableRR;
        this.infoType = infoType;
    }

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

        /* 头条推荐 */
        if (infoType == 1) {

            if (isEnableCF) {
                new UserBasedCollaborativeFilteringRecommender().recommend(userIDs);
                logger.info("===================== 基于用户的协同过滤方法 推荐完成！ =====================");
            }
            if (isEnableCB) {
                new ContentBasedRecommender().recommend(userIDs);
                logger.info("===================== 基于内容推荐方法 推荐完成！ =====================");
            }
            if (isEnableHR) {
                HotRecommender.formTopHotItemList();
                new HotRecommender().recommend(userIDs);
                logger.info("===================== 基于热点推荐方法 推荐完成！ =====================");
            }
            if (isEnableRR) {
                new RandomRecommender().recommend(userIDs);
                logger.info("===================== 随机补充推荐方法 推荐完成！ =====================");
            }
        }

        /* 百科推荐 */
        else if (infoType == 2) {

            if (isEnableCF) {
                new WikiUserBasedCollaborativeFilteringRecommender().recommend(userIDs);
                logger.info("===================== 基于用户的协同过滤方法 推荐完成！ =====================");
            }
            if (isEnableCB) {
                new WikiContentBasedRecommender().recommend(userIDs);
                logger.info("===================== 基于内容推荐方法 推荐完成！ =====================");
            }
            if (isEnableHR) {
                WikiHotRecommender.formTopHotWikiItemList();
                new WikiHotRecommender().recommend(userIDs);
                logger.info("===================== 基于热点推荐方法 推荐完成！ =====================");
            }
            if (isEnableRR) {
                new WikiRandomRecommender().recommend(userIDs);
                logger.info("===================== 随机补充推荐方法 推荐完成！ =====================");
            }
        }

        logger.info("本次推荐结束于 " + new Date());
    }
}
