package com.wx.account.Message;


import lombok.Data;
import org.springframework.stereotype.Component;

import java.io.Serializable;


/**
 * 封装发送方,接收方,微信用户名等
 * Created by supermrl on 2019/1/19.
 */
@Data
@Component
public class WxMsgInfo implements Serializable {


    private static final long serialVersionUID = 1L;

    private String fromUserName;           // 发送发微信账号

    private String toUserName;             // 接收方微信账号

    private String weixinUserName;         // 微信用户名

    private String messageType;            // 消息类型

    private String content;            // 消息内容
}
