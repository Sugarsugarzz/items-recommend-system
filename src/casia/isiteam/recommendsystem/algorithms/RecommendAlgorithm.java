package casia.isiteam.recommendsystem.algorithms;

import casia.isiteam.recommendsystem.utils.DBKit;

import java.util.List;

public interface RecommendAlgorithm {

    // 基于用户的协同过滤
    int CF = 0;
    // 基于内容推荐
    int CB = 1;
    // 基于热点推荐
    int HR = 2;
    // 随机推荐
    int RR = 3;

    /**
     * 针对所有用户返回推荐结果
     */
    default void recommend() {
        recommend(DBKit.getAllUserIDs());
    }

    /**
     * 针对特定用户返回推荐结果
     * @param userIDs 用户ID列表
     */
    void recommend(List<Long> userIDs);
}
