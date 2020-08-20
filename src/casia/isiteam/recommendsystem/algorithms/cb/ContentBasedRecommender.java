package casia.isiteam.recommendsystem.algorithms.cb;

import casia.isiteam.recommendsystem.algorithms.RecommendAlgorithm;
import casia.isiteam.recommendsystem.utils.ConfigGetKit;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Date;
import java.util.List;

/**
 * 基于内容的推荐算法实现
 *  思路：提取新闻的关键词列表（TF-IDF），以及每个用户的偏好关键词列表，计算关键词相似度计算，取最相似的 N 个新闻推荐给用户。
 *
 */
public class ContentBasedRecommender implements RecommendAlgorithm {

    private static final Logger logger = LogManager.getLogger(LogManager.ROOT_LOGGER_NAME);

    // TFIDF算法提取关键词的次数
    private static final int KEY_WORDS_NUM = ConfigGetKit.getInt("TFIDFKeywordsNum");
    // 利用基于内容推荐算法给每个用户推荐的新闻条数
    private static final int recNum = ConfigGetKit.getInt("CBRecNum");


    /**
     * CB算法 推荐主函数
     * @param userIDs 用户ID列表
     */
    @Override
    public void recommend(List<Long> userIDs) {

        logger.info("基于内容的推荐 start at " + new Date());
        // 统计利用 CB算法 推荐的新闻数量
        int count = 0;

        try {
            // 用户偏好关键词列表衰减更新 + 用户浏览历史记录更新
            new UserPrefRefresher().refresher(userIDs);

            // 未实现






        } catch (Exception e) {
            logger.error("基于内容推荐算法 推荐失败！" + e);
        }

        logger.info("基于内容的推荐 has contributed " + (userIDs.size() == 0 ? 0 : count/userIDs.size()) + " recommendations on average");
        logger.info("基于内容的推荐 end at " + new Date());

    }
}
