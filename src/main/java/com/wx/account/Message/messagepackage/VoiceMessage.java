package com.wx.account.Message.messagepackage;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 语音消息
 * Created by supermrl on 2019/1/19.
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class VoiceMessage extends BaseMessage{

    //回复的消息内容
    private Voice voice;
}
