package casia.isiteam.recommendsystem.main;

import casia.isiteam.recommendsystem.algorithms.cf.UserBasedCollaborativeFilteringRecommender;
import casia.isiteam.recommendsystem.algorithms.hr.HotRecommender;
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

    private boolean enableCF;
    private boolean enableCB;
    private boolean enableHR;

    /**
     * 构造方法
     * @param enableCF 是否启动协同过滤
     * @param enableCB 是否启动基于内容推荐
     * @param enableHR 是否启动热点头条推荐
     */
    public JobSetter(boolean enableCF, boolean enableCB, boolean enableHR) {
        // 加载配置文件
        ConfigGetKit.loadProperties("config");
        // 初始化算法选择
        this.enableCF = enableCF;
        this.enableCB = enableCB;
        this.enableHR = enableHR;
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
        // 先用热点新闻推荐器生成今日的热点新闻
        HotRecommender.formTodayTopHotNewsList();

        if (enableCF) {
            new UserBasedCollaborativeFilteringRecommender().recommend(userIDs);
            logger.info("基于用户的协同过滤方法 推荐完成！");
        }
        if (enableCB)
            logger.info("基于内容推荐方法 推荐完成！");
        if (enableHR) {
            new HotRecommender().recommend(userIDs);
            logger.info("基于热点推荐方法 推荐完成！");
        }


        logger.info("本次推荐结束于 " + new Date());
    }
}
