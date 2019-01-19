package com.wx.account.service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by supermrl on 2019/1/19.
 */
public interface WxCoreService {

    /**
     * 处理消息
     * @param req
     * @param resp
     * @return
     */
    String wxMessageHandelCoreService(HttpServletRequest req, HttpServletResponse resp);
}
