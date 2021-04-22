package casia.isiteam.recommendsystem;

import casia.isiteam.recommendsystem.controller.IndexController;
import casia.isiteam.recommendsystem.utils.SpringUtil;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class RecommendsystemApplication {

    public static void main(String[] args) {
        SpringApplication.run(RecommendsystemApplication.class, args);

        // 启动一轮全部推荐
        ApplicationContext context = SpringUtil.getApplicationContext();
        IndexController bean = context.getBean(IndexController.class);
        bean.executeInstantJobForAllUsers();
    }
}
