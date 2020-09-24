package casia.isiteam.recommendsystem.main;

import java.util.ArrayList;
import java.util.List;

/**
 * 推荐系统入口类
 */
public class Main {

    public static void main(String[] args) {

        // 为所有用户执行一次推荐
        new Recommender().executeInstantJobForAllUsers();

        // 为特定用户执行一次推荐
//        List<Long> users = new ArrayList<>();
//        users.add(4L);
//        new Recommender().executeInstantJobForCertainUsers(users);

    }
}
