package com.wx.account.util;

import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSONObject;
import com.wx.account.common.enums.SpreadType;
import com.wx.account.dto.TicketInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URLEncoder;

/**
 * 二维码工具类
 * Created by supermrl on 2019/1/20.
 */

public class TicketUtil {


    private static Logger logger = LoggerFactory.getLogger(TicketUtil.class);
    /**
     * 获取订阅用户产生的推广二维码全部信息
     * 二维码为永久二维码_最多10W个
     *
     * @param openid
     * @return
     */
    public static TicketInfo getTicketInfo(String openid) {
        TicketInfo info = new TicketInfo();
        if (StrUtil.isBlank(openid)) {
            return null;
        }

        String ticketInfoUrl = String.format(ConstantUtils.ticketInfo, ConstantUtils.accessToken);
        //设置参数
        JSONObject scene = new JSONObject();
        scene.put("scene_id", SpreadType.TICKET.getType());
        scene.put("scene_str", openid);//推广者的openid

        JSONObject action_info = new JSONObject();
        action_info.put("scene", scene);

        JSONObject json = new JSONObject();
        json.put("action_name", "QR_LIMIT_STR_SCENE");//永久类型的二维码字符参数类型
        json.put("action_info", action_info);//永久类型的二维码字符参数类型

        String result = HttpUtil.post(ticketInfoUrl, json.toString());
        logger.info("get ticket info from wx is " + result);
        //获取二维码信息
        if (!StrUtil.isBlank(result)) {
            JSONObject resultObject = JSONObject.parseObject(result);
            try {
                info.setTicket(resultObject.get("ticket").toString());
                info.setUrl(resultObject.get("url").toString());
                //获取二维码图片地址
                String ticketUrl = String.format(ConstantUtils.ticketUrl, URLEncoder.encode(info.getTicket(), "UTF-8"));
                info.setTicketUrl(ticketUrl);
            } catch (Exception e) {
                e.printStackTrace();
                logger.error("获取二维码失败 errcode:{" + resultObject.get("errcode").toString() + "} errmsg:{" + resultObject.get("errmsg").toString() + "}");
            }
        } else {
            logger.error("服务器错误,获取二维码失败");
        }
        logger.info("user openid is "+ openid +" and get TicketInfo from wx is "+info);
        return info;
    }

}
