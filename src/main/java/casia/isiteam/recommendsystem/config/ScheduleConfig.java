package casia.isiteam.recommendsystem.config;

import casia.isiteam.recommendsystem.main.Recommender;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;


@Configuration
@EnableScheduling
public class ScheduleConfig {

    // 定时每日0时执行一次为所有用户推荐
    @Scheduled(cron = "0 0 0 * * ?")
    public void doRecommend() {
        new Recommender().executeInstantJobForAllUsers();
    }
}
