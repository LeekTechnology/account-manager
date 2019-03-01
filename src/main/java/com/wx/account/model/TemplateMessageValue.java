package com.wx.account.model;

import java.io.Serializable;

/**
 * 模板消息字段值
 */
public class TemplateMessageValue implements Serializable {

    private static final long serialVersionUID = 5074913673674222244L;

    //属性值
    private String value;

    //属性颜色
    private String color = "#173177";

    public TemplateMessageValue() {

    }

    public TemplateMessageValue(String value) {
        this.value = value;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
