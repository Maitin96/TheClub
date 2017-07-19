package com.martin.myclub.bean;

import android.graphics.drawable.Drawable;

/**
 * Created by Martin on 2017/7/10.
 */
public class DynamicMsg {

    private int headPic;
    private String userName;
    private String time;
    private String content;
    private int picture;

    public int getHeadPic() {
        return headPic;
    }

    public void setHeadPic(int headPic) {
        this.headPic = headPic;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getPicture() {
        return picture;
    }

    public void setPicture(int picture) {
        this.picture = picture;
    }
}
