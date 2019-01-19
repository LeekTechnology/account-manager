package com.wx.account.service.impl;

import cn.hutool.core.date.DateUtil;
import com.wx.account.Message.WxMsgInfo;
import com.wx.account.Message.messagepackage.TextMessage;
import com.wx.account.service.WxCoreService;
import com.wx.account.util.ConstantUtils;
import com.wx.account.util.WxMsgModelUtil;
import com.wx.account.util.WxMsgUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

/**
 * Created by supermrl on 2019/1/19.
 */
@Slf4j
@Service
public class WxCoreServiceImpl implements WxCoreService {

    @Autowired
    private WxMsgUtil wxMsgUtil;
    @Autowired
    private WxMsgInfo wxMsgInfo;
    @Autowired
    private WxMsgModelUtil wxMsgModelUtil;
    @Autowired
    private UserServiceImpl userService;


    @Override
    public String wxMessageHandelCoreService(HttpServletRequest req, HttpServletResponse resp) {
        log.info("------------微信消息开始处理-------------");
        // 返回给微信服务器的消息,默认为null

        String respMessage = null;

        try {

            // 默认返回的文本消息内容
            String respContent = null;
            // xml分析
            // 调用消息工具类MessageUtil解析微信发来的xml格式的消息，解析的结果放在HashMap里；
            Map<String, String> map = wxMsgUtil.parseXml(req);
            // 发送方账号
            String fromUserName = map.get("FromUserName");
            wxMsgInfo.setFromUserName(fromUserName);
            // 接受方账号（公众号）
            String toUserName = map.get("ToUserName");
            wxMsgInfo.setToUserName(toUserName);
            // 消息类型
            String msgType = map.get("MsgType");
            wxMsgInfo.setMessageType(msgType);

            // 默认回复文本消息
            TextMessage textMessage = new TextMessage();
            textMessage.setToUserName(fromUserName);
            textMessage.setFromUserName(toUserName);
            textMessage.setCreateTime(Long.valueOf(DateUtil.format(DateUtil.date(), ConstantUtils.TIME_REQ_PATTERN)));
            textMessage.setMsgType(wxMsgUtil.RESP_MESSAGE_TYPE_TEXT);
            textMessage.setFuncFlag(0);

            // 分析用户发送的消息类型，并作出相应的处理

            // 文本消息
            if (msgType.equals(wxMsgUtil.REQ_MESSAGE_TYPE_TEXT)) {
                respContent = "亲，这是文本消息！";
                textMessage.setContent(respContent);
                respMessage = wxMsgUtil.textMessageToXml(textMessage);
            }

            // 图片消息
            else if (msgType.equals(wxMsgUtil.REQ_MESSAGE_TYPE_IMAGE)) {
                respContent = "您发送的是图片消息！";
                textMessage.setContent(respContent);
                respMessage = wxMsgUtil.textMessageToXml(textMessage);
            }

            // 语音消息
            else if (msgType.equals(wxMsgUtil.REQ_MESSAGE_TYPE_VOICE)) {
                respContent = "您发送的是语音消息！";
                textMessage.setContent(respContent);
                respMessage = wxMsgUtil.textMessageToXml(textMessage);
            }

            // 视频消息
            else if (msgType.equals(wxMsgUtil.REQ_MESSAGE_TYPE_VIDEO)) {
                respContent = "您发送的是视频消息！";
                textMessage.setContent(respContent);
                respMessage = wxMsgUtil.textMessageToXml(textMessage);
            }

            // 地理位置消息
            else if (msgType.equals(wxMsgUtil.REQ_MESSAGE_TYPE_LOCATION)) {
                respContent = "您发送的是地理位置消息！";
                textMessage.setContent(respContent);
                respMessage = wxMsgUtil.textMessageToXml(textMessage);
            }

            // 链接消息
            else if (msgType.equals(wxMsgUtil.REQ_MESSAGE_TYPE_LINK)) {
                respContent = "您发送的是链接消息！";
                textMessage.setContent(respContent);
                respMessage = wxMsgUtil.textMessageToXml(textMessage);
            }

            // 事件推送(当用户主动点击菜单，或者扫面二维码等事件)
            else if (msgType.equals(wxMsgUtil.REQ_MESSAGE_TYPE_EVENT)) {

                // 事件类型
                String eventType = map.get("Event");
                log.info("eventType------>" + eventType);
                // 关注
                if (eventType.equals(wxMsgUtil.EVENT_TYPE_SUBSCRIBE)) {
                    //保存关注人员的信息
                    userService.saveWxUser(fromUserName);
                    respMessage = wxMsgModelUtil.followResponseMessageModel(wxMsgInfo);
                }
                // 取消关注
                else if (eventType.equals(wxMsgUtil.EVENT_TYPE_UNSUBSCRIBE)) {
                    wxMsgModelUtil.cancelAttention(fromUserName);
                }
                // 扫描带参数二维码
                else if (eventType.equals(wxMsgUtil.EVENT_TYPE_SCAN)) {
                    log.info("扫描带参数二维码");
                }
                // 上报地理位置
                else if (eventType.equals(wxMsgUtil.EVENT_TYPE_LOCATION)) {
                    log.info("上报地理位置");
                }
                // 自定义菜单（点击菜单拉取消息）
                else if (eventType.equals(wxMsgUtil.EVENT_TYPE_CLICK)) {

                    // 事件KEY值，与创建自定义菜单时指定的KEY值对应
                    String eventKey = map.get("EventKey");
                    log.info("eventKey------->" + eventKey);

                }
                // 自定义菜单（(自定义菜单URl视图)）
                else if (eventType.equals(wxMsgUtil.EVENT_TYPE_VIEW)) {
                    log.info("处理自定义菜单URI视图");
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
            log.error("系统出错");
            respMessage = null;
        } finally {
            if (null == respMessage) {
                respMessage = wxMsgModelUtil.systemErrorResponseMessageModel(wxMsgInfo);
            }
        }

        return respMessage;
    }


}
