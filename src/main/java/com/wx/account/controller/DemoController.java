package com.wx.account.controller;

import com.wx.account.model.User;
import com.wx.account.service.DemoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by supermrl on 2019/1/17.
 */

@Slf4j
@RestController
public class DemoController {

    @Autowired
    private DemoService demoService;

    @RequestMapping("/demo")
    public String getData(@RequestBody User users){

        log.info("request body is "+ users.getName());

        User user = demoService.getUserByName(users.getName());
        if(user == null){
            return "。。。";
        }
        return user.getSex() == 0?"女":"男";
    }
}
