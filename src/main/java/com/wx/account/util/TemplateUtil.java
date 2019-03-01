package com.wx.account.util;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSONObject;
import com.wx.account.config.error.ErrorCode;
import com.wx.account.dto.TemplateInfo;
import com.wx.account.exception.BusinessException;
import com.wx.account.model.TemplateMessage;
import com.wx.account.model.TemplateMessageValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedHashMap;
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
     * @param count
     * @param openId
     */
    public static void sendSpreadTemplateMsg(String spreadName, String userNickname, Integer count, String openId) throws Exception {


        TemplateMessage tm = new TemplateMessage();
        tm.setTouser(openId);
        tm.setTemplate_id("EP0aDhuelqGN1IY8liG3fGOarH1fosBnGjSwIylLyjI");

        Map<String, TemplateMessageValue> data = new LinkedHashMap<String, TemplateMessageValue>();

        data.put("title", new TemplateMessageValue("推广详情" + "\n"));

        data.put("spread", new TemplateMessageValue(spreadName));

        data.put("count", new TemplateMessageValue(count.toString()+" 人"));

        tm.setData(data);

        String sendSpreadTemplateUrl = String.format(ConstantUtils.templateSendUrl, ConstantUtils.accessToken);
        String result = HttpUtil.post(sendSpreadTemplateUrl, JSONObject.toJSONString(tm));
        if (!StrUtil.isBlank(result)) {
            JSONObject resultJson = JSONObject.parseObject(result);
            String errcode = resultJson.get("errcode").toString();
            if (!"0".equals(errcode)) {
                logger.info("send spreadUser message is fail and openid is " + spreadName);
            }
        }
    }
}
