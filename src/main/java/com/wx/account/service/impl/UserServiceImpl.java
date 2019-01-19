package com.wx.account.service.impl;

import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSONObject;
import com.wx.account.mapper.UserMapper;
import com.wx.account.util.ConstantUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * Created by supermrl on 2019/1/19.
 */
@Slf4j
@Service
public class UserServiceImpl {


    @Autowired
    private UserMapper userMapper;


    /**
     * 保存订阅的用户信息
     *
     * @param openid
     */
    public void saveWxUser(String openid) {
        String userInfoUrl = ConstantUtils.userInfoUrl.replace("ACCESS_TOKEN", ConstantUtils.accessToken).replace("OPENID", openid);
        String result = HttpUtil.get(userInfoUrl);
        if (result != null) {
            JSONObject json = JSONObject.parseObject(result);
            try {

            } catch (Exception e) {
                e.printStackTrace();
                log.error("获取token失败 errcode:{" + json.get("errcode").toString() + "} errmsg:{" + json.get("errmsg").toString() + "}");
            }

        } else {
            log.error("获取用户信息失败");
        }
    }

}
