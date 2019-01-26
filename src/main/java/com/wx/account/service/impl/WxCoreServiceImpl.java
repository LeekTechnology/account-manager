package com.wx.account.service.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONObject;
import com.google.common.base.Strings;
import com.wx.account.Message.WxMsgInfo;
import com.wx.account.Message.messagepackage.Image;
import com.wx.account.Message.messagepackage.ImageMessage;
import com.wx.account.Message.messagepackage.TextMessage;
import com.wx.account.dto.TicketInfo;
import com.wx.account.model.User;
import com.wx.account.model.UserOther;
import com.wx.account.service.WxCoreService;
import com.wx.account.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.util.Map;

/**
 * Created by supermrl on 2019/1/19.
 */

@Service
public class WxCoreServiceImpl implements WxCoreService {
	private static final Logger log = LoggerFactory.getLogger(WxCoreServiceImpl.class);
    @Autowired
    private WxMsgUtil wxMsgUtil;
    @Autowired
    private WxMsgInfo wxMsgInfo;
    @Autowired
    private WxMsgUtil weixinMessageUtil;
    @Autowired
    private WxMsgModelUtil wxMsgModelUtil;
    @Autowired
    private UserServiceImpl userService;



    @Override
    public String wxMessageHandelCoreService(HttpServletRequest req, HttpServletResponse resp) {
        log.info("------------微信消息开始处理-------------");
        // 返回给微信服务器的消息,默认为null

        String respMessage = null;

        try {

            // 默认返回的文本消息内容
            String respContent = null;
            // xml分析
            // 调用消息工具类MessageUtil解析微信发来的xml格式的消息，解析的结果放在HashMap里；
            Map<String, String> map = wxMsgUtil.parseXml(req);
            // 发送方账号
            String fromUserName = map.get("FromUserName");
            wxMsgInfo.setFromUserName(fromUserName);
            // 接受方账号（公众号）
            String toUserName = map.get("ToUserName");
            wxMsgInfo.setToUserName(toUserName);
            // 消息类型
            String msgType = map.get("MsgType");
            wxMsgInfo.setMessageType(msgType);

            // 默认回复文本消息
            TextMessage textMessage = new TextMessage();
            textMessage.setToUserName(fromUserName);
            textMessage.setFromUserName(toUserName);
            textMessage.setCreateTime(Long.valueOf(DateUtil.format(DateUtil.date(), ConstantUtils.TIME_REQ_PATTERN)));
            textMessage.setMsgType(wxMsgUtil.RESP_MESSAGE_TYPE_TEXT);
            textMessage.setFuncFlag(0);

            // 分析用户发送的消息类型，并作出相应的处理

            // 文本消息
            if (msgType.equals(wxMsgUtil.REQ_MESSAGE_TYPE_TEXT)) {
                /*respContent = "亲，这是文本消息！";
                textMessage.setContent(respContent);
                respMessage = wxMsgUtil.textMessageToXml(textMessage);*/

                String infoUrl = String.format(ConstantUtils.ticketInfo, ConstantUtils.accessToken);
                QrCodeUtil.createQrCodeImage(infoUrl, "G:\\image\\9.png", "G:\\image\\2.png" , 300, 300);
                QrCodeUtil.addLogoToQRCode(new File("G:\\image\\6.png"), new File("G:\\image\\9.png"), new LogoConfig());

                JSONObject json = upload("G:\\image\\6.png ", ConstantUtils.accessToken, "image");
                String mediaId = json.get("media_id").toString();
                String createdAt = json.get("created_at").toString();

                ImageMessage imageMessage = new ImageMessage();
                imageMessage.setToUserName(fromUserName);
                imageMessage.setFromUserName(toUserName);
                imageMessage.setMsgType(wxMsgUtil.RESP_MESSAGE_TYPE_IMAGE);
                imageMessage.setCreateTime(Long.valueOf(createdAt));
                Image image = imageMessage.getImage();
                image.setMediaId(mediaId);

                respMessage = wxMsgUtil.imageMessageToXml(imageMessage);
            }

            // 图片消息
            else if (msgType.equals(wxMsgUtil.REQ_MESSAGE_TYPE_IMAGE)) {
                respContent = "您发送的是图片消息！";
                textMessage.setContent(respContent);
                respMessage = wxMsgUtil.textMessageToXml(textMessage);
            }

            // 语音消息
            else if (msgType.equals(wxMsgUtil.REQ_MESSAGE_TYPE_VOICE)) {
                respContent = "您发送的是语音消息！";
                textMessage.setContent(respContent);
                respMessage = wxMsgUtil.textMessageToXml(textMessage);
            }

            // 视频消息
            else if (msgType.equals(wxMsgUtil.REQ_MESSAGE_TYPE_VIDEO)) {
                respContent = "您发送的是视频消息！";
                textMessage.setContent(respContent);
                respMessage = wxMsgUtil.textMessageToXml(textMessage);
            }

            // 地理位置消息
            else if (msgType.equals(wxMsgUtil.REQ_MESSAGE_TYPE_LOCATION)) {
                respContent = "您发送的是地理位置消息！";
                textMessage.setContent(respContent);
                respMessage = wxMsgUtil.textMessageToXml(textMessage);
            }

            // 链接消息
            else if (msgType.equals(wxMsgUtil.REQ_MESSAGE_TYPE_LINK)) {
                respContent = "您发送的是链接消息！";
                textMessage.setContent(respContent);
                respMessage = wxMsgUtil.textMessageToXml(textMessage);
            }

            // 事件推送(当用户主动点击菜单，或者扫面二维码等事件)
            else if (msgType.equals(wxMsgUtil.REQ_MESSAGE_TYPE_EVENT)) {

                // 事件类型
                String eventType = map.get("Event");
                
                String eventKey = map.get("EventKey");//enum定义的动作标识
                log.info("eventType------>" + eventType + "and eventKey------>" + eventKey);
                // 关注
                if (eventType.equals(wxMsgUtil.EVENT_TYPE_SUBSCRIBE)) {
                    respMessage = subscribeAction(wxMsgInfo, map.get("Ticket"));
                }
                // 取消关注
                else if (eventType.equals(wxMsgUtil.EVENT_TYPE_UNSUBSCRIBE)) {
                    userService.unSubscribe(fromUserName);
                    respContent = fromUserName+"用户取消关注";
                    log.info("取消关注: openid is "+fromUserName);
                    textMessage.setContent(respContent);
                    respMessage = wxMsgUtil.textMessageToXml(textMessage);
                }
                // 扫描带参数二维码
                else if (eventType.equals(wxMsgUtil.EVENT_TYPE_SCAN)) {
                    if(!Strings.isNullOrEmpty(eventKey)){
                        respMessage = subscribeAction(wxMsgInfo,map.get("Ticket"));
                    }
                }
                /*else if(eventType.equals(wxMsgUtil.EVENT_TYPE_SCAN_SELF)) {
                	log.info("本人扫描自己的二维码");
                	respContent = "亲，您已经关注了！<a href = 'https://www.baidu.com/'>点击</a>";
                    textMessage.setContent(respContent);
                    respMessage = wxMsgUtil.textMessageToXml(textMessage);
                }*/
                // 上报地理位置
                else if (eventType.equals(wxMsgUtil.EVENT_TYPE_LOCATION)) {
                    log.info("上报地理位置");
                }
                // 自定义菜单（点击菜单拉取消息）
                else if (eventType.equals(wxMsgUtil.EVENT_TYPE_CLICK)) {

                    // 事件KEY值，与创建自定义菜单时指定的KEY值对应
                    log.info("eventKey------->" + eventKey);

                }
                // 自定义菜单（(自定义菜单URl视图)）
                else if (eventType.equals(wxMsgUtil.EVENT_TYPE_VIEW)) {
                    log.info("处理自定义菜单URI视图");
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
            log.error("系统出错");
            respMessage = null;
        } finally {
            if (null == respMessage) {
                respMessage = wxMsgModelUtil.systemErrorResponseMessageModel(wxMsgInfo);
            }
        }

        return respMessage;
    }


    /**
     * 用户关注公众号时的动作
     * 包含主动关注,扫描二维码关注
     *
     * @param wxMsgInfo
     * @param ticket 推广人的ticket
     * @return
     */
    public String subscribeAction(WxMsgInfo wxMsgInfo, String ticket) {
        String respMessage = null;
        //获取用于推广的二维码信息
        TicketInfo ticketInfo = TicketUtil.getTicketInfo(wxMsgInfo.getFromUserName());
        //保存关注人员的信息
        User user = userService.saveWxUser(wxMsgInfo.getFromUserName(), ticketInfo);

        if(!Strings.isNullOrEmpty(ticket)){
            //单独向推广人发送文本消息
            dealwithSpreadMessage(user,ticket,wxMsgInfo.getToUserName());
        }

        //返回欢迎信息和转发二维码
        respMessage = wxMsgModelUtil.followResponseMessageModel(wxMsgInfo, user);
        return respMessage;
    }

    /**
     * 处理返回推广者消息
     * @param user 订阅用户,被推广者
     * @param ticket 推广者ticket
     * @param serviceName 服务号名称
     */
    private void dealwithSpreadMessage(User user, String ticket, String serviceName) {
        //查询推广人信息
        User spreadUser = userService.getUserByTicket(ticket);
        if(spreadUser == null){
            return;
        }
        //插入推广信息
        UserOther uo = new UserOther();
        uo.setOpenid(user.getOpenid());
        uo.setSubscribe_scene(wxMsgUtil.EVENT_TYPE_SCAN);
        uo.setReference(spreadUser.getOpenid());
        userService.insertSpreadInfo(uo);

        //查询推广人的推广次数
        Integer spreadNum = userService.querySpreadNum(spreadUser.getOpenid());
        TemplateUtil.sendSpreadTemplateMsg(spreadUser.getNickname(),user.getNickname(),spreadNum,spreadUser.getOpenid());
    }

    /**
     * 上传本地文件到微信获取mediaId
     */

    private static JSONObject upload(String filePath, String accessToken, String type) throws IOException, NoSuchAlgorithmException, NoSuchProviderException, KeyManagementException {
        File file = new File(filePath);
        if (!file.exists() || !file.isFile()) {
            throw new IOException("文件不存在");
        }

        String url = ConstantUtils.UPLOAD_IMG_URL.replace("ACCESS_TOKEN", accessToken).replace("TYPE", type);

        URL urlObj = new URL(url);
        //连接
        HttpURLConnection con = (HttpURLConnection) urlObj.openConnection();

        con.setRequestMethod("POST");
        con.setDoInput(true);
        con.setDoOutput(true);
        con.setUseCaches(false);

        //设置请求头信息
        con.setRequestProperty("Connection", "Keep-Alive");
        con.setRequestProperty("Charset", "UTF-8");

        //设置边界
        String BOUNDARY = "----------" + System.currentTimeMillis();
        con.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + BOUNDARY);

        StringBuilder sb = new StringBuilder();
        sb.append("--");
        sb.append(BOUNDARY);
        sb.append("\r\n");
        sb.append("Content-Disposition: form-data;name=\"file\";filename=\"" + file.getName() + "\"\r\n");
        sb.append("Content-Type:application/octet-stream\r\n\r\n");

        byte[] head = sb.toString().getBytes("utf-8");

        //获得输出流
        OutputStream out = new DataOutputStream(con.getOutputStream());
        //输出表头
        out.write(head);

        //文件正文部分
        //把文件已流文件的方式 推入到url中
        DataInputStream in = new DataInputStream(new FileInputStream(file));
        int bytes = 0;
        byte[] bufferOut = new byte[1024];
        while ((bytes = in.read(bufferOut)) != -1) {
            out.write(bufferOut, 0, bytes);
        }
        in.close();

        //结尾部分
        byte[] foot = ("\r\n--" + BOUNDARY + "--\r\n").getBytes("utf-8");//定义最后数据分隔线

        out.write(foot);

        out.flush();
        out.close();

        StringBuffer buffer = new StringBuffer();
        BufferedReader reader = null;
        String result = null;
        try {
            //定义BufferedReader输入流来读取URL的响应
            reader = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String line = null;
            while ((line = reader.readLine()) != null) {
                buffer.append(line);
            }
            if (result == null) {
                result = buffer.toString();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                reader.close();
            }
        }

        JSONObject json = JSONObject.parseObject(result);
        return json;
    }

}
