package com.wx.account.common.enums;

/**
 * 模板消息配置
 * 自定义模板--只需要配置title即可，系统根据title找到template_id。
 * 务必保证模板标题正确
 * Created by supermrl on 2019/1/20.
 */
public enum TemplateType {

    SPREAD_SUCCESS(0,"推广成功通知");

    private Integer type;

    private String template_Title;

    TemplateType(Integer type, String template_Title) {
        this.type = type;
        this.template_Title = template_Title;
    }

    public Integer getType() {
        return type;
    }

    public String getTemplate_Title() {
        return template_Title;
    }


}
