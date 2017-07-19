package com.martin.myclub.bean;

/**
 * Created by Martin on 2017/7/17.
 */
public class ChatMsg {
    private boolean myInfo;
    private int iconID;
    private String username;
    private String content;
    private String chatObj;

    public boolean isMyInfo() {
        return myInfo;
    }

    public void setMyInfo(boolean myInfo) {
        this.myInfo = myInfo;
    }

    public int getIconID() {
        return iconID;
    }

    public void setIconID(int iconID) {
        this.iconID = iconID;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getChatObj() {
        return chatObj;
    }

    public void setChatObj(String chatObj) {
        this.chatObj = chatObj;
    }
}
