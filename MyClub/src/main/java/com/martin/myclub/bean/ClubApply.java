package com.martin.myclub.bean;

import android.support.annotation.NonNull;

import com.martin.myclub.util.Cn2Spell;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.datatype.BmobRelation;

/**
 * 社团申请表
 * Created by Edward on 2017/8/15.
 */

public class ClubApply extends BmobObject implements Comparable<ClubApply>{

    private String pinyin; // 姓名对应的拼音
    private String firstLetter; // 拼音的首字母



    private MyUser user;
    private String name;   //社团名称
    private String introduction;  //社团简介
    private BmobFile logo; // 社团logo
    private BmobFile bg;  //社团封面背景

    private String phone;   //社团联系电话
    private String Email;  //社团邮箱
    private String QQ;   //社团QQ
    private String remarks;   // 备注

    private String realName;  // 申请人真实姓名
    private String position;  //社团内的职务
    private String applicatPhone; //  申请人的电话
    private String applicatQQ;  //  申请人的QQ

    private BmobRelation admin; //社团管理员
    private BmobRelation member;  //社团成员

    public BmobRelation getAdmin() {
        return admin;
    }

    public void setAdmin(BmobRelation admin) {
        this.admin = admin;
    }

    public String getIntroduction() {
        return introduction;
    }

    public void setIntroduction(String introduction) {
        this.introduction = introduction;
    }

    public BmobFile getBg() {
        return bg;
    }

    public void setBg(BmobFile bg) {
        this.bg = bg;
    }

    public BmobFile getLogo() {
        return logo;
    }

    public void setLogo(BmobFile logo) {
        this.logo = logo;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public String getQQ() {
        return QQ;
    }

    public void setQQ(String QQ) {
        this.QQ = QQ;
    }

    public MyUser getUser() {
        return user;
    }

    public void setUser(MyUser user) {
        this.user = user;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getApplicatPhone() {
        return applicatPhone;
    }

    public void setApplicatPhone(String applicatPhone) {
        this.applicatPhone = applicatPhone;
    }

    public String getApplicatQQ() {
        return applicatQQ;
    }

    public void setApplicatQQ(String applicatQQ) {
        this.applicatQQ = applicatQQ;
    }

    public BmobRelation getMember() {
        return member;
    }

    public void setMember(BmobRelation member) {
        this.member = member;
    }



    public String getPinyin() {
        return pinyin;
    }

    public String getFirstLetter() {
        return firstLetter;
    }

    public void exec(){
        pinyin = Cn2Spell.getPinYin(name); // 根据姓名获取拼音
        firstLetter = pinyin.substring(0, 1).toUpperCase(); // 获取拼音首字母并转成大写
        if (!firstLetter.matches("[A-Z]")) { // 如果不在A-Z中则默认为“#”
            firstLetter = "#";
        }
    }

    @Override
    public int compareTo(@NonNull ClubApply club) {
        if (firstLetter.equals("#") && !club.getFirstLetter().equals("#")) {
            return 1;
        } else if (!firstLetter.equals("#") && club.getFirstLetter().equals("#")){
            return -1;
        } else {
            return pinyin.compareToIgnoreCase(club.getPinyin());
        }
    }
}
