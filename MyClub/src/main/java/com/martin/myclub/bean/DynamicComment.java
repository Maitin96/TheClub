package com.martin.myclub.bean;

import cn.bmob.v3.BmobObject;

/**
 * 评论bean
 * Created by Edward on 2017/7/23.
 */

public class DynamicComment extends BmobObject{
    private String dyObjId;
    private MyUser user;
    private String target_comm_username;
    private String comment;

    public String getTarget_comm_username() {
        return target_comm_username;
    }

    public void setTarget_comm_username(String target_comm_username) {
        this.target_comm_username = target_comm_username;
    }

    private String target_comm_id;

    public String getTarget_comm_id() {
        return target_comm_id;
    }

    public void setTarget_comm_id(String target_comm_id) {
        this.target_comm_id = target_comm_id;
    }

    public String getDyObjId() {
        return dyObjId;
    }

    public void setDyObjId(String dyObjId) {
        this.dyObjId = dyObjId;
    }

    public MyUser getUser() {
        return user;
    }

    public void setUser(MyUser user) {
        this.user = user;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
