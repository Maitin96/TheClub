package com.martin.myclub.bean;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.datatype.BmobFile;

/**
 * Created by Martin on 2017/7/17.
 * 反馈和建议的JavaBean
 */
public class Feedback extends BmobObject {
    //反馈内容
    private String content;
    //联系方式
    private String contact;
    //问题图片
    private BmobFile file;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public BmobFile getFile() {
        return file;
    }

    public void setFile(BmobFile file) {
        this.file = file;
    }
}
