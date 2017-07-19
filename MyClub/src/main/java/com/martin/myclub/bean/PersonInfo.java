package com.martin.myclub.bean;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.datatype.BmobFile;

/**
 * Created by Martin on 2017/7/16.
 * 与Bomb服务器交互的JavaBean，用于个人资料页
 */
public class PersonInfo extends BmobObject {
    private String name;
    private boolean sex;
    private String sign;
    private boolean isAdmin;
    private BmobFile pic;       //头像
    private String myClub;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isSex() {
        return sex;
    }

    public void setSex(boolean sex) {
        this.sex = sex;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    public boolean isAdmin() {
        return isAdmin;
    }

    public void setAdmin(boolean admin) {
        isAdmin = admin;
    }

    public BmobFile getPic() {
        return pic;
    }

    public void setPic(BmobFile pic) {
        this.pic = pic;
    }

    public String getMyClub() {
        return myClub;
    }

    public void setMyClub(String myClub) {
        this.myClub = myClub;
    }
}
