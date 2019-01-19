package com.wx.account.Message.messagepackage;

import lombok.Data;

/**
 * 音乐类
 * Created by supermrl on 2019/1/19.
 */
@Data
public class Music {

    //音乐标题
    private String Title;

    //音乐描述
    private String Description;

    //音乐链接
    private String MusicUrl;

    //高品质音乐链接，Wi-Fi环境优先使用该链接播放音乐
    private String HQMusicUrl;

    //缩略图的媒体ID，通过上传多媒体文件得到的ID
    private String ThumbMediaId;

}
