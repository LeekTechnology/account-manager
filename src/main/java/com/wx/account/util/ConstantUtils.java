package com.wx.account.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

/**
 * 系统定义公用数据
 * Created by supermrl on 2019/1/19.
 */
@Component
public class ConstantUtils {

    public static String token;

    public static String appid;

    public static String appsecret;

    public static String accessToken;

    @Value("${wx.token}")
    public void setToken(String token) {
        ConstantUtils.token = token;
    }

    @Value("${wx.appid}")
    public void setAppid(String appid) {
        ConstantUtils.appid = appid;
    }

    @Value("${wx.appsecret}")
    public void setAppsecret(String appsecret) {
        ConstantUtils.appsecret = appsecret;
    }

    public static void setAccessToken(String accessToken) {
        ConstantUtils.accessToken = accessToken;
    }

    /**
     * 日期格式
     */
    public static String DATE_MONTH = "yyyy.MM";
    public static String DATE_REQ_PATTERNs = "yyyyMM";
    public static String DATE_REQ_PATTERN = "yyyyMMdd";
    public static String DATE_DB_PATTERN = "yyyy-MM-dd";
    public static String TIME_REQ_PATTERN = "yyyyMMddHHmmss";
    public static String TIME_DB_PATTERN = "yyyy-MM-dd HH:mm:ss";


    /**
     * 公众号名称
     */
    public static final String ACCOUNT_NAME = "楠木科技";


    /**
     * 链接信息
     */
    //获取accessToken--url
    public static final String accessTokenUrl = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=%s&secret=%s";

    //获取用户信息url --url
    public static final String userInfoUrl = "https://api.weixin.qq.com/cgi-bin/user/info?access_token=%s&openid=%s&lang=zh_CN";

    //永久二维码ticket --info
    public static final String ticketInfo = "https://api.weixin.qq.com/cgi-bin/qrcode/create?access_token=%s";

    //通过ticket换取二维码
    public static final String ticketUrl = "https://mp.weixin.qq.com/cgi-bin/showqrcode?ticket=%s";

    //发送模板消息
    public static final String templateSendUrl = "https://api.weixin.qq.com/cgi-bin/message/template/send?access_token=%s";

    //图片上传微信地址
    public static final String UPLOAD_IMG_URL = "https://api.weixin.qq.com/cgi-bin/media/upload?access_token=ACCESS_TOKEN&type=TYPE";

}
