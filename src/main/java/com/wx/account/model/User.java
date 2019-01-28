package com.wx.account.model;

import lombok.Data;

import java.util.Date;

@Data
public class User{
    private Long id;

    private String openid;

    private String nickname;

    private Integer sex;

    private String city;

    private String country;

    private String province;

    private String headimgurl;

    private Date subscribe_time;

    private Boolean status;

    private String ticket;

}