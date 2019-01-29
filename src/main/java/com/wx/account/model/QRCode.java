package com.wx.account.model;


import java.io.Serializable;

/**
 * 微信二维码基类
 */
public abstract class QRCode implements Serializable {

    protected String action_name;

    protected ActionInfo action_info;

    //默认构造方法，使用此方法构造后必须要设置action_info
    public QRCode() {

    }

    public QRCode(ActionInfo actionInfo) {
        this.action_info = actionInfo;
    }

    public QRCode(Scene scene) {
        this.action_info = new ActionInfo(scene);
    }

    public ActionInfo getAction_info() {
        return action_info;
    }

    public void setAction_info(ActionInfo action_info) {
        this.action_info = action_info;
    }

    public String getAction_name() {
        return action_name;
    }

    public void setAction_name(String action_name) {
        this.action_name = action_name;
    }
}
