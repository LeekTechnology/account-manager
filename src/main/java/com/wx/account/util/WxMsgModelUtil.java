package com.wx.account.util;

import cn.hutool.core.date.DateUtil;
import com.google.common.collect.Lists;
import com.wx.account.Message.WxMsgInfo;
import com.wx.account.Message.messagepackage.Article;
import com.wx.account.Message.messagepackage.NewsMessage;
import com.wx.account.Message.messagepackage.TextMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

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
    public String systemErrorResponseMessageModel(WxMsgInfo wxMsgInfo ){

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
    public String followResponseMessageModel(WxMsgInfo wxMsgInfo){

        // 关注时发送图文消息
        NewsMessage newsMessage = new NewsMessage();
        newsMessage.setToUserName(wxMsgInfo.getFromUserName());
        newsMessage.setFromUserName(wxMsgInfo.getToUserName());
        newsMessage.setCreateTime(Long.valueOf(DateUtil.format(DateUtil.date(), ConstantUtils.TIME_REQ_PATTERN)));
        newsMessage.setMsgType(weixinMessageUtil.RESP_MESSAGE_TYPE_NEWS);
        newsMessage.setFuncFlag(0);

        // 图文消息
        List<Article> articleList= Lists.newArrayList();
        Article article = new Article();
        // 设置图文消息的标题
        String string = "送你送到小城外，有句话儿要交代";
        article.setTitle(string);
        //设置图文信息
//        article.setPicUrl(webConfigBean.getWeixinPicture()+"meetingLogo2.png");
//        article.setUrl(webConfigBean.getDoMainNameurl()+"/WeixinParticipantFouce");
        articleList.add(article);
        newsMessage.setArticleCount(articleList.size());
        newsMessage.setArticles(articleList);
        return weixinMessageUtil.newsMessageToXml(newsMessage);
    }

    /**
     * @Description: 用户取消关注，先判断用户是否绑定，如果已经绑定则解除绑定
     * @Return: void
     */
    public void cancelAttention(String fromUserName){

//        if (userService.isAlreadyBinding(fromUserName)) {
//            userService.userUnbinding(fromUserName);
//        }else {
//            System.out.println("取消关注用户{}"+fromUserName+"还未绑定");
//        }
        log.info(fromUserName);

    }

}
