package com.wx.account.model;

import java.io.Serializable;
import java.util.Map;

/**
 * 模板消息
 */
public class TemplateMessage implements Serializable {

    public static final String TEMPLATE_ID_CACHE_NAME = "templateId";

    private static final long serialVersionUID = 6192456115751648104L;

    //用户openid
    private String touser;

    //模板id
    private String template_id;

    //模板消息打开的url
    private String url;

    //标题颜色
    private String topcolor = "#FF0000";

    //模板数据
    private Map<String, TemplateMessageValue> data;

    public Map<String, TemplateMessageValue> getData() {
        return data;
    }

    public void setData(Map<String, TemplateMessageValue> data) {
        this.data = data;
    }

    public String getTemplate_id() {
        return template_id;
    }

    public void setTemplate_id(String template_id) {
        this.template_id = template_id;
    }

    public String getTopcolor() {
        return topcolor;
    }

    public void setTopcolor(String topcolor) {
        this.topcolor = topcolor;
    }

    public String getTouser() {
        return touser;
    }

    public void setTouser(String touser) {
        this.touser = touser;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
