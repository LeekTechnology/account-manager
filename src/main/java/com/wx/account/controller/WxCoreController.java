package com.wx.account.controller;

import cn.hutool.core.util.StrUtil;
import com.wx.account.service.impl.WxCoreServiceImpl;
import com.wx.account.util.WxMsgUtil;
import com.wx.account.util.WxSignUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

/**
 * 微信处理消息
 * Created by supermrl on 2019/1/19.
 */
@RestController
@RequestMapping(value = "/leek/wx")
public class WxCoreController {

    private static Logger log = LoggerFactory.getLogger(WxCoreController.class);


    @Autowired
    private WxSignUtil wxSignUtil;

    @Autowired
    private WxMsgUtil wxMsgUtil;

    @Autowired
    private WxCoreServiceImpl wxCoreService;

    /**
     * 微信验证服务器
     *
     * @param req
     * @return
     * @throws IOException
     */
    @RequestMapping(value = "/access" ,method = RequestMethod.GET)
    public String validateToken(ServletRequest req) throws IOException {
        log.info("----开始校验签名----");

        /**
         * 接收微信服务器发送请求时传递过来的参数
         */
        String signature = req.getParameter("signature");
        String timestamp = req.getParameter("timestamp");
        String nonce = req.getParameter("nonce"); //随机数
        String echostr = req.getParameter("echostr");//随机字符串

        /**
         * 将token、timestamp、nonce三个参数进行字典序排序
         * 并拼接为一个字符串
         */
        String sortStr = wxSignUtil.sort(timestamp, nonce);
        /**
         * 字符串进行shal加密
         */
        String mySignature = wxSignUtil.shal(sortStr);
        /**
         * 校验微信服务器传递过来的签名 和  加密后的字符串是否一致, 若一致则签名通过
         */
        if (!"".equals(signature) && !"".equals(mySignature) && signature.equals(mySignature)) {
            log.info("-----签名校验通过-----");
            return echostr;
        } else {
            log.info("-----校验签名失败-----");
            return "校验签名失败";
        }
    }


    @RequestMapping(value = "/access" ,method = RequestMethod.POST)
    public String getWxMessage(HttpServletRequest req, HttpServletResponse resp) throws UnsupportedEncodingException {
        log.info("----------------开始处理微信发过来的消息------------------");
        // 微信服务器POST消息时用的是UTF-8编码，在接收时也要用同样的编码，否则中文会乱码；
        req.setCharacterEncoding("UTF-8");
        // 在响应消息（回复消息给用户）时，也将编码方式设置为UTF-8，原理同上；
        resp.setCharacterEncoding("UTF-8");
        String respXml = wxCoreService.wxMessageHandelCoreService(req, resp);
        if (StrUtil.isBlank(respXml)){
            log.error("-------------处理微信消息失败-----------------------");
            return "处理微信消息失败";
        }else {
            log.info("----------返回微信消息处理结果-----------------------:"+respXml);
            return respXml;
        }
    }

}
