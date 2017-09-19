package com.martin.myclub.bean;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.datatype.BmobFile;

/**
 * 社团内部的动态
 * Created by Edward on 2017/9/13.
 */

public class ClubDynamic extends BmobObject{
    private MyUser user;
    private String clubId;
    private String content;
    private BmobFile picture;
    private String identity;
    private int lookedTime;
    private int good;
    private int comment;

    public MyUser getUser() {
        return user;
    }

    public void setUser(MyUser user) {
        this.user = user;
    }

    public String getClubId() {
        return clubId;
    }

    public void setClubId(String clubId) {
        this.clubId = clubId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public BmobFile getPicture() {
        return picture;
    }

    public void setPicture(BmobFile picture) {
        this.picture = picture;
    }

    public String getIdentity() {
        return identity;
    }

    public void setIdentity(String identity) {
        this.identity = identity;
    }

    public int getLookedTime() {
        return lookedTime;
    }

    public void setLookedTime(int lookedTime) {
        this.lookedTime = lookedTime;
    }

    public int getGood() {
        return good;
    }

    public void setGood(int good) {
        this.good = good;
    }

    public int getComment() {
        return comment;
    }

    public void setComment(int comment) {
        this.comment = comment;
    }
}
