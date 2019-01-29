package com.wx.account.util;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;
import org.apache.commons.io.IOUtils;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import javax.servlet.http.HttpServletRequest;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * 微信请求消息工具类
 */
public class ReqMessageUtil {

    /**
     * 请求消息类型：文本
     */
    public static final String MESSAGE_TYPE_TEXT = "text";

    /**
     * 请求消息类型：图片
     */
    public static final String MESSAGE_TYPE_IMAGE = "image";

    /**
     * 请求消息类型：链接
     */
    public static final String MESSAGE_TYPE_LINK = "link";

    /**
     * 请求消息类型：地理位置
     */
    public static final String MESSAGE_TYPE_LOCATION = "location";

    /**
     * 请求消息类型：音频
     */
    public static final String MESSAGE_TYPE_VOICE = "voice";

    /**
     * 请求消息类型：推送
     */
    public static final String MESSAGE_TYPE_EVENT = "event";

    /**
     * 事件类型：subscribe(订阅)
     */
    public static final String EVENT_TYPE_SUBSCRIBE = "subscribe";

    /**
     * 事件类型：unsubscribe(取消订阅)
     */
    public static final String EVENT_TYPE_UNSUBSCRIBE = "unsubscribe";

    /**
     * 事件类型：CLICK(自定义菜单点击事件)
     */
    public static final String EVENT_TYPE_CLICK = "CLICK";

    /**
     * 扫描事件:click(自定义菜单点击事件)
     */
    public static final String SCANCODE_PUSH = "scancode_push";

    /**
     * 领取事件
     */
    public static final String USER_GET_CARD = "user_get_card";

    /**
     * 用户进入会员卡事件
     */
    public static final String USER_VIEW_CARD = "user_view_card";


    /**
     * 解析微信发来的请求（XML）
     *
     * @param request
     * @return
     * @throws Exception
     */
    @SuppressWarnings("unchecked")
    public static Map<String, String> parseXml(HttpServletRequest request) {
        Map<String, String> map = null;
        InputStream inputStream = null;
        try {
            // 将解析结果存储在HashMap中
            map = new TreeMap<String, String>();

            // 从request中取得输入流
            inputStream = request.getInputStream();
            // 读取输入流
            SAXReader reader = new SAXReader();
            //防XML外部实体注入
            reader.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
            reader.setFeature("http://xml.org/sax/features/external-general-entities", false);
            reader.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
            Document document = reader.read(inputStream);
            // 得到xml根元素
            Element root = document.getRootElement();
            // 得到根元素的所有子节点
            List<Element> elementList = root.elements();

            // 遍历所有子节点
            for (Element e : elementList) {
                map.put(e.getName(), e.getText());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            IOUtils.closeQuietly(inputStream);
        }

        return map;
    }


    /**
     * 获取请求的消息类型
     *
     * @param request
     * @return
     */
    public String getMsgType(HttpServletRequest request) {
        InputStream inputStream = null;
        try {
            inputStream = request.getInputStream();
            return new SAXReader().read(inputStream).getRootElement().selectSingleNode("MsgType").getText();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            IOUtils.closeQuietly(inputStream);
        }
        return null;
    }


    /**
     * 转化request的xml输入流为对象
     *
     * @param request
     * @param cls
     * @param <T>
     * @return
     */
    public static <T> T xmlToBean(HttpServletRequest request, Class<T> cls) {
        InputStream inputStream = null;
        try {
//            InputStream inputStream = request.getInputStream();
//            SAXReader reader = new SAXReader();
//            Document document = reader.read(inputStream);
//            Element rootElement = document.getRootElement();
//            String xml = rootElement.asXML();
            inputStream = request.getInputStream();
            return xmlToBean(new SAXReader().read(inputStream).getRootElement().asXML(), cls);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            IOUtils.closeQuietly(inputStream);
        }
        return null;
    }


    /**
     * 转化xml的string为对象
     *
     * @param s
     * @param cls
     * @param <T>
     * @return
     */
    @SuppressWarnings("unchecked")
    public static <T> T xmlToBean(String s, Class<T> cls) {
        try {
            XStream xstream = new XStream(new DomDriver());
            xstream.alias("xml", cls);
            return (T) xstream.fromXML(s);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
