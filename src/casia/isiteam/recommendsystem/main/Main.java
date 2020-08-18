package casia.isiteam.recommendsystem.main;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * 推荐系统入口类
 */
public class Main {

    private static final Logger logger = LogManager.getLogger(LogManager.ROOT_LOGGER_NAME);

    public static void main(String[] args) {

        /*
        选择在推荐系统中需要运行的推荐算法：
            CF - 协同过滤 Collaborative Filtering
            CB - 基于内容推荐 Content-Based
            HR - 基于热点头条推荐 Hot Recommendation
         */
        boolean enableCF = true;
        boolean enableCB = true;
        boolean enableHR = false;

        // 为所有用户执行定时推荐


        // 为所有用户执行一次推荐
        new JobSetter(enableCF, enableCB, enableHR).executeInstantJobForAllUsers();

    }
}
