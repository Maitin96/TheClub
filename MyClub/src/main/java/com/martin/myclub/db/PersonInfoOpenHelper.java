package com.martin.myclub.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import static android.R.attr.version;

/**
 * Created by Martin on 2017/8/8.
 * 用于在无网络环境或网络环境差的条件下从本地数据库读取用户信息
 */
public class PersonInfoOpenHelper extends SQLiteOpenHelper{

    public PersonInfoOpenHelper(Context context) {
        super(context, "personinfo.db", null, 1);
    }
    /**
     * 本地缓存用户信息
     * 表名：personinfo.db
     * 需要存储信息
     * 昵称：name String
     * 签名：sign String
     * 性别：isMan boolean
     * 头像：avatar String
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE personInfo(objectId varchar(20),name varchar(20),sign varchar(30),isMan bit(1),avatar varchar(100))");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
