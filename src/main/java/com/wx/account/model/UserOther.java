package com.wx.account.model;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Date;

@Data
public class UserOther{
    private Long id;

    private String openid;

    private String subscribe_scene;

    private String reference;

    private Date operator_time;

    private Integer action;

}