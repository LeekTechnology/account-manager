package com.wx.account.service.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSONObject;
import com.wx.account.dto.TicketInfo;
import com.wx.account.mapper.SpreadUserMapper;
import com.wx.account.mapper.UserMapper;
import com.wx.account.model.SpreadUser;
import com.wx.account.model.User;
import com.wx.account.util.ConstantUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * 用户管理
 * Created by supermrl on 2019/1/19.
 */
@Service
public class UserServiceImpl {

    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private SpreadUserMapper userOtherMapper;


    /**
     * 保存订阅的用户信息
     *
     * @param openid
     * @param ticketInfo
     */
    public User saveWxUser(String openid, TicketInfo ticketInfo) {
        User user = null;
        String userInfoUrl = String.format(ConstantUtils.USER_INFO_URL, ConstantUtils.accessToken, openid);
        //获取用户信息
        String result = HttpUtil.get(userInfoUrl);
        if (result != null) {
            JSONObject json = JSONObject.parseObject(result);
            try {
                user = transJson2User(json, ticketInfo);
                if (StrUtil.isBlank(user.getOpenid())) {
                    throw new Exception("获取用户信息失败");
                }
                int i = userMapper.save(user);
                if (i < 0) {
                    logger.error("sub user save is fail and user is " + user);
                }
            } catch (Exception e) {
                e.printStackTrace();
                logger.error("获取token失败 errcode:{" + json.get("errcode").toString() + "} errmsg:{" + json.get("errmsg").toString() + "}");
            }

        } else {
            logger.error("获取用户信息失败");
        }
        return user;
    }

    /**
     * 保存订阅的用户信息
     *
     * @param openid
     */
    public User findWxUser(String openid) {

        User temp = new User();
        temp.setOpenid(openid);
        List<User> users = userMapper.selectList(temp);

        //已经关注
        if (!CollectionUtils.isEmpty(users)) {
            User use = users.get(0);
            if (!use.getStatus()) {
                //该用户曾经关注过，后又取消关注，信息处于锁定状态；现在又重新关注，需重置状态为正常
                use.setStatus(true);
                userMapper.update(use);
            }
            return use;
        }

        //未关注
        User user = null;
        String userInfoUrl = String.format(ConstantUtils.USER_INFO_URL, ConstantUtils.accessToken, openid);
        //获取用户信息
        String result = HttpUtil.get(userInfoUrl);
        if (result != null) {
            JSONObject json = JSONObject.parseObject(result);
            try {
                user = JSONObject.parseObject(json.toJSONString(), User.class);
                user.setStatus(true);
                if (StrUtil.isBlank(user.getOpenid())) {
                    throw new Exception("获取用户信息失败");
                }
                int i = userMapper.save(user);
                if (i < 0) {
                    logger.error("sub user save is fail and user is " + user);
                }
            } catch (Exception e) {
                e.printStackTrace();
                logger.error("获取token失败 errcode:{" + json.get("errcode").toString() + "} errmsg:{" + json.get("errmsg").toString() + "}");
            }

        } else {
            logger.error("获取用户信息失败");
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
        //userOtherMapper.updateSpreadAction(openid);//用户推广重置
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
    public void insertSpreadInfo(SpreadUser uo) {
        //如果存在则不进行操作
        SpreadUser userOtherDB = userOtherMapper.querySpreadInfoByOpenid(uo.getOpenid(), uo.getReference());

        int result = userOtherMapper.insert(uo);
        if (result < 0) {
            logger.error("save spreadInfo is fail and params is " + uo.toString());
        }
    }

    public Integer querySpreadNum(String openid) {
        return userOtherMapper.querySpreadNum(openid);
    }
}
