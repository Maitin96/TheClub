package com.martin.myclub.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.martin.myclub.R;

import cn.bmob.v3.BmobUser;

/**
 * Created by Martin on 2017/7/13.
 * 设置页面
 */
public class SettingActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        initViews();
    }

    private void initViews(){
        Button btSignOut = (Button) findViewById(R.id.bt_sign_out);
        btSignOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BmobUser.logOut();  //退出登录，并清除当前用户信息
                Intent intent = new Intent(SettingActivity.this,LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}
