package casia.isiteam.recommendsystem.service.latest;

import casia.isiteam.recommendsystem.common.Candidates;
import casia.isiteam.recommendsystem.common.Constant;
import casia.isiteam.recommendsystem.common.RecConfig;
import casia.isiteam.recommendsystem.utils.DBKit;
import casia.isiteam.recommendsystem.utils.RecommendKit;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Slf4j
@Service
public class LatestRecommendService {

    @Autowired
    RecConfig recConfig;


    public void recommend(List<Long> userIds, int infoType) {
        log.info("信息类型：{} -  基于最新推荐 Start.", infoType);
        for (Long userId : userIds) {
            // 初始化
            RecommendKit.initToBeRecommended(userId, infoType);
            // 添加生成的推荐词条项
            Candidates.latestItemsMap.get(infoType).forEach(itemId ->
                Candidates.toBeRecommended.get(userId).get(infoType).add(itemId)
            );
        }
        log.info("信息类型：{} - 基于最新推荐 End.", infoType);
    }

    public void formLatestItems() {
        // 获取每个类型的随机项
        log.info("正在生成 最新项....");
        try {
            for (Integer infoType : Constant.INFO_TYPES) {
                // 生成随机项
                Candidates.latestItemsMap.put(infoType, new ArrayList<>());
                List<Long> latestItemIds = DBKit.getLatestItemsByDateAndInfoType(infoType, recConfig.getLatestRecommendNum());
                Candidates.latestItemsMap.get(infoType).addAll(latestItemIds);
                // 添加到默认推荐项
                latestItemIds.forEach(itemId ->
                        Candidates.latestCandidates.add(new long[] {itemId, infoType})
                );
            }
        } catch (Exception e) {
            log.error("生成最新项发生错误！", e);
        }
        log.info("生成 最新项 完毕！");
    }
}
