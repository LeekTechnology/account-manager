package com.wx.account.service.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONObject;
import com.wx.account.Message.WxMsgInfo;
import com.wx.account.Message.messagepackage.Image;
import com.wx.account.Message.messagepackage.ImageMessage;
import com.wx.account.Message.messagepackage.TextMessage;
import com.wx.account.common.enums.KeyWordEnum;
import com.wx.account.dto.TicketInfo;
import com.wx.account.model.SpreadUser;
import com.wx.account.model.User;
import com.wx.account.service.WxCoreService;
import com.wx.account.util.*;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
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
    private static final Logger logger = LoggerFactory.getLogger(WxCoreServiceImpl.class);
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
        logger.info("------------微信消息开始处理-------------");
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

            // 接收内容
            String content = map.get("Content");
            wxMsgInfo.setContent(content);

            // 文本消息
            if (msgType.equals(wxMsgUtil.REQ_MESSAGE_TYPE_TEXT)) {

                //业务中 关键字 处理
                if (StringUtils.isNotBlank(content) && KeyWordEnum.getEnumContent(content)) {

                    //获取用户信息
                    User user = userService.findWxUser(wxMsgInfo.getFromUserName());

                    //获取用户头像用于合成二维码
                    String headimgurl = user.getHeadimgurl();

                    //下载头像
                    BufferedImage bufferedImage = ImageIO.read(new URL(headimgurl));

                    //创建二维码，并与用户头像合成新图片
                    QrCodeUtil.createQrCodeImage(ConstantUtils.SUBSCRIBE_INDEX_URL, "F:\\images\\11.png", bufferedImage, 300, 300);

                    //将图片填充至海报
                    QrCodeUtil.addLogoToQRCodeBound(new File("F:\\image\\55.png"), new File("F:\\images\\11.png"), new LogoConfig());

                    //上传微信服务器
                    JSONObject json = upload("F:\\image\\55.png ", ConstantUtils.accessToken, "image");
                    String mediaId = json.get("media_id").toString();
                    String createdAt = json.get("created_at").toString();

                    //装箱返回图片信息
                    ImageMessage imageMessage = new ImageMessage();
                    imageMessage.setToUserName(fromUserName);
                    imageMessage.setFromUserName(toUserName);
                    imageMessage.setMsgType(wxMsgUtil.RESP_MESSAGE_TYPE_IMAGE);
                    imageMessage.setCreateTime(Long.valueOf(createdAt));
                    Image image = imageMessage.getImage();
                    image.setMediaId(mediaId);
                    respMessage = wxMsgUtil.imageMessageToXml(imageMessage);
                } else {
                    //非自定义关键字，默认返回文本信息
                    TextMessage textMessage = new TextMessage();
                    textMessage.setToUserName(fromUserName);
                    textMessage.setFromUserName(toUserName);
                    textMessage.setMsgType(wxMsgUtil.RESP_MESSAGE_TYPE_TEXT);
                    textMessage.setCreateTime(Long.valueOf(DateUtil.format(DateUtil.date(), ConstantUtils.TIME_REQ_PATTERN)));
                    textMessage.setContent("再点，滚一边去！");
                    respMessage = wxMsgUtil.textMessageToXml(textMessage);
                }
            } else if (msgType.equals(wxMsgUtil.REQ_MESSAGE_TYPE_EVENT)) {

                // 事件类型
                String eventType = map.get("Event");

                String eventKey = map.get("EventKey");
                logger.info("eventType------>" + eventType + "and eventKey------>" + eventKey);
                // 关注
                if (eventType.equals(wxMsgUtil.EVENT_TYPE_SUBSCRIBE)) {
                    respMessage = subscribeAction(wxMsgInfo);
                }
                // 取消关注
                else if (eventType.equals(wxMsgUtil.EVENT_TYPE_UNSUBSCRIBE)) {
                    //更新用户信息
                    userService.unSubscribe(fromUserName);
                    //respContent = fromUserName + "用户取消关注";
                    logger.info("取消关注: openid is " + fromUserName);
                    /*textMessage.setContent(respContent);
                    respMessage = wxMsgUtil.textMessageToXml(textMessage);*/
                }
                // 扫描带参数二维码
                else if (eventType.equals(wxMsgUtil.EVENT_TYPE_SCAN)) {
                    if (!StrUtil.isBlank(eventKey)) {
                        respMessage = scanSubscribeAction(wxMsgInfo, map.get("Ticket"));
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("系统出错");
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
     * 主动关注, 返回文本信息
     *
     * @param wxMsgInfo
     * @return
     */
    public String subscribeAction(WxMsgInfo wxMsgInfo) throws Exception {

        //保存关注人员的信息
        User user = userService.findWxUser(wxMsgInfo.getFromUserName());

        TextMessage textMessage = new TextMessage();
        textMessage.setToUserName(wxMsgInfo.getFromUserName());
        textMessage.setFromUserName(wxMsgInfo.getToUserName());
        textMessage.setCreateTime(Long.valueOf(DateUtil.format(DateUtil.date(), ConstantUtils.TIME_REQ_PATTERN)));
        textMessage.setMsgType(weixinMessageUtil.RESP_MESSAGE_TYPE_TEXT);

        String text = "嗨 ，"+user.getNickname()+"小同学，一场高端而又有趣的任务宝之旅正在为你展开。。。\r\n" +
                "成功邀请 10 个好友扫码你的专属任务海报并关注我们，即可拿走A6一辆！\r\n" +
                "成功邀请 20 个好友扫码你的专属任务海报并关注我们，即可拿走A8一辆！\r\n" +
                "\r\n" +
                "数量有限，需要你速战速决！\r\n" +
                "公众号回复【海报】！\r\n" +
                "\r\n" +
                "\r\n" +
                "即将为你生成专属任务海报↓↓↓";

        textMessage.setContent(text);
        return wxMsgUtil.textMessageToXml(textMessage);
    }

    /**
     * 用户关注公众号时的动作
     * 扫描二维码关注
     *
     * @param wxMsgInfo
     * @return
     */
    public String scanSubscribeAction(WxMsgInfo wxMsgInfo, String ticket) throws Exception {
        String respMessage = null;
        //获取用于推广的二维码信息
        TicketInfo ticketInfo = TicketUtil.getTicketInfo(wxMsgInfo.getFromUserName());
        //保存关注人员的信息
        User user = userService.saveWxUser(wxMsgInfo.getFromUserName(), ticketInfo);

        if (!StrUtil.isBlank(ticket)) {
            //单独向推广人发送文本消息
            dealwithSpreadMessage(user, ticket, wxMsgInfo.getToUserName());
        }

        //返回欢迎信息和转发二维码
        //respMessage = wxMsgModelUtil.followResponseMessageModel(wxMsgInfo, user);
        return respMessage;
    }

    /**
     * 处理返回推广者消息
     *
     * @param user        订阅用户,被推广者
     * @param ticket      推广者ticket
     * @param serviceName 服务号名称
     */
    private void dealwithSpreadMessage(User user, String ticket, String serviceName) throws Exception {
        //查询推广人信息
        User spreadUser = userService.getUserByTicket(ticket);
        if (spreadUser == null) {
            return;
        }
        //插入推广信息
        SpreadUser uo = new SpreadUser();
        uo.setOpenid(user.getOpenid());
        uo.setSubscribe_scene(wxMsgUtil.EVENT_TYPE_SCAN);
        uo.setReference(spreadUser.getOpenid());
        userService.insertSpreadInfo(uo);

        //查询推广人的推广次数
        Integer spreadNum = userService.querySpreadNum(spreadUser.getOpenid());
        TemplateUtil.sendSpreadTemplateMsg(spreadUser.getNickname(), user.getNickname(), spreadNum, spreadUser.getOpenid());
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
