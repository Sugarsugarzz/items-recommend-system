package casia.isiteam.recommendsystem.main;

import casia.isiteam.recommendsystem.utils.DBKit;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Date;
import java.util.List;

/**
 * 设定/启动推荐任务的类
 */
public class JobSetter {

    private static Logger logger = LogManager.getLogger(LogManager.ROOT_LOGGER_NAME);

    private boolean enableCF, enableCB, enableHR;

    /**
     * 构造方法
     * @param enableCF 是否启动协同过滤
     * @param enableCB 是否启动基于内容推荐
     * @param enableHR 是否启动热点头条推荐
     */
    public JobSetter(boolean enableCF, boolean enableCB, boolean enableHR) {
        this.enableCF = enableCF;
        this.enableCB = enableCB;
        this.enableHR = enableHR;
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
        // HotRecommender

        if (enableCF)
            logger.info("协同过滤方法 推荐完成！");
        if (enableCB)
            logger.info("基于内容推荐方法 推荐完成！");
        if (enableHR)
            logger.info("基于热点推荐方法 推荐完成！");

        logger.info("本次推荐结束于 " + new Date());

    }
}
