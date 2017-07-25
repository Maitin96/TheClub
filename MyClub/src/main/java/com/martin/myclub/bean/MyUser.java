package com.martin.myclub.bean;

import cn.bmob.v3.BmobUser;

/**
 * Created by Martin on 2017/7/13.
 * 个人资料
 */
public class MyUser extends BmobUser {

    //昵称
    private String name;
    //性别
    private boolean isMan;
    //头像
    private String avatar;
    //背景
    private String background;
    //签名
    private String sign;


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getBackground() {
        return background;
    }

    public void setBackground(String background) {
        this.background = background;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    public boolean isMan() {
        return isMan;
    }

    public void setMan(boolean man) {
        isMan = man;
    }
}
