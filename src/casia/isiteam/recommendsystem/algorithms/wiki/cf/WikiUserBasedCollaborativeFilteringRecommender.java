package casia.isiteam.recommendsystem.algorithms.wiki.cf;

import casia.isiteam.recommendsystem.algorithms.RecommendAlgorithm;
import casia.isiteam.recommendsystem.model.ItemLog;
import casia.isiteam.recommendsystem.utils.ConfigGetKit;
import casia.isiteam.recommendsystem.utils.DBKit;
import casia.isiteam.recommendsystem.utils.RecommendKit;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.impl.common.FastByIDMap;
import org.apache.mahout.cf.taste.impl.common.FastIDSet;
import org.apache.mahout.cf.taste.impl.common.LongPrimitiveIterator;
import org.apache.mahout.cf.taste.impl.model.GenericBooleanPrefDataModel;
import org.apache.mahout.cf.taste.impl.model.jdbc.MySQLBooleanPrefJDBCDataModel;
import org.apache.mahout.cf.taste.impl.model.jdbc.ReloadFromJDBCDataModel;
import org.apache.mahout.cf.taste.impl.neighborhood.NearestNUserNeighborhood;
import org.apache.mahout.cf.taste.impl.recommender.GenericUserBasedRecommender;
import org.apache.mahout.cf.taste.impl.similarity.LogLikelihoodSimilarity;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.neighborhood.UserNeighborhood;
import org.apache.mahout.cf.taste.recommender.RecommendedItem;
import org.apache.mahout.cf.taste.recommender.Recommender;
import org.apache.mahout.cf.taste.similarity.UserSimilarity;

import java.util.*;

/**
 * 基于用户的协同过滤推荐算法实现
 */
public class WikiUserBasedCollaborativeFilteringRecommender implements RecommendAlgorithm {

    private static final Logger logger = LogManager.getLogger(LogManager.ROOT_LOGGER_NAME);

    // 计算用户相似度时的时效天数
    private static final int recValidDays = ConfigGetKit.getInt("CFValidDay");
    // 利用协同过滤算法给每个用户推荐的信息项条数
    private static final int recNum = ConfigGetKit.getInt("CFRecNum");


    /**
     * CF算法 推荐主函数
     * @param userIDs 用户ID列表
     */
    @Override
    public void recommend(List<Long> userIDs) {

        logger.info("基于用户的协同过滤推荐 start at " + new Date());
        // 统计利用 CF算法 推荐的信息项数量
        int count = 0;

        try {
            DataModel dataModel = getDataModel();

//            // 获取时效期内所有用户浏览历史记录
//            List<ItemLog> itemLogList = DBKit.getAllItemLogs();
//            System.out.println("获取所有浏览历史记录完成");
//            // 移除过期的用户浏览行为，这些行为对于计算用户相似度不具备价值
//            for (ItemLog itemLog : itemLogList) {
//                System.out.println("处理浏览记录 - " + itemLog.getUser_id() + " - " + itemLog.getRef_data_id());
//                if (itemLog.getInsert_time().before(RecommendKit.getInRecTimestamp(recValidDays))) {
//                    dataModel.removePreference(itemLog.getUser_id(), itemLog.getRef_data_id());
//                }
//            }

            // 指定用户相似度计算方法，这里采用对数似然相似度
            // 惩罚热门产品，利于挖掘长尾产品
            UserSimilarity similarity = new LogLikelihoodSimilarity(dataModel);
            // 指定最近邻用户数量，这里为5
            UserNeighborhood neighborhood = new NearestNUserNeighborhood(2, similarity, dataModel);
            // 构建协同过滤推荐模型
            Recommender recommender = new GenericUserBasedRecommender(dataModel, neighborhood, similarity);

            // 计算用户之间的相似度
//            LongPrimitiveIterator itr = dataModel.getUserIDs();
//            while (itr.hasNext()) {
//                long userID = itr.nextLong();
//                LongPrimitiveIterator otheritr = dataModel.getUserIDs();
//                while (otheritr.hasNext()) {
//                    long otherUserID = otheritr.nextLong();
//                    System.out.println("用户 " + userID + " 与用户 " + otherUserID + " 的相似度为 " + similarity.userSimilarity(userID, otherUserID) );
//                }
//            }


            for (Long userID : userIDs) {

                // 获取算法为每个用户的推荐项
                List<RecommendedItem> recItems = recommender.recommend(userID, recNum);
                System.out.println("用户ID：" + userID + "\n本次协同过滤为该用户生成：" + recItems.size() + " 条");

                // 初始化最终推荐信息项列表
                Set<Long> toBeRecommended = new HashSet<>();

                for (RecommendedItem recItem : recItems) {
                    toBeRecommended.add(recItem.getItemID());
                }

                // 过滤用户已浏览的信息项
                RecommendKit.filterBrowsedItems(toBeRecommended, userID, RecommendAlgorithm.WIKI);
                // 过滤已推荐过的信息项
                RecommendKit.filterRecommendedItems(toBeRecommended, userID, RecommendAlgorithm.WIKI);
                // 推荐信息项数量超出协同过滤推荐最多信息项数量
                RecommendKit.removeOverSizeItems(toBeRecommended, recNum);
                // 将本次推荐的结果，存入表中
                RecommendKit.insertRecommendations(userID, toBeRecommended, RecommendAlgorithm.CF, RecommendAlgorithm.WIKI);
                logger.info("本次向用户 " + userID +" 成功推荐：" + toBeRecommended);

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
     * 需要一张 user_read_record 表，记录用户的浏览历史
     * user_id 用户ID
     * ref_data_id  信息项ID
     * view_time 浏览时间戳
     */
    private static DataModel getDataModel() throws TasteException {
        // TODO
        Map<Long, List<Long>> map = new HashMap<>();
        List<ItemLog> itemLogs = DBKit.getBrowsedItemsByDate(RecommendKit.getInRecDate(recValidDays), RecommendAlgorithm.WIKI);
        for (ItemLog itemLog : itemLogs) {
            if (!map.containsKey(itemLog.getUser_id()))
                map.put(itemLog.getUser_id(), new ArrayList<>());
            map.get(itemLog.getUser_id()).add(itemLog.getRef_data_id());

        }

        FastByIDMap fastByIDMap = new FastByIDMap();
        for (Long userID : map.keySet()) {
            List<Long> list = map.get(userID);
            FastIDSet set = new FastIDSet();
            for (Long aLong : list) {
                set.add(aLong);
            }
            fastByIDMap.put(userID, set);
            System.out.println(fastByIDMap);
        }
        return new GenericBooleanPrefDataModel(fastByIDMap);


//        MysqlDataSource dataSource = new MysqlDataSource();
//        dataSource.setServerName("192.168.10.231");
//        dataSource.setPort(3307);
//        dataSource.setUser("bj");
//        dataSource.setPassword("bj2016");
//        dataSource.setDatabaseName("zbzs");
//        MySQLBooleanPrefJDBCDataModel jdbcDataModel = new MySQLBooleanPrefJDBCDataModel(dataSource, "user_read_record", "user_id", "ref_data_id", "insert_time");
//        return new ReloadFromJDBCDataModel(jdbcDataModel);
    }
}
