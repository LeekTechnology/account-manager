package com.wx.account.service.impl;

import cn.hutool.core.date.DateUtil;
import com.alibaba.fastjson.JSONObject;
import com.wx.account.Message.WxMsgInfo;
import com.wx.account.Message.messagepackage.Image;
import com.wx.account.Message.messagepackage.ImageMessage;
import com.wx.account.Message.messagepackage.TextMessage;
import com.wx.account.common.enums.KeyWordEnum;
import com.wx.account.common.enums.QrCodeEnum;
import com.wx.account.model.SpreadUser;
import com.wx.account.model.User;
import com.wx.account.service.WxCoreService;
import com.wx.account.util.*;
import org.apache.commons.collections.CollectionUtils;
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
import java.util.Date;
import java.util.List;
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

    @Autowired
    private SpreadUserServiceImpl spreadUserService;

    @Override
    public String wxMessageHandelCoreService(HttpServletRequest req, HttpServletResponse resp) {
        logger.info("------------微信消息开始处理-------------");
        // 返回给微信服务器的消息,默认为null

        String respMessage = null;

        try {

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

            // 文本消息---生成海报
            if (msgType.equals(wxMsgUtil.REQ_MESSAGE_TYPE_TEXT)) {

                //业务中 关键字 处理
                if (StringUtils.isNotBlank(content) && KeyWordEnum.getEnumContent(content)) {

                    //获取用户信息
                    User user = userService.findWxUser(wxMsgInfo.getFromUserName());

                    //查询推广人数
                    if (content.equals(KeyWordEnum.getName(2))) {
                        return scanSubscribeRespMessage(user);
                    }

                    //获取用户头像用于合成二维码
                    String headimgurl = user.getHeadimgurl();

                    BufferedImage bufferedImage = null;
                    if (StringUtils.isNotEmpty(headimgurl)) {

                        //下载头像
                        bufferedImage = ImageIO.read(new URL(headimgurl));

                    }
                    net.sf.json.JSONObject limitQRCode = WeiXinUtil.createLimitQRCode(QrCodeEnum.userSpread.getValue() + "_" + user.getId());

                    //创建二维码，并与用户头像合成新图片
                    QrCodeUtil.createQrCodeImage(limitQRCode.getString("url"), "F:\\images\\11.png", bufferedImage, 300, 300);

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
                    textMessage.setContent("请输入指定文本信息");
                    respMessage = wxMsgUtil.textMessageToXml(textMessage);
                }
            } else if (msgType.equals(wxMsgUtil.REQ_MESSAGE_TYPE_EVENT)) {

                // 事件类型
                String eventType = map.get("Event");

                String eventKey = map.get("EventKey");
                logger.info("eventType------>" + eventType + "and eventKey------>" + eventKey);

                // 关注或扫描带参数二维码
                if (eventType.equals(wxMsgUtil.EVENT_TYPE_SUBSCRIBE) || eventType.equals(wxMsgUtil.EVENT_TYPE_SCAN)) {
                    //respMessage = subscribeAction(wxMsgInfo);
                    respMessage = scanSubscribeAction(wxMsgInfo, eventKey);
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

        String text = "嗨 ，" + user.getNickname() + "小同学，一场高端而又有趣的任务宝之旅正在为你展开。。。\r\n" +
                "成功邀请 10 个好友扫码你的专属任务海报并关注我们，即可拿走A6一辆！\r\n" +
                "成功邀请 20 个好友扫码你的专属任务海报并关注我们，即可拿走A8一辆！\r\n" +
                "\r\n" +
                "数量有限，需要你速战速决！\r\n" +
                "公众号回复【海报】！\r\n" +
                "公众号回复【推广】：查询已推广人数\r\n" +
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
    public String scanSubscribeAction(WxMsgInfo wxMsgInfo, String eventKey) throws Exception {
        String respMessage = null;

        User wxUser = userService.getSubScribeUserInfo(wxMsgInfo.getFromUserName());

        User temp = new User();
        temp.setOpenid(wxMsgInfo.getFromUserName());

        List<User> users = userService.selectList(temp);

        //如果用户不存在则保存当前用户的数据
        if (CollectionUtils.isEmpty(users)) {

            //保存用户信息
            userService.saveUser(wxUser);

            List<User> tempUsers = userService.selectList(temp);

            //保存推广用户关系表
            saveSubscribeBusiness(tempUsers.get(0), eventKey);

            //返回欢迎文本信息
            respMessage = scanSubscribeAction(tempUsers.get(0));

        } else {

            //存在
            User user = users.get(0);
            if (!user.getStatus()) {//取消关注后又扫码关注
                user.setStatus(true);
                userService.update(user);
                respMessage = scanSubscribeAction(users.get(0));
            } else if (StringUtils.isNotBlank(eventKey) && user.getId().toString().equals(eventKey.split("_")[1])) {//自扫行为
                respMessage = scanSubscribeRespMessage(user);
            } else {
                respMessage = scanCommonMessage(user);
            }
        }
        return respMessage;
    }

    public String scanSubscribeRespMessage(User wxUser) throws Exception {

        //保存关注人员的信息
        TextMessage textMessage = new TextMessage();
        textMessage.setToUserName(wxMsgInfo.getFromUserName());
        textMessage.setFromUserName(wxMsgInfo.getToUserName());
        textMessage.setCreateTime(Long.valueOf(DateUtil.format(DateUtil.date(), ConstantUtils.TIME_REQ_PATTERN)));
        textMessage.setMsgType(weixinMessageUtil.RESP_MESSAGE_TYPE_TEXT);
        Integer count = userService.querySpreadCount(wxUser.getId());
        String text = "嗨 ，" + wxUser.getNickname() + "小同学，当前已推广" + count + "人\r\n";

        textMessage.setContent(text);
        return wxMsgUtil.textMessageToXml(textMessage);
    }

    public String scanCommonMessage(User wxUser) throws Exception {

        //保存关注人员的信息
        TextMessage textMessage = new TextMessage();
        textMessage.setToUserName(wxMsgInfo.getFromUserName());
        textMessage.setFromUserName(wxMsgInfo.getToUserName());
        textMessage.setCreateTime(Long.valueOf(DateUtil.format(DateUtil.date(), ConstantUtils.TIME_REQ_PATTERN)));
        textMessage.setMsgType(weixinMessageUtil.RESP_MESSAGE_TYPE_TEXT);
        String text = "嗨，" + wxUser.getNickname() + ", 不能重复关注哦\r\n";

        textMessage.setContent(text);
        return wxMsgUtil.textMessageToXml(textMessage);
    }

    /**
     * 用户关注公众号时的动作
     * 主动关注, 返回文本信息
     *
     * @param wxUser
     * @return
     */
    public String scanSubscribeAction(User wxUser) throws Exception {

        //保存关注人员的信息
        TextMessage textMessage = new TextMessage();
        textMessage.setToUserName(wxMsgInfo.getFromUserName());
        textMessage.setFromUserName(wxMsgInfo.getToUserName());
        textMessage.setCreateTime(Long.valueOf(DateUtil.format(DateUtil.date(), ConstantUtils.TIME_REQ_PATTERN)));
        textMessage.setMsgType(weixinMessageUtil.RESP_MESSAGE_TYPE_TEXT);
        Integer count = userService.querySpreadCount(wxUser.getId());
        String text = "嗨 ，" + wxUser.getNickname() + "小同学，一场高端而又有趣的任务宝之旅正在为你展开。。。\r\n" +
                "成功邀请 10 个好友扫码你的专属任务海报并关注我们，即可拿走A6一辆！\r\n" +
                "成功邀请 20 个好友扫码你的专属任务海报并关注我们，即可拿走A8一辆！\r\n" +
                "当前推广人数！(" + count + "人 )\r\n" +
                "\r\n" +
                "数量有限，需要你速战速决！\r\n" +
                "公众号回复【海报】！\r\n" +
                "公众号回复【推广】：查询已推广人数\r\n" +
                "\r\n" +
                "\r\n" +
                "即将为你生成专属任务海报↓↓↓";

        textMessage.setContent(text);
        return wxMsgUtil.textMessageToXml(textMessage);
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
        /*SpreadUser uo = new SpreadUser();
        uo.setOpenid(user.getOpenid());
        uo.setSubscribe_scene(wxMsgUtil.EVENT_TYPE_SCAN);
        uo.setReference(spreadUser.getOpenid());
        userService.insertSpreadInfo(uo);*/

        //查询推广人的推广次数
        /*Integer spreadNum = userService.querySpreadCount(spreadUser.getOpenid());
        TemplateUtil.sendSpreadTemplateMsg(spreadUser.getNickname(), user.getNickname(), spreadNum, spreadUser.getOpenid());*/
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

    /**
     * 保存关注的相关信息
     *
     * @param temp
     * @param eventKey
     * @return stationId
     */
    private void saveSubscribeBusiness(User temp, String eventKey) throws Exception {
        //判断是否是通过带参数的二维码进行扫描关注的
        if (StringUtils.isNotEmpty(eventKey)) {
            String[] arr = eventKey.split("_");
            //推广营销
            if (StringUtils.equals(Integer.toString(QrCodeEnum.userSpread.getValue()), arr[1])) {
                SpreadUser spreadUser = new SpreadUser();
                User user = userService.getById(Long.valueOf(arr[2]));
                if (user != null) {
                    spreadUser.setUserId(user.getId());//推广人员
                    spreadUser.setSpreadUserId(temp.getId());//被推广人员
                    spreadUser.setCreateDate(new Date());
                    //此处直接保存，没有校验，因为temp一定是一个新用户
                    spreadUserService.save(spreadUser);
                    logger.debug("推广信息：{}", JSONObject.toJSONString(spreadUser));
                }
            }
        }
    }
}
