package casia.isiteam.recommendsystem.algorithms.all.cf;

import casia.isiteam.recommendsystem.algorithms.RecommendAlgorithm;
import casia.isiteam.recommendsystem.main.Recommender;
import casia.isiteam.recommendsystem.model.ItemLog;
import casia.isiteam.recommendsystem.utils.ConfigKit;
import casia.isiteam.recommendsystem.utils.DBKit;
import casia.isiteam.recommendsystem.utils.RecommendKit;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.impl.common.FastByIDMap;
import org.apache.mahout.cf.taste.impl.common.FastIDSet;
import org.apache.mahout.cf.taste.impl.model.GenericBooleanPrefDataModel;
import org.apache.mahout.cf.taste.impl.neighborhood.NearestNUserNeighborhood;
import org.apache.mahout.cf.taste.impl.recommender.GenericUserBasedRecommender;
import org.apache.mahout.cf.taste.impl.similarity.LogLikelihoodSimilarity;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.neighborhood.UserNeighborhood;
import org.apache.mahout.cf.taste.recommender.RecommendedItem;
import org.apache.mahout.cf.taste.similarity.UserSimilarity;

import java.util.*;

public class UserBasedCollaborativeFilteringRecommender implements RecommendAlgorithm {

    private static final Logger logger = LogManager.getLogger(LogManager.ROOT_LOGGER_NAME);

    // 计算用户相似度时的时效天数
    private static final int recValidDays = ConfigKit.getInt("CFValidDays");
    // 利用CF推荐的总数
    private static final int recNum = ConfigKit.getInt("CFRecommendNum");


    @Override
    public void recommend(List<Long> userIDs, int infoType) {

        logger.info("信息类型：" + infoType + "  基于用户的协同过滤 开始于 " + new Date());

        try {
            // 建模
            DataModel dataModel = getDataModel(infoType);
            UserSimilarity similarity = new LogLikelihoodSimilarity(dataModel);
            UserNeighborhood neighborhood = new NearestNUserNeighborhood(6, similarity, dataModel);
            org.apache.mahout.cf.taste.recommender.Recommender recommender = new GenericUserBasedRecommender(dataModel, neighborhood, similarity);

            for (Long userID : userIDs) {
                // 获取针对每个用户的推荐项
                List<RecommendedItem> recItems = recommender.recommend(userID, recNum);
                // 初始化
                RecommendKit.initToBeRecommended(userID, infoType);
                // 添加生成的推荐项
                recItems.forEach(recItem ->
                    Recommender.toBeRecommended.get(userID).get(infoType).add(recItem.getItemID())
                );
            }

        } catch (TasteException e) {
            logger.error("协同过滤 构建偏好模型失败！");
        }

        logger.info("信息类型：" + infoType + "  基于用户的协同过滤 结束于 " + new Date());
    }


    /**
     * 根据时效内所有用户的浏览历史，生成DataSourceModel，用于构建协同过滤模型
     */
    private static DataModel getDataModel(int infoType) {

        // 构建 <用户ID> - <浏览项ID List> 的 Map
        Map<Long, List<Long>> map = new HashMap<>();
        List<ItemLog> itemLogs = DBKit.getBrowsedItemsByDate(RecommendKit.getInRecDate(recValidDays), infoType);
        for (ItemLog itemLog : itemLogs) {
            if (!map.containsKey(itemLog.getUser_id()))
                map.put(itemLog.getUser_id(), new ArrayList<>());
            map.get(itemLog.getUser_id()).add(itemLog.getRef_data_id());
        }

        // 构建无偏模型
        FastByIDMap fastByIDMap = new FastByIDMap();
        for (Long userID : map.keySet()) {
            List<Long> list = map.get(userID);
            FastIDSet set = new FastIDSet();
            for (Long aLong : list) {
                set.add(aLong);
            }
            fastByIDMap.put(userID, set);
        }

        return new GenericBooleanPrefDataModel(fastByIDMap);
    }
}
