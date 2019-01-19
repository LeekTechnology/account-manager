package com.wx.account.task;

import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.wx.account.util.ConstantUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.context.properties.PropertyMapper;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 获取accessToken定时任务
 * 获取access_token的接口地址（GET） 限2000（次/天）
 * 微信的accessToken有效时间为两个小时，调用微信接口很多都要用到accessToken所以需要将accessToken保存，可以保存到redis中或者内存中
 * Created by supermrl on 2019/1/19.
 */
@Slf4j
@Order(1) //启动顺序
public class AccessTokenTask implements CommandLineRunner {


    @Override
    public void run(String... strings) throws Exception {
        this.getWeixinAccessToken();
    }

    @Scheduled(cron = "0 0 0/1 * * ? ")//每一小时执行一次
    private void getWeixinAccessToken() {
        log.info("======init accessToken task start======");
        try {
            initAccessToken(ConstantUtils.appid, ConstantUtils.appsecret);
            System.out.println(ConstantUtils.accessToken);
            if (ConstantUtils.accessToken == null) {
                //重复三次获取accessToken
                for (int i = 0; i < 3; i++) {
                    Thread.sleep(2000);
                    initAccessToken(ConstantUtils.appid, ConstantUtils.appsecret);
                    if (ConstantUtils.accessToken != null) {
                        break;
                    }
                }
            }
            log.info("获取到的微信accessToken为" + ConstantUtils.accessToken);
        } catch (Exception e) {
            log.error("获取微信adcessToken出错");
            e.printStackTrace();
        }
        log.info("======init accessToken task end======");
    }

    /**
     * 初始化accessToken
     *
     * @param appid
     * @param appsecret
     */
    private void initAccessToken(String appid, String appsecret) {
        String url = ConstantUtils.accessTokenUrl.replace("APPID", appid).replace("APPSECRET", appsecret);
        String result = HttpUtil.get(url);
        String accessToken = null;
        //判断json是否为空
        if (result != null) {
            JSONObject json = JSONObject.parseObject(result);
            try {
                accessToken = json.get("access_token").toString();
            } catch (Exception e) {
                e.printStackTrace();
                log.error("获取token失败 errcode:{" + json.get("errcode").toString() + "} errmsg:{" + json.get("errmsg").toString() + "}");
            }
        } else {
            // 获取token失败
            log.error("微信accessToken无返回数据");
        }
        //设置accessToken
        ConstantUtils.setAccessToken(accessToken);
    }


}
