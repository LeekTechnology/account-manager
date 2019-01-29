package com.wx.account.common.enums;

/**
 * 二维码前缀
 */
public enum QrCodeEnum {

    //加油员营销二维码标识
    userSpread(1);

    int value;

    QrCodeEnum(int value) {
        this.value = value;
    }

    public int getValue() {
        return this.value;
    }

}
