package com.wx.account.service.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSONObject;
import com.google.common.base.Strings;
import com.wx.account.dto.TicketInfo;
import com.wx.account.mapper.UserMapper;
import com.wx.account.mapper.UserOtherMapper;
import com.wx.account.model.User;
import com.wx.account.model.UserOther;
import com.wx.account.util.ConstantUtils;
import com.wx.account.util.WxMsgUtil;

import lombok.extern.slf4j.Slf4j;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * 用户管理
 * Created by supermrl on 2019/1/19.
 */
@Service
public class UserServiceImpl {
    private static final Logger log = LoggerFactory.getLogger(UserServiceImpl.class);

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private UserOtherMapper userOtherMapper;


    /**
     * 保存订阅的用户信息
     *
     * @param openid
     * @param ticketInfo
     */
    public User saveWxUser(String openid, TicketInfo ticketInfo) {
        User user = null;
        String userInfoUrl = String.format(ConstantUtils.userInfoUrl, ConstantUtils.accessToken, openid);
        //获取用户信息
        String result = HttpUtil.get(userInfoUrl);
        if (result != null) {
            JSONObject json = JSONObject.parseObject(result);
            try {
                user = transJson2User(json, ticketInfo);
                if (Strings.isNullOrEmpty(user.getOpenid())) {
                    throw new Exception("获取用户信息失败");
                }
                int i = userMapper.insert(user);
                if (i < 0) {
                    log.error("sub user save is fail and user is " + user);
                }
            } catch (Exception e) {
                e.printStackTrace();
                log.error("获取token失败 errcode:{" + json.get("errcode").toString() + "} errmsg:{" + json.get("errmsg").toString() + "}");
            }

        } else {
            log.error("获取用户信息失败");
        }
        return user;
    }

    /**
     * 封装用户信息,添加关注时间和二维码ticket
     *
     * @param json
     * @param ticketInfo
     */
    private User transJson2User(JSONObject json, TicketInfo ticketInfo) {
        User user = JSONObject.parseObject(json.toJSONString(), User.class);
        user.setSubscribe_time(DateUtil.date());
        user.setTicket(ticketInfo.getTicket());
        return user;
    }

    /**
     * 用户取消订阅
     *
     * @param openid
     */
    public void unSubscribe(String openid) {
        userMapper.unSubscribe(openid);//修改用户状态
        userOtherMapper.updateSpreadAction(openid);//用户推广重置
    }

    /**
     * 获取用户信息
     *
     * @param ticket
     * @return
     */
    public User getUserByTicket(String ticket) {
        return userMapper.getUserByTicket(ticket);
    }


    /**
     * 保存推广信息
     *
     * @param uo
     */
    public void insertSpreadInfo(UserOther uo) {
        //如果存在则不进行操作
        UserOther userOtherDB = userOtherMapper.querySpreadInfoByOpenid(uo.getOpenid(), uo.getReference());

        int result = userOtherMapper.insert(uo);
        if (result < 0) {
            log.error("save spreadInfo is fail and params is " + uo.toString());
        }
    }

    public Integer querySpreadNum(String openid) {
        return userOtherMapper.querySpreadNum(openid);
    }
}
