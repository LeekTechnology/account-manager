package com.wx.account.model;


import java.io.Serializable;

/**
 * 二维码action_info
 */
public class ActionInfo implements Serializable {

    private Scene scene;

    public ActionInfo(Scene scene) {
        this.scene = scene;
    }

    public Scene getScene() {
        return scene;
    }

    public void setScene(Scene scene) {
        this.scene = scene;
    }
}
