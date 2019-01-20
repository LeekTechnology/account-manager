package com.wx.account.util;

import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.wx.account.common.enums.TemplateType;
import lombok.extern.slf4j.Slf4j;

/**
 * 模板消息
 * Created by supermrl on 2019/1/20.
 */
@Slf4j
public class TemplateUtil {


    /**
     * 发送模板消息
     * @param spreadName
     * @param userNickname
     * @param spreadNum
     * @param openid
     */
    public static void sendSpreadTemplateMsg(String spreadName, String userNickname, Integer spreadNum, String openid){
        JSONObject data = new JSONObject();
        JSONObject spreadNameT = new JSONObject();
        spreadNameT.put("value",spreadName);
        spreadNameT.put("color","#173177");

        JSONObject userT = new JSONObject();
        userT.put("value",userNickname);
        userT.put("color","#173177");

        JSONObject spreadNumT = new JSONObject();
        spreadNumT.put("value",spreadNum);
        spreadNumT.put("color","#173177");

        data.put("spreadName",spreadNameT);
        data.put("user",userT);
        data.put("spreadNum",spreadNumT);


        JSONObject json = new JSONObject();
        json.put("touser",openid);
        json.put("template_id", TemplateType.SPREAD_SUCCESS.getTemplate_id());
        json.put("data",data);


        String sendSpreadTemplateUrl = String.format(ConstantUtils.templateSendUrl,ConstantUtils.accessToken);
        log.info("send spreadUser message body is "+json.toJSONString());
        String result = HttpUtil.post(sendSpreadTemplateUrl, json.toJSONString());
        if(!StrUtil.isBlank(result)){
            JSONObject resultJson = JSONObject.parseObject(result);
            String errcode = resultJson.get("errcode").toString();
            if(!"0".equals(errcode)){
                log.info("send spreadUser message is fail and openid is "+ spreadName);
            }
        }
    }
}
