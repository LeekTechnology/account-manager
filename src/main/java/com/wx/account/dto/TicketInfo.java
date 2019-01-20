package com.wx.account.dto;

import lombok.Data;

/**
 * 二维码信息
 * Created by supermrl on 2019/1/20.
 */
@Data
public class TicketInfo {

    private String ticket;//唯一标示

    private Integer expire_seconds;//失效时间-仅临时二维码拥有

    private String url;//由此地址可以使用工具转换为二维码图片

    private String ticketUrl;//二维码图片地址



}
