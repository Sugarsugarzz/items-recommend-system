package casia.isiteam.recommendsystem.controller;

import casia.isiteam.recommendsystem.common.Candidates;
import casia.isiteam.recommendsystem.common.Constant;
import casia.isiteam.recommendsystem.service.cb.ContentBasedRecommendService;
import casia.isiteam.recommendsystem.service.cf.UserBasedCollaborativeFilteringRecommendService;
import casia.isiteam.recommendsystem.service.hr.HotRecommendService;
import casia.isiteam.recommendsystem.service.latest.LatestRecommendService;
import casia.isiteam.recommendsystem.service.random.RandomRecommendService;
import casia.isiteam.recommendsystem.utils.DBKit;
import casia.isiteam.recommendsystem.utils.RecommendKit;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


@Slf4j
@RestController
public class IndexController {

    @Autowired
    ContentBasedRecommendService contentBasedRecommendService;
    @Autowired
    HotRecommendService hotRecommendService;
    @Autowired
    LatestRecommendService latestRecommendService;
    @Autowired
    RandomRecommendService randomRecommendService;
    @Autowired
    UserBasedCollaborativeFilteringRecommendService userBasedCollaborativeFilteringRecommendService;


    /**
     * 为特定用户执行一次推荐
     * @param userIds 特定用户ID列表
     */
    @RequestMapping("/executeForCertainUsers")
    public void executeInstantJobForCertainUsers(@RequestParam("userIds") List<Long> userIds) {
        executeInstantJob(userIds);
    }

    /**
     * 为所有用户执行一次推荐
     * 定时每日零时执行
     */
    @Scheduled(cron = "0 0 0 * * ?")
    @RequestMapping("/executeForAllUsers")
    public void executeInstantJobForAllUsers() {
        if (!Constant.isProcessing) {
            log.info("============ 开始本轮推荐 ============");
            executeInstantJob(DBKit.getAllUserIds());
        } else {
            log.info("============ 已有推荐任务在执行中，跳过本次推荐 ============");
        }
    }

    /**
     * 执行一次推荐
     * @param userIds 用户id列表
     */
    private void executeInstantJob(List<Long> userIds) {
        Constant.isProcessing = true;
        try {
            // 清除时效外的推荐项
            DBKit.deleteDefaultRecommendationByDate(RecommendKit.getInRecDate());
            DBKit.deleteRecommendationByDate(RecommendKit.getInRecDate());
            // 清空推荐项
            RecommendKit.emptyRecommendations();

            // 生成推荐项
            hotRecommendService.formHotItems();
            latestRecommendService.formLatestItems();
            randomRecommendService.formRandomItems();
            for (Integer infoType : Constant.INFO_TYPES) {
                userBasedCollaborativeFilteringRecommendService.recommend(userIds, infoType);
                contentBasedRecommendService.recommend(userIds, infoType);
                hotRecommendService.recommend(userIds, infoType);
                randomRecommendService.recommend(userIds, infoType);
                latestRecommendService.recommend(userIds, infoType);
            }

            // 默认推荐项
            log.info("正在生成 默认推荐项...");
            Collections.shuffle(Candidates.defaultCandidates);
            DBKit.saveDefaultRecommendation(Candidates.defaultCandidates);
            Collections.shuffle(Candidates.latestCandidates);
            DBKit.saveDefaultRecommendation(Candidates.latestCandidates);
            log.info("默认推荐项生成完成. 共 {} 条.", Candidates.defaultCandidates.size() + Candidates.latestCandidates.size());

            // 存推荐结果  混排
            log.info("正在生成 个性化推荐项...共 {} 个用户待推荐.", Candidates.toBeRecommended.keySet().size());
            int count = 0;
            for (Long userId : Candidates.toBeRecommended.keySet()) {
                List<long[]> candidates = new ArrayList<>();
                for (Integer infoType : Candidates.toBeRecommended.get(userId).keySet()) {
                    RecommendKit.filterBrowsedItems(Candidates.toBeRecommended.get(userId).get(infoType), userId, infoType);
                    for (Long itemId : Candidates.toBeRecommended.get(userId).get(infoType)) {
                        candidates.add(new long[] {itemId, infoType});
                    }
                }
                log.info("正在为第 {}/{} 个用户生成推荐项.（UserID：{}，共 {} 条.）...", ++count, Candidates.toBeRecommended.keySet().size(), userId, candidates.size());
                Collections.shuffle(candidates);
                // 存入
                DBKit.saveRecommendation(userId, candidates);
            }

            log.info("本次推荐结束.");

            // 清空推荐项
            RecommendKit.emptyRecommendations();
        } catch (Exception e) {
            log.error("本轮推荐发生错误！", e);
        } finally {
            Constant.isProcessing = false;
        }
    }
}
