package casia.isiteam.recommendsystem.common;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * 读取 .yml 中的推荐配置参数
 */
@Component
@Data
public class RecConfig {

    /**
     * 已推荐时效性（时效外的已推荐项会删除）
     */
    @Value("${rec-config.recommend-before-days}")
    private Integer recommendBeforeDays;

    /**
     * -- Collaborative Filtering Recommendation --
     * 利用协同过滤算法每次推荐的信息项数量
     */
    @Value("${rec-config.cf-recommend-num}")
    private Integer cfRecommendNum;

    /**
     * 计算用户相似度时的浏览历史时效
     */
    @Value("${rec-config.cf-valid-days}")
    private Integer cfValidDays;

    /**
     * -- Content-based Recommendation --
     * 利用基于内容推荐算法每次推荐的信息项数量
     */
    @Value("${rec-config.cb-recommend-num}")
    private Integer cbRecommendNum;

    /**
     * TF-IDF提取关键词数量
     */
    @Value("${rec-config.tfidf-keywords-num}")
    private Integer tfidfKeywordsNum;

    /**
     * 偏好关键词删除阈值
     */
    @Value("${rec-config.pref-threshold}")
    private Integer prefThreshold;

    /**
     * 每日偏好衰减系数
     */
    @Value("${rec-config.decay-num}")
    private Double decayNum;

    /**
     * -- Hot Recommendation --
     * 热点信息项的时效性
     */
    @Value("${rec-config.hot-before-days}")
    private Integer hotBeforeDays;

    /**
     * 热点信息项的推荐数量
     */
    @Value("${rec-config.hot-recommend-num}")
    private Integer hotRecommendNum;

    /**
     * -- Random Recommendation --
     * 随机推荐信息项的时效性
     */
    @Value("${rec-config.random-recommend-num}")
    private Integer randomRecommendNum;

    /**
     * -- Latest Recommendation --
     * 随机信息项的推荐数量
     */
    @Value("${rec-config.latest-recommend-num}")
    private Integer latestRecommendNum;
}
