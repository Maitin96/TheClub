package com.martin.myclub.bean;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.datatype.BmobRelation;

/**
 * Created by Administrator on 2017/10/5.
 */

public class CLubAnnouncement extends BmobObject{

    private MyUser user;
    private String clubObjId;
    private String title;
    private String content;
    private BmobRelation readedMember;
    private int readedCount;
    private BmobFile pic;

    public BmobFile getPic() {
        return pic;
    }

    public void setPic(BmobFile pic) {
        this.pic = pic;
    }

    public MyUser getUser() {
        return user;
    }

    public void setUser(MyUser user) {
        this.user = user;
    }

    public String getClubObjId() {
        return clubObjId;
    }

    public void setClubObjId(String clubObjId) {
        this.clubObjId = clubObjId;
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

    public BmobRelation getReadedMember() {
        return readedMember;
    }

    public void setReadedMember(BmobRelation readedMember) {
        this.readedMember = readedMember;
    }

    public int getReadedCount() {
        return readedCount;
    }

    public void setReadedCount(int readedCount) {
        this.readedCount = readedCount;
    }
}
