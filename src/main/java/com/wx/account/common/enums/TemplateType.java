package com.wx.account.common.enums;

/**
 * 模板消息配置
 * Created by supermrl on 2019/1/20.
 */
public enum TemplateType {

    SPREAD_SUCCESS(0,"agA3sIDa6O36b4zTJMtkWQKyPdlesmGLNPvT_PPXoBw","推广成功通知");

    private Integer type;

    private String template_id;

    private String template_Title;

    TemplateType(Integer type,String template_id, String template_Title) {
        this.type = type;
        this.template_id = template_id;
        this.template_Title = template_Title;
    }

    public Integer getType() {
        return type;
    }

    public String getTemplate_id() {
        return template_id;
    }

    public String getTemplate_Title() {
        return template_Title;
    }
}
