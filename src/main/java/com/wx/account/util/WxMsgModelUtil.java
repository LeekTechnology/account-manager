package com.wx.account.util;

import cn.hutool.core.date.DateUtil;
import com.google.common.collect.Lists;
import com.wx.account.Message.WxMsgInfo;
import com.wx.account.Message.messagepackage.Article;
import com.wx.account.Message.messagepackage.NewsMessage;
import com.wx.account.Message.messagepackage.TextMessage;
import com.wx.account.dto.TicketInfo;
import com.wx.account.model.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * 封装微信回复消息，各种回复消息对应不同的方法
 * Created by supermrl on 2019/1/19.
 */
@Slf4j
@Component
public class WxMsgModelUtil {

    @Autowired
    private WxMsgUtil weixinMessageUtil;


    /**
     * @Description: 当系统出错时，默认回复的文本消息
     * @Return: 系统出错回复的消息
     */
    public String systemErrorResponseMessageModel(WxMsgInfo wxMsgInfo) {

        // 回复文本消息
        TextMessage textMessage = new TextMessage();
        textMessage.setToUserName(wxMsgInfo.getFromUserName());
        textMessage.setFromUserName(wxMsgInfo.getToUserName());
        textMessage.setCreateTime(Long.valueOf(DateUtil.format(DateUtil.date(), ConstantUtils.TIME_REQ_PATTERN)));
        textMessage.setMsgType(weixinMessageUtil.RESP_MESSAGE_TYPE_TEXT);
        textMessage.setFuncFlag(0);
        textMessage.setContent("系统出错啦，请稍后再试");
        return weixinMessageUtil.textMessageToXml(textMessage);
    }

    /**
     * @Description: 用户关注时发送的图文消息
     * @Return: 用户关注后发送的提示绑定用户的图文消息
     */
    public String followResponseMessageModel(WxMsgInfo wxMsgInfo, User user) {
        // 关注之后向用户发送专属二维码
        NewsMessage newsMessage = new NewsMessage();
        newsMessage.setToUserName(wxMsgInfo.getFromUserName());
        newsMessage.setFromUserName(wxMsgInfo.getToUserName());
        newsMessage.setCreateTime(Long.valueOf(DateUtil.format(DateUtil.date(), ConstantUtils.TIME_REQ_PATTERN)));
        newsMessage.setMsgType(weixinMessageUtil.RESP_MESSAGE_TYPE_NEWS);
        newsMessage.setFuncFlag(0);

        // 图文消息
        List<Article> articleList = Lists.newArrayList();
        Article article = new Article();
        // 设置图文消息的标题
        String title = "欢迎 " + user.getNickname() + " 来到" + ConstantUtils.ACCOUNT_NAME;
        article.setTitle(title);
        String text = "嗨hahaha ，"+user.getNickname()+"，一场高端而又有趣的任务宝之旅正在为你展开。。。\n" +
                "成功邀请 10 个好友扫码你的专属任务海报并关注我们，即可拿走A6一辆！\n" +
                "成功邀请 20 个好友扫码你的专属任务海报并关注我们，即可拿走A8一辆！\n" +
                "\n" +
                "数量有限，需要你速战速决！\n" +
                "\n" +
                "\n" +
                "即将为你生成专属任务海报↓↓↓";
        article.setDescription(text);

        //设置图文信息
        //获取推广二维码
        TicketInfo ticketInfo = TicketUtil.getTicketInfo(user.getOpenid());
        article.setPicUrl(ticketInfo.getTicketUrl());
        article.setUrl(ticketInfo.getTicketUrl());
        articleList.add(article);

        newsMessage.setArticleCount(articleList.size());
        newsMessage.setArticles(articleList);
        return weixinMessageUtil.newsMessageToXml(newsMessage);
    }

}
