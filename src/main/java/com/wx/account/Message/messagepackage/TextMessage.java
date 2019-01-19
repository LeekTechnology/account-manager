package com.wx.account.Message.messagepackage;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 音乐消息
 * Created by supermrl on 2019/1/19.
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class TextMessage extends BaseMessage{

    //回复的消息内容
    private String Content;
}
