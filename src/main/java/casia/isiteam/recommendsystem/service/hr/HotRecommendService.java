package casia.isiteam.recommendsystem.service.hr;

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
public class HotRecommendService {

    @Autowired
    RecConfig recConfig;


    public void recommend(List<Long> userIds, int infoType) {
        log.info("信息类型：{} - 基于热点推荐 Start.", infoType);

        for (Long userId : userIds) {
            // 初始化
            RecommendKit.initToBeRecommended(userId, infoType);
            // 添加生成的推荐词条项
            Candidates.hotItemsMap.get(infoType).forEach(itemId ->
                Candidates.toBeRecommended.get(userId).get(infoType).add(itemId)
            );
        }
        log.info("信息类型：{} - 基于热点推荐 End.", infoType);
    }

    public void formHotItems() {
        // 获取每个类型的热点项
        log.info("正在生成 热点项....");
        try {
            for (Integer infoType : Constant.INFO_TYPES) {
                // 生成热点项
                Candidates.hotItemsMap.put(infoType, new ArrayList<>());
                List<Long> hotItemIds = DBKit.getHotItemIds(RecommendKit.getInRecDate(recConfig.getHotBeforeDays()), infoType, recConfig.getHotRecommendNum());
                Candidates.hotItemsMap.get(infoType).addAll(hotItemIds);
                // 添加到默认推荐项
                hotItemIds.forEach(itemId ->
                        Candidates.defaultCandidates.add(new long[] {itemId, infoType})
                );
            }
        } catch (Exception e) {
            log.error("生成热点项发生错误！", e);
        }

        log.info("生成 热点项 完毕！");
    }
}
