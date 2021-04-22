package casia.isiteam.recommendsystem.service.random;

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
public class RandomRecommendService {

    @Autowired
    RecConfig recConfig;


    public void recommend(List<Long> userIds, int infoType) {
        log.info("信息类型：{} - 基于随机推荐 Start.", infoType);
        for (Long userId : userIds) {
            // 初始化
            RecommendKit.initToBeRecommended(userId, infoType);
            // 添加生成的推荐词条项
            Candidates.randomItemsMap.get(infoType).forEach(itemId ->
                Candidates.toBeRecommended.get(userId).get(infoType).add(itemId)
            );
        }
        log.info("信息类型：{} - 基于随机推荐 End.", infoType);
    }

    public void formRandomItems() {
        // 获取每个类型的随机项
        log.info("正在生成 随机项....");
        try {
            for (Integer infoType : Constant.INFO_TYPES) {
                // 生成随机项
                Candidates.randomItemsMap.put(infoType, new ArrayList<>());
                List<Long> randomItemIds = DBKit.getRandomItemsByInfoType(infoType, recConfig.getRandomRecommendNum());
                Candidates.randomItemsMap.get(infoType).addAll(randomItemIds);
                // 添加到默认推荐项
                randomItemIds.forEach(itemId ->
                        Candidates.defaultCandidates.add(new long[] {itemId, infoType})
                );
            }
        } catch (Exception e) {
            log.error("生成随机项发生错误！", e);
        }
        log.info("生成 随机项 完毕！");
    }
}
