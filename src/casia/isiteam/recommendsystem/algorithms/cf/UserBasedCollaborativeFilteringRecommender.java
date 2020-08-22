package casia.isiteam.recommendsystem.algorithms.cf;

import casia.isiteam.recommendsystem.algorithms.RecommendAlgorithm;
import casia.isiteam.recommendsystem.model.NewsLog;
import casia.isiteam.recommendsystem.utils.ConfigGetKit;
import casia.isiteam.recommendsystem.utils.DBKit;
import casia.isiteam.recommendsystem.utils.RecommendKit;
import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.impl.model.jdbc.MySQLBooleanPrefJDBCDataModel;
import org.apache.mahout.cf.taste.impl.neighborhood.NearestNUserNeighborhood;
import org.apache.mahout.cf.taste.impl.recommender.GenericUserBasedRecommender;
import org.apache.mahout.cf.taste.impl.similarity.LogLikelihoodSimilarity;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.neighborhood.UserNeighborhood;
import org.apache.mahout.cf.taste.recommender.RecommendedItem;
import org.apache.mahout.cf.taste.recommender.Recommender;
import org.apache.mahout.cf.taste.similarity.UserSimilarity;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 基于用户的协同过滤推荐算法实现
 */
public class UserBasedCollaborativeFilteringRecommender implements RecommendAlgorithm {

    private static final Logger logger = LogManager.getLogger(LogManager.ROOT_LOGGER_NAME);

    // 计算用户相似度时的时效天数
    private static int recValidDays = ConfigGetKit.getInt("CFValidDay");
    // 利用协同过滤算法给每个用户推荐的新闻条数
    private static int recNum = ConfigGetKit.getInt("CFRecNum");


    /**
     * CF算法 推荐主函数
     * @param userIDs 用户ID列表
     */
    @Override
    public void recommend(List<Long> userIDs) {

        logger.info("基于用户的协同过滤推荐 start at " + new Date());
        // 统计利用 CF算法 推荐的新闻数量
        int count = 0;

        try {
            DataModel dataModel = getDataModel();
            // 获取所有用户浏览历史
            List<NewsLog> newsLogList = DBKit.getAllNewsLogs();
            // 移除过期的用户浏览行为，这些行为对于计算用户相似度不具备价值
            for (NewsLog newsLog : newsLogList) {
                if (newsLog.getView_time().before(RecommendKit.getInRecTimestamp(recValidDays))) {
                    dataModel.removePreference(newsLog.getUser_id(), newsLog.getNews_id());
                }
            }
            // 指定用户相似度计算方法，这里采用对数似然相似度
            UserSimilarity similarity = new LogLikelihoodSimilarity(dataModel);
            // 指定最近邻用户数量，这里为5
            UserNeighborhood neighborhood = new NearestNUserNeighborhood(5, similarity, dataModel);
            // 构建推荐系统
            Recommender recommender = new GenericUserBasedRecommender(dataModel, neighborhood, similarity);

            for (Long userID : userIDs) {

                // 获取算法为每个用户的推荐项
                List<RecommendedItem> recItems = recommender.recommend(userID, recNum);
                System.out.println("用户ID：" + userID + "\n本次协同过滤为用户生成：" + recItems.size() + " 条");

                // 初始化最终推荐新闻列表
                Set<Long> toBeRecommended = new HashSet<>();

                for (RecommendedItem recItem : recItems) {
                    toBeRecommended.add(recItem.getItemID());
                }

                // 过滤已推荐过的新闻
                RecommendKit.filterRecommendedNews(toBeRecommended, userID);
                // 过滤已过期的新闻
                // RecommendKit.filterOutDateNews(toBeRecommended, userID);

                // 无可推荐的新闻
                if (toBeRecommended.size() == 0)
                    continue;

                // 推荐新闻数量超出协同过滤推荐最多新闻数量
                if (toBeRecommended.size() > recNum) {
                    RecommendKit.removeOverSizeNews(toBeRecommended, recNum);
                }

                // 将本次推荐的结果，存入表中
                RecommendKit.insertRecommendations(userID, toBeRecommended, RecommendAlgorithm.CF);

                System.out.println("================================================");
                count += toBeRecommended.size();
            }

        } catch (TasteException e) {
            logger.error("协同过滤算法 构建偏好对象失败！");
        }

        logger.info("基于用户的协同过滤推荐 has contributed " + (userIDs.size() == 0 ? 0 : count/userIDs.size()) + " recommendations on average");
        logger.info("基于用户的协同过滤推荐 end at " + new Date());

    }

    /**
     * 生成 DataSource，用于构建协同过滤模型
     * 需要一张 newslogs 表，记录用户的浏览历史
     * user_id 用户
     * news_id 对应 item_id  新闻id
     * view_time 浏览时间戳
     */
    private static MySQLBooleanPrefJDBCDataModel getDataModel() {
        // 这步复杂了，后面可以的话改成直接用Mybatis获取DataSource
        // Warning: You are not using ConnectionPoolDataSource. 需要给userID和itemID添加索引，否则会导致速度慢。（啊啊啊又不是不能用）
        MysqlDataSource dataSource = new MysqlDataSource();
        dataSource.setServerName("192.168.10.231");
        dataSource.setPort(3307);
        dataSource.setUser("bj");
        dataSource.setPassword("bj2016");
        dataSource.setDatabaseName("zs_recommend");
        return new MySQLBooleanPrefJDBCDataModel(dataSource, "newslogs", "user_id", "news_id", "view_time");
    }

    public static void main(String[] args) {
        new UserBasedCollaborativeFilteringRecommender().recommend();
    }
}
