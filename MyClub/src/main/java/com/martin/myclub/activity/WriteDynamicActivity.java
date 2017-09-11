package com.martin.myclub.activity;


import android.os.Bundle;

import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;


import com.martin.myclub.R;
import com.martin.myclub.bean.DynamicMsg;
import com.martin.myclub.bean.MyUser;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;


/**
 * Created by Martin on 2017/7/18.
 * Write by Edward on 2017/7/20
 * 写动态
 */
public class WriteDynamicActivity extends AppCompatActivity implements View.OnClickListener{


    private ImageView iv_return;  //  返回
    private ImageView iv_send;   //发送动态
    private MyUser currentUser;//  当前登录的用户
    private EditText et_content;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write_dynamic);

        currentUser = BmobUser.getCurrentUser(MyUser.class);
        initViews();
        initListener();
    }

    private void initListener() {
        iv_return.setOnClickListener(this);
        iv_send.setOnClickListener(this);
    }

    private void initViews() {
        iv_return = (ImageView) findViewById(R.id.iv_return);
        iv_send = (ImageView) findViewById(R.id.iv_send);
        et_content = (EditText) findViewById(R.id.et_content);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.iv_return:
                finish();
                break;
            case R.id.iv_send:
                send();
                break;
        }
    }

    /**
     * 上传动态
     */
    private void send() {
        String content = et_content.getText().toString().trim();
        if(!TextUtils.isEmpty(content)){
            DynamicMsg msg = new DynamicMsg();
            msg.setUser(currentUser);
            msg.setContent(content);
            msg.save(new SaveListener<String>() {
                @Override
                public void done(String s, BmobException e) {
                    if(e == null){
                        Toast.makeText(getApplicationContext(),"发送成功",Toast.LENGTH_SHORT).show();
                        finish();
                    }else{
                        Toast.makeText(getApplicationContext(),s,Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }
}
