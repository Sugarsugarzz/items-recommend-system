package casia.isiteam.recommendsystem.service.cf;

import casia.isiteam.recommendsystem.common.Candidates;
import casia.isiteam.recommendsystem.common.RecConfig;
import casia.isiteam.recommendsystem.model.ItemLog;
import casia.isiteam.recommendsystem.utils.DBKit;
import casia.isiteam.recommendsystem.utils.RecommendKit;
import cn.hutool.core.util.ObjectUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.mahout.cf.taste.impl.common.FastByIDMap;
import org.apache.mahout.cf.taste.impl.common.FastIDSet;
import org.apache.mahout.cf.taste.impl.model.GenericBooleanPrefDataModel;
import org.apache.mahout.cf.taste.impl.neighborhood.NearestNUserNeighborhood;
import org.apache.mahout.cf.taste.impl.recommender.GenericUserBasedRecommender;
import org.apache.mahout.cf.taste.impl.similarity.LogLikelihoodSimilarity;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.neighborhood.UserNeighborhood;
import org.apache.mahout.cf.taste.recommender.RecommendedItem;
import org.apache.mahout.cf.taste.recommender.Recommender;
import org.apache.mahout.cf.taste.similarity.UserSimilarity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;


@Slf4j
@Service
public class UserBasedCollaborativeFilteringRecommendService {

    @Autowired
    RecConfig recConfig;

    public void recommend(List<Long> userIds, int infoType) {

        log.info("信息类型：{} - 基于用户的协同过滤 Start.", infoType);

        try {
            // 建模
            DataModel dataModel = getDataModel(infoType);
            UserSimilarity similarity = new LogLikelihoodSimilarity(dataModel);
            UserNeighborhood neighborhood = new NearestNUserNeighborhood(6, similarity, dataModel);
            Recommender recommender = new GenericUserBasedRecommender(dataModel, neighborhood, similarity);

            for (Long userId : userIds) {
                // 获取针对每个用户的推荐项
                List<RecommendedItem> recItems = recommender.recommend(userId, recConfig.getCfRecommendNum());
                // 初始化
                RecommendKit.initToBeRecommended(userId, infoType);
                // 添加生成的推荐项
                recItems.forEach(recItem ->
                    Candidates.toBeRecommended.get(userId).get(infoType).add(recItem.getItemID())
                );
            }

        } catch (Exception e) {
            log.error("协同过滤 构建偏好模型失败！", e);
        }

        log.info("信息类型：{} - 基于用户的协同过滤 End.", infoType);
    }


    /**
     * 根据时效内所有用户的浏览历史，生成DataSourceModel，用于构建协同过滤模型
     */
    private DataModel getDataModel(int infoType) {

        // 构建 <用户ID> - <浏览项ID List> 的 Map
        Map<Long, List<Long>> map = new HashMap<>();
        List<ItemLog> itemLogs = DBKit.getBrowsedItemsByDate(RecommendKit.getInRecDate(recConfig.getCfValidDays()), infoType);
        for (ItemLog itemLog : itemLogs) {
            if (!map.containsKey(itemLog.getUser_id())) {
                map.put(itemLog.getUser_id(), new ArrayList<>());
            }
            map.get(itemLog.getUser_id()).add(itemLog.getRef_data_id());
        }

        // 构建无偏模型
        FastByIDMap fastByIDMap = new FastByIDMap<>();
        for (Long userId : map.keySet()) {
            if (ObjectUtil.isEmpty(userId)) {
                log.error("user_read_record表中出现user_id为null的数据。");
                continue;
            }
            List<Long> list = map.get(userId);
            FastIDSet set = new FastIDSet();
            for (Long aLong : list) {
                set.add(aLong);
            }
            fastByIDMap.put(userId, set);
        }

        return new GenericBooleanPrefDataModel(fastByIDMap);
    }
}
