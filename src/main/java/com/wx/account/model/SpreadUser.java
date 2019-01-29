package com.wx.account.model;

import lombok.Data;

import java.util.Date;

/**
 * Created by Administrator on 2019/1/28.
 */

@Data
public class SpreadUser {

    private Long id;

    private Long userId;

    private Long spreadUserId;

    private Date createDate;
}
