package com.martin.myclub.bean;

import cn.bmob.v3.BmobObject;

/**
 *
 * Created by 1033834071 on 2017/9/27.
 */

public class ApplyToAddClub extends BmobObject{
    private MyUser user;
    private String Content;
    private String clubId;

    public MyUser getUser() {
        return user;
    }

    public void setUser(MyUser user) {
        this.user = user;
    }

    public String getContent() {
        return Content;
    }

    public void setContent(String content) {
        Content = content;
    }

    public String getClubId() {
        return clubId;
    }

    public void setClubId(String clubId) {
        this.clubId = clubId;
    }
}
