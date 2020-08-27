package casia.isiteam.recommendsystem.main;

import casia.isiteam.recommendsystem.algorithms.toutiao.cb.ContentBasedRecommender;
import casia.isiteam.recommendsystem.algorithms.toutiao.cf.UserBasedCollaborativeFilteringRecommender;
import casia.isiteam.recommendsystem.algorithms.toutiao.hr.HotRecommender;
import casia.isiteam.recommendsystem.algorithms.toutiao.random.RandomRecommender;
import casia.isiteam.recommendsystem.algorithms.wiki.cb.WikiContentBasedRecommender;
import casia.isiteam.recommendsystem.algorithms.wiki.cf.WikiUserBasedCollaborativeFilteringRecommender;
import casia.isiteam.recommendsystem.algorithms.wiki.hr.WikiHotRecommender;
import casia.isiteam.recommendsystem.algorithms.wiki.random.WikiRandomRecommender;
import casia.isiteam.recommendsystem.utils.ConfigGetKit;
import casia.isiteam.recommendsystem.utils.DBKit;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Date;
import java.util.List;

/**
 * 设定/启动推荐任务的类
 */
public class JobSetter {

    private static final Logger logger = LogManager.getLogger(LogManager.ROOT_LOGGER_NAME);

    boolean isEnableCF, isEnableCB, isEnableHR, isEnableRR;
    int flag;

    /**
     * 构造方法
     * @param isEnableCF 是否启动协同过滤
     * @param isEnableCB 是否启动基于内容推荐
     * @param isEnableHR 是否启动热点头条推荐
     * @param flag 头条 1，百科 2
     */
    public JobSetter(boolean isEnableCF, boolean isEnableCB, boolean isEnableHR, boolean isEnableRR, int flag) {
        // 加载配置文件
        ConfigGetKit.loadProperties("config");
        // 初始化算法选择
        this.isEnableCF = isEnableCF;
        this.isEnableCB = isEnableCB;
        this.isEnableHR = isEnableHR;
        this.isEnableRR = isEnableRR;
        this.flag = flag;
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
        if (flag == 1) {
            // 先用热点推荐器生成今日的热点信息项
            HotRecommender.formTopHotItemList();

            if (isEnableCF) {
                new UserBasedCollaborativeFilteringRecommender().recommend(userIDs);
                logger.info("基于用户的协同过滤方法 推荐完成！");
            }
            if (isEnableCB) {
                new ContentBasedRecommender().recommend(userIDs);
                logger.info("基于内容推荐方法 推荐完成！");
            }
            if (isEnableHR) {
                new HotRecommender().recommend(userIDs);
                logger.info("基于热点推荐方法 推荐完成！");
            }
            if (isEnableRR) {
                new RandomRecommender().recommend(userIDs);
                logger.info("随机补充推荐方法 推荐完成！");
            }
        }

        /* 百科推荐 */
        else if (flag == 2) {
            // 生成当日热点百科项
            WikiHotRecommender.formTopHotWikiItemList();

            if (isEnableCF) {
                new WikiUserBasedCollaborativeFilteringRecommender().recommend(userIDs);
                logger.info("基于用户的协同过滤方法 推荐完成！");
            }
            if (isEnableCB) {
                new WikiContentBasedRecommender().recommend(userIDs);
                logger.info("基于内容推荐方法 推荐完成！");
            }
            if (isEnableHR) {
                new WikiHotRecommender().recommend(userIDs);
                logger.info("基于热点推荐方法 推荐完成！");
            }
            if (isEnableRR) {
                new WikiRandomRecommender().recommend(userIDs);
                logger.info("随机补充推荐方法 推荐完成！");
            }
        }

        logger.info("本次推荐结束于 " + new Date());
    }
}
