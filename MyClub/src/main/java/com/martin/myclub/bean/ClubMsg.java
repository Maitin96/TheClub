package com.martin.myclub.bean;

import cn.bmob.v3.datatype.BmobFile;

/**
 * Created by Martin on 2017/7/18.
 */
public class ClubMsg{
    private String clubName;
    private int image;

    public ClubMsg(String clubName, int image) {
        this.clubName = clubName;
        this.image = image;
    }

    public String getClubName() {
        return clubName;
    }

    public void setClubName(String clubName) {
        this.clubName = clubName;
    }

    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }
}
