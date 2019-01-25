package com.wx.account.Message.messagepackage;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 图片消息
 * Created by supermrl on 2019/1/19.
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class ImageMessage extends BaseMessage{

    private Image Image = new Image();
}
