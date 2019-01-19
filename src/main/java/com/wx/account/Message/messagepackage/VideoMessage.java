package com.wx.account.Message.messagepackage;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 视频消息
 * Created by supermrl on 2019/1/19.
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class VideoMessage extends BaseMessage{

    //回复的消息内容
    private Video video;
}
