package com.martin.myclub.bean;

import java.util.List;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.datatype.BmobRelation;

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

    private BmobRelation goods;
    private List<MyUser> goods_users; // 赞了该贴的用户集合
    private BmobRelation collects;
    private List<MyUser> collect_users; // 收藏了该贴的用户集合

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

    public BmobRelation getGoods() {
        return goods;
    }

    public void setGoods(BmobRelation goods) {
        this.goods = goods;
    }

    public List<MyUser> getGoods_users() {
        return goods_users;
    }

    public void setGoods_users(List<MyUser> goods_users) {
        this.goods_users = goods_users;
    }

    public BmobRelation getCollects() {
        return collects;
    }

    public void setCollects(BmobRelation collects) {
        this.collects = collects;
    }

    public List<MyUser> getCollect_users() {
        return collect_users;
    }

    public void setCollect_users(List<MyUser> collect_users) {
        this.collect_users = collect_users;
    }
}
