package casia.isiteam.recommendsystem.common;


import java.util.*;

/**
 * 推荐结果
 */
public class Candidates {

    /**
     * 单轮待推荐项
     */
    public static Map<Long, Map<Integer, Set<Long>>> toBeRecommended = new HashMap<>();

    /**
     * 热点项
     */
    public static Map<Integer, List<Long>> hotItemsMap = new HashMap<>();

    /**
     * 最新项
     */
    public static Map<Integer, List<Long>> latestItemsMap = new HashMap<>();

    /**
     * 随机项
     */
    public static Map<Integer, List<Long>> randomItemsMap = new HashMap<>();

    /**
     * 默认推荐项，存在recommendations_default表
     */
    public static List<long[]> defaultCandidates = new ArrayList<>();

    /**
     * 时效最新的推荐项
     */
    public static List<long[]> latestCandidates = new ArrayList<>();
}
