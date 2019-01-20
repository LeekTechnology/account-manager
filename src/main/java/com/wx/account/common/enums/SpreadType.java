package com.wx.account.common.enums;

/**
 * 推广类型
 * Created by supermrl on 2019/1/20.
 */
public enum SpreadType {

    TICKET(0);//二维码推广关注


    private Integer type;

    public Integer getType() {
        return type;
    }


    SpreadType(Integer type) {
        this.type = type;
    }
}
