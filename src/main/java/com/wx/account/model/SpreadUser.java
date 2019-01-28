package com.wx.account.model;

import lombok.Data;

import java.util.Date;

/**
 * Created by Administrator on 2019/1/28.
 */

@Data
public class SpreadUser {

    private Long id;

    private String openid;

    private String subscribe_scene;

    private String reference;

    private Date operator_time;

    private Integer action;

}
