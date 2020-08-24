package casia.isiteam.recommendsystem.main;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

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
        boolean isEnableCF = true;
        boolean isEnableCB = true;
        boolean isEnableHR = true;

        logger.info("算法启用状态："
                + "基于用户的协同过滤 - " + isEnableCF + " | "
                + "基于内容推荐 - " + isEnableCB + " | "
                + "基于热点头条推荐 - " + isEnableHR);

        // 为所有用户执行一次推荐
        new JobSetter(isEnableCF, isEnableCB, isEnableHR).executeInstantJobForAllUsers();

        // 为特定用户执行一次推荐
//        List<Long> users = new ArrayList<>();
//        users.add(1L);
//        users.add(2L);
//        new JobSetter(isEnableCF, isEnableCB, isEnableHR).executeInstantJobForCertainUsers(users);

    }
}
