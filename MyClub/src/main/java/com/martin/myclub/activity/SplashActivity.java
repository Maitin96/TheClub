package com.martin.myclub.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.Toast;

import cn.bmob.sms.BmobSMS;
import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobUser;

import com.martin.myclub.R;

/**
 * Splash闪屏页面
 * 每次进入应用时显示
 * 判断是否为第一次进入，而进入引导页面或者主页面
 */
public class SplashActivity extends AppCompatActivity {

    private static final int GO_MAIN = 0;
    private static final int GO_GUIDE = 1;
    private static final int GO_IN = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initBomb();
        initView();
        initLoad();
    }

    private void initView() {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash);
    }

    private void initLoad(){
        SharedPreferences sp = getSharedPreferences("myClub",MODE_PRIVATE);
        boolean isFirstEnter = sp.getBoolean("isFirstEnter",true);
        if (isFirstEnter){
            handler.sendEmptyMessageDelayed(GO_GUIDE,2000);
            SharedPreferences.Editor editor = sp.edit();
            editor.putBoolean("isFirstEnter",false);
            editor.apply();
        } else {
            //判断用户是否已经登录
            BmobUser bmobUser = BmobUser.getCurrentUser();
            if (bmobUser != null){
                handler.sendEmptyMessageDelayed(GO_IN,2000);
            } else {
                handler.sendEmptyMessageDelayed(GO_MAIN,2000);
            }
        }
    }

    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case GO_MAIN:
                    goMain();
                    break;
                case GO_GUIDE:
                    goGuide();
                    break;
                case GO_IN:
                    Intent intent = new Intent(SplashActivity.this,MainActivity.class);
                    startActivity(intent);
                    finish();
                    break;
            }
        }
    };

    private void goMain(){
        Intent intent = new Intent(SplashActivity.this,LoginActivity.class);
        startActivity(intent);
        finish();
    }

    private void goGuide(){
        Intent intent = new Intent(SplashActivity.this,GuideActivity.class);
        startActivity(intent);
        finish();
    }

    private void initBomb() {
        //Bmob服务器初始化
        Bmob.initialize(this, "efcbb4f650eca73b83b11fd45dfc70b2", "bmob");
        //Bmob信息初始化
        BmobSMS.initialize(this,"efcbb4f650eca73b83b11fd45dfc70b2");
    }
}