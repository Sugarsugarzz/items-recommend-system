package casia.isiteam.recommendsystem.controller;

import casia.isiteam.recommendsystem.main.Recommender;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
public class IndexController {

    @RequestMapping("/execute")
    public String doRecommend() {
        new Recommender().executeInstantJobForAllUsers();
        return "success";
    }

    @RequestMapping("/executefor")
    public String doReocmmendForUser(@RequestParam("uid") Long uid) {
        List<Long> users = new ArrayList<>();
        users.add(uid);
        new Recommender().executeInstantJobForCertainUsers(users);
        return "success";
    }
}
