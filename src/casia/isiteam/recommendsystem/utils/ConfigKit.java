package casia.isiteam.recommendsystem.utils;

/**
 常量配置
 */
public class ConfigKit {

    /*
    RecommendKit
     */
    // 已推荐时效性（时效内的信息项可以重复推荐）
    public static final int RecommendBeforeDays = -20;

    // 每次向用户推荐的信息项总数
    public static final int TotalNum = 80;

    /*
    Collaborative Filtering Recommendation
     */
    // 利用协同过滤算法每次推荐的信息项数量
    public static final int CFRecommendNum = 30;

    // 计算用户相似度时的时效天数
    public static final int CFValidDays = -30;

    /*
    Content-based Recommendation
     */
    // 利用基于内容推荐算法每次推荐的信息项数量
    public static final int CBRecommendNum = 30;

    // TF-IDF提取关键词数量
    public static final int TFIDFKeywordsNum = 10;

    // 每日偏好衰减系数
    public static final double decayNum = 0.7;

    /*
    Hot Recommendation
     */
    // 热点信息项的时效性
    public static final int HotBeforeDays = -10;

    /*
    Random Recommendation
     */
    // 随机推荐信息项的时效性
    public static final int RandomBeforeDays = -10;



}
