package com.wx.account.model;



/**
 * 字符串场景永久二维码
 */
public class LimitStrQRCode extends QRCode {

    public static final String QR_LIMIT_STR_SCENE = "QR_LIMIT_STR_SCENE";//str场景的action_name

    public LimitStrQRCode() {
        super.action_name = QR_LIMIT_STR_SCENE;
    }

    public LimitStrQRCode(ActionInfo actionInfo) {
        super(actionInfo);
        this.action_name = QR_LIMIT_STR_SCENE;
    }

    public LimitStrQRCode(Scene scene) {
        super(scene);
        this.action_name = QR_LIMIT_STR_SCENE;
    }
}
