package com.wx.account.util;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSONObject;
import com.wx.account.common.enums.TemplateType;
import com.wx.account.config.error.ErrorCode;
import com.wx.account.dto.TemplateInfo;
import com.wx.account.exception.BusinessException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 模板消息
 * Created by supermrl on 2019/1/20.
 */
public class TemplateUtil {


    private static Logger logger = LoggerFactory.getLogger(TemplateUtil.class);

    /**
     * 获取消息模板
     *
     * @return
     */
    private static String getTempleId(String title) throws Exception {
        String templateId = null;
        String getTemplateListUrl = String.format(ConstantUtils.TemplateLListUrl, ConstantUtils.accessToken);
        String result = HttpUtil.get(getTemplateListUrl);
        if (StrUtil.isBlank(result)) {
            logger.error("微信服务端获取模板失败");
            throw new BusinessException(ErrorCode.TEMPLATE_GET_IS_FAIL);
        }
        logger.info("template list is "+ result);
        JSONObject resultJson = JSONObject.parseObject(result);
        //转换模板对象数组
        List<TemplateInfo> templateInfos = JSONObject.parseArray(resultJson.get("template_list").toString(), TemplateInfo.class);
        if (CollUtil.isEmpty(templateInfos)) {
            logger.error("请先设置模板数据");
            throw new BusinessException(ErrorCode.TEMPLATE_DATA_IS_NULL);
        }
        //模板map
        Map<String, TemplateInfo> templateInfoMap = templateInfos.stream().collect(Collectors.toMap(TemplateInfo::getTitle, t -> t));
        TemplateInfo templateInfo = templateInfoMap.get(title);
        if (BeanUtil.isEmpty(templateInfo)) {
            logger.error("无法匹配系统内置模板名称");
            throw new BusinessException(ErrorCode.TEMPLATE_TITLE_NOT_MATCH);
        }
        templateId = templateInfo.getTemplate_id();
        return templateId;
    }

    /**
     * 发送模板消息
     *
     * @param spreadName
     * @param userNickname
     * @param spreadNum
     * @param openid
     */
    public static void sendSpreadTemplateMsg(String spreadName, String userNickname, Integer spreadNum, String openid) throws Exception {
        JSONObject data = new JSONObject();
        JSONObject spreadNameT = new JSONObject();
        spreadNameT.put("value", spreadName);
        spreadNameT.put("color", "#173177");

        JSONObject userT = new JSONObject();
        userT.put("value", userNickname);
        userT.put("color", "#173177");

        JSONObject spreadNumT = new JSONObject();
        spreadNumT.put("value", spreadNum);
        spreadNumT.put("color", "#173177");

        data.put("spreadName", spreadNameT);
        data.put("user", userT);
        data.put("spreadNum", spreadNumT);


        JSONObject json = new JSONObject();
        json.put("touser", openid);
        json.put("template_id", getTempleId(TemplateType.SPREAD_SUCCESS.getTemplate_Title()));
        json.put("data", data);


        String sendSpreadTemplateUrl = String.format(ConstantUtils.templateSendUrl, ConstantUtils.accessToken);
        logger.info("send spreadUser message body is " + json.toJSONString());
        String result = HttpUtil.post(sendSpreadTemplateUrl, json.toJSONString());
        if (!StrUtil.isBlank(result)) {
            JSONObject resultJson = JSONObject.parseObject(result);
            String errcode = resultJson.get("errcode").toString();
            if (!"0".equals(errcode)) {
                logger.info("send spreadUser message is fail and openid is " + spreadName);
            }
        }
    }
}
