package com.martin.myclub.bean;

import android.graphics.drawable.Drawable;

import java.util.List;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.datatype.BmobRelation;

/**
 * Created by Martin on 2017/7/10.
 * Powered by Edward
 */
public class DynamicMsg extends BmobObject {

    private MyUser user;
    private String time;
    private String content;
    private String title;

    private BmobFile picture;
    private int look_times = 0;  //浏览次数
    private int comment_counts = 0; //评论数
    private int like_counts = 0;  //点赞数

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getLike_counts() {
        return like_counts;
    }

    public void setLike_counts(int like_counts) {
        this.like_counts = like_counts;
    }

    private BmobRelation likes;
    private List<MyUser> like_users; // 赞了该贴的用户集合
    private BmobRelation collects;
    private List<MyUser> collect_users; // 收藏了该贴的用户集合

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

    public List<MyUser> getLike_users() {
        return like_users;
    }

    public void setLike_users(List<MyUser> like_users) {
        this.like_users = like_users;
    }

    public BmobRelation getLikes() {
        return likes;
    }

    public void setLikes(BmobRelation likes) {
        this.likes = likes;
    }

    public int getComment_counts() {

        return comment_counts;
    }

    public void setComment_counts(int comment_counts) {
        this.comment_counts = comment_counts;
    }

    public int getLook_times() {
        return look_times;
    }

    public void setLook_times(int look_times) {
        this.look_times = look_times;
    }


    public MyUser getUser() {
        return user;
    }

    public void setUser(MyUser user) {
        this.user = user;
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

    public BmobFile getPicture() {
        return picture;
    }

    public void setPicture(BmobFile picture) {
        this.picture = picture;
    }
}
