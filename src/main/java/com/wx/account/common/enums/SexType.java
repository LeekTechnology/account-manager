package com.wx.account.common.enums;

/**
 * 性别
 * Created by supermrl on 2019/1/20.
 */
public enum SexType {

    BOY(1,"男"),
    GIRL(0,"女");


    private Integer sex;

    private String sexInfo;

    public Integer getSex(){return sex;}

    public String getSexInfo(){return sexInfo;}

    SexType(Integer sex,String sexInfo){
        this.sex = sex;
        this.sexInfo =sexInfo;
    }

    public static String getSexInfo(Integer sex){
        switch (sex){
            case 0:
                return SexType.GIRL.getSexInfo();
            case 1:
                return SexType.BOY.getSexInfo();
            default:
                return SexType.BOY.getSexInfo();
        }
    }
}
