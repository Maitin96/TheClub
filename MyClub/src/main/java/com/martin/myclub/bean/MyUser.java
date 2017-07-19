package com.martin.myclub.bean;

import cn.bmob.v3.BmobUser;

/**
 * Created by Martin on 2017/7/13.
 */
public class MyUser extends BmobUser {
    private boolean sex;

    public boolean isSex() {
        return sex;
    }

    public void setSex(boolean sex) {
        this.sex = sex;
    }
}
