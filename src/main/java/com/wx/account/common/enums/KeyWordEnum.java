package com.wx.account.common.enums;

import org.apache.commons.lang.BooleanUtils;

import java.util.*;

/**
 * Created by Administrator on 2019/1/6.
 */
public enum KeyWordEnum {

    POSTER_TYPE(1, "海报", true),
    NEWPERSON_TYPE(2, "初来乍到", false),
    PREFERENTIAL_TYPE(3, "优惠多多", false);


    private Integer code;

    private String name;

    private Boolean enable;

    public static KeyWordEnum getKeyWord(Integer type) {
        for (KeyWordEnum anEnum : KeyWordEnum.values()) {
            if (anEnum.getCode().equals(type)) {
                return anEnum;
            }
        }
        return null;
    }

    KeyWordEnum(Integer code, String name, Boolean enable) {
        this.code = code;
        this.name = name;
        this.enable = enable;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean getEnable() {
        return enable;
    }

    public void setEnable(Boolean enable) {
        this.enable = enable;
    }

    public static String getName(Integer code){
        KeyWordEnum payStatus = getEnum(code);
        return payStatus==null?"":payStatus.getName();
    }

    public static KeyWordEnum getEnum(Integer code) {
        KeyWordEnum[] array = KeyWordEnum.values();
        for (KeyWordEnum type : array) {
            if (type.getCode().equals(code)) {
                return type;
            }
        }
        return null;
    }

    public static Map<Integer, String> getMap() {
        HashMap<Integer, String> map = new HashMap<Integer, String>();
        KeyWordEnum[] array = KeyWordEnum.values();
        for (KeyWordEnum type : array) {
            map.put(type.getCode(), type.getName());
        }
        return map;
    }

    public static Map<Integer, String> getMap(boolean enable) {
        HashMap<Integer, String> map = new HashMap<Integer, String>();
        KeyWordEnum[] array = KeyWordEnum.values();
        for (KeyWordEnum type : array) {
            if(BooleanUtils.isTrue(type.getEnable())){
                map.put(type.getCode(), type.getName());
            }
        }
        return map;
    }

    public static List<KeyWordEnum> getList() {
        KeyWordEnum[] array = KeyWordEnum.values();
        return Arrays.asList(array);
    }

    public static List<KeyWordEnum> getList(boolean enable) {
        List<KeyWordEnum> list = new ArrayList<KeyWordEnum>();
        KeyWordEnum[] array = KeyWordEnum.values();
        for (KeyWordEnum type : array) {
            if(BooleanUtils.isTrue(type.getEnable())){
                list.add(type);
            }
        }
        return list;
    }

    public static boolean getEnumContent(String name) {
        KeyWordEnum[] array = KeyWordEnum.values();
        for (KeyWordEnum type : array) {
            if(BooleanUtils.isTrue(type.getEnable())){
                if (type.getName().equals(name)) {
                    return true;
                }
            }
        }
        return false;
    }
}
