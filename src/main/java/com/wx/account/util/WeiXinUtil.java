package com.wx.account.util;

import cn.hutool.http.HttpUtil;
import com.wx.account.model.LimitStrQRCode;
import com.wx.account.model.StrScene;
import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by Administrator on 2019/1/29.
 */
public class WeiXinUtil {

    private static Logger log = LoggerFactory.getLogger(WeiXinUtil.class);

    /**
     * 生成str值的永久二维码
     *
     * @param str
     * @return
     */
    public static JSONObject createLimitQRCode(String str) {
        try {
            LimitStrQRCode qrCode = new LimitStrQRCode(new StrScene(str));
            String ticketInfoUrl = String.format(ConstantUtils.ticketInfo, ConstantUtils.accessToken);
            JSONObject result = JSONObject.fromObject(HttpUtil.post(ticketInfoUrl, JSONObject.fromObject(qrCode).toString()));

            if (result != null) {
                if (!result.containsKey("ticket")) {
                   // throw new CustomException("createTempQRCode", result.getString("errcode") + ":" + result.getString("errmsg"));
                }
                return result;
            }
        } catch (Exception e) {
            log.info("创建str值的永久二维码出错:\r\n", e.fillInStackTrace());
        }

        return null;
    }

}
