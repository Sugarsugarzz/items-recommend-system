package casia.isiteam.recommendsystem.main;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.Date;
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
            HR - 基于热点推荐 Hot Recommendation
            RR - 随机推荐 Random Recommendation  冷启动随机补充推荐
            flag -  头条 1，百科  2
         */

        boolean isEnableCF = false;
        boolean isEnableCB = true;
        boolean isEnableHR = false;
        boolean isEnableRR = false;
        int flag = 2;

        logger.info("算法启用状态："
                + "基于用户的协同过滤 - " + isEnableCF + " | "
                + "基于内容推荐 - " + isEnableCB + " | "
                + "基于热点推荐 - " + isEnableHR + " | "
                + "基于随机推荐 - " + isEnableRR + "   "
                + "at " + new Date());

        // 为所有用户执行一次推荐
//        new JobSetter(isEnableCF, isEnableCB, isEnableHR, isEnableRR, flag).executeInstantJobForAllUsers();

        // 为特定用户执行一次推荐
        List<Long> users = new ArrayList<>();
//        users.add(1L);
        users.add(2L);
        new JobSetter(isEnableCF, isEnableCB, isEnableHR, isEnableRR, flag).executeInstantJobForCertainUsers(users);

    }
}
