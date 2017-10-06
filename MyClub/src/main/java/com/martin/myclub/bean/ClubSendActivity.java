package com.martin.myclub.bean;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.datatype.BmobRelation;

/**
 * Created by 1033834071 on 2017/9/26.
 */

public class ClubSendActivity extends BmobObject {

    private MyUser user;
    private String clubId;
    private String title;
    private String content;
    private String start_time;
    private String end_time;
    private int readedCount;
    private BmobFile pic;
    private BmobRelation member;   // 已读成员

    public BmobRelation getMember() {
        return member;
    }

    public void setMember(BmobRelation member) {
        this.member = member;
    }

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

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getStart_time() {
        return start_time;
    }

    public void setStart_time(String start_time) {
        this.start_time = start_time;
    }

    public String getEnd_time() {
        return end_time;
    }

    public void setEnd_time(String end_time) {
        this.end_time = end_time;
    }

    public BmobFile getPic() {
        return pic;
    }

    public void setPic(BmobFile pic) {
        this.pic = pic;
    }

    public int getReadedCount() {
        return readedCount;
    }

    public void setReadedCount(int readedCount) {
        this.readedCount = readedCount;
    }
}
