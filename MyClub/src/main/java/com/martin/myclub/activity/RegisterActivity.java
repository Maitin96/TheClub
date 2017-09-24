package com.martin.myclub.activity;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.martin.myclub.R;
import com.martin.myclub.bean.MyUser;
import com.martin.myclub.db.PersonInfoOpenHelper;

import cn.bmob.sms.BmobSMS;
import cn.bmob.sms.bean.BmobSmsState;
import cn.bmob.sms.exception.BmobException;
import cn.bmob.sms.listener.QuerySMSStateListener;
import cn.bmob.sms.listener.RequestSMSCodeListener;
import cn.bmob.sms.listener.VerifySMSCodeListener;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.listener.SaveListener;

/**
 * Created by Martin on 2017/7/2.
 */
public class RegisterActivity extends AppCompatActivity {

    private Button btnRegister;
    private EditText phoneNumber;
    private EditText yanzheng;
    private Button getYanzheng;
    private EditText pass;
    private int SMS_ID;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initViews();
    }

    private void initViews(){
        setContentView(R.layout.activity_register);

        btnRegister = (Button) findViewById(R.id.btn_register);
        phoneNumber = (EditText) findViewById(R.id.phone_number);
        yanzheng = (EditText) findViewById(R.id.yanzheng);
        getYanzheng = (Button) findViewById(R.id.get_yanzheng);
        pass = (EditText) findViewById(R.id.pass);
        //调用倒计时方法
        final MyCountDownTimer myCountDownTimer = new MyCountDownTimer(60000,1000);

        //获取验证码按钮
        getYanzheng.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty(phoneNumber.getText().toString())){
                    myCountDownTimer.start();
                    sendSMS();      //发送短信
                    queryState();       //获取短信发送状态
                } else {
                    Toast.makeText(RegisterActivity.this,"请输入电话号码",Toast.LENGTH_SHORT).show();
                }
            }
        });

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(pass.getText().toString())){
                    Toast.makeText(RegisterActivity.this,"请输入密码",Toast.LENGTH_SHORT).show();
                } else if (!TextUtils.isEmpty(yanzheng.getText().toString()) &&
                        !TextUtils.isEmpty(pass.getText().toString())){
                    HandlerRegister();
                } else {
                    Toast.makeText(RegisterActivity.this,"验证码和密码不能为空",Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void sendSMS(){

        BmobSMS.requestSMSCode(this, phoneNumber.getText().toString(), "test1", new RequestSMSCodeListener() {
            @Override
            public void done(Integer smsId, BmobException e) {
                if (e == null){
                    Log.e("bomb","短信发送成功" + smsId );
                } else {
                    Log.i("bmob","errorCode = "+e.getErrorCode()+",errorMsg = "+e.getLocalizedMessage());
                }
                SMS_ID = smsId;
            }
        });
    }

    private void HandlerRegister(){
        BmobSMS.verifySmsCode(this, phoneNumber.getText().toString(),
                yanzheng.getText().toString(), new VerifySMSCodeListener() {
                    @Override
                    public void done(BmobException e) {
                        if (e == null){
                            Log.e("RegisterActivity","验证通过");

                            //保存账号密码
                            SaveAccount();
                        } else {
                            Log.i("bmob", "验证失败：code ="+e.getErrorCode()+",msg = "+e.getLocalizedMessage());
                        }
                    }
                });
    }
    //保存账户
    private void SaveAccount(){
        //保存用户信息到Bomb服务器
        MyUser user = new MyUser();
        user.setName("小团");
        user.setMan(true);
//        user.setAvatar(null);
        user.setSign("这个人很神秘，什么也有留下哦~");
        user.setUsername(phoneNumber.getText().toString());
        user.setPassword(pass.getText().toString());
        user.signUp(new SaveListener<MyUser>() {
            @Override
            public void done(MyUser myUser, cn.bmob.v3.exception.BmobException e) {
                if (e == null){
                    Log.e("Register","注册成功");

                    //保存用户信息到本地数据库
                    String objectId = myUser.getObjectId();
                    PersonInfoOpenHelper openHelper = new PersonInfoOpenHelper(RegisterActivity.this);
                    SQLiteDatabase db = openHelper.getWritableDatabase();
                    ContentValues values = new ContentValues();
                    values.put("objectId",objectId);
                    values.put("name","小团");
                    values.put("isMan",true);
                    values.put("avatar","");
                    values.put("sign","这个人很神秘，什么也没有留下~");
                    db.insert("personInfo",null,values);
                    db.close();
                    Log.e("RegisterActivity","数据库保存成功");

                    //跳转到主页面
                    Intent intent = new Intent(RegisterActivity.this,MainActivity.class);
                    startActivity(intent);
                } else {
                    Log.e("Register","注册失败" + e);
                }
            }
        });
    }

    //查询发送状态
    private void queryState(){
        BmobSMS.querySmsState(this, SMS_ID, new QuerySMSStateListener() {
            @Override
            public void done(BmobSmsState bmobSmsState, BmobException e) {
                if(e==null){
                    Log.i("bmob","短信状态："+bmobSmsState.getSmsState()+",验证状态："+bmobSmsState.getVerifyState());
                }
            }
        });
    }

    /**
     * 复写倒计时类
     */

    private class MyCountDownTimer extends CountDownTimer {

        public MyCountDownTimer(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        //计时过程
        @Override
        public void onTick(long l) {
            //防止计时过程中重复点击
            getYanzheng.setClickable(false);
            getYanzheng.setText(l/1000+"s 后重试");

        }

        //计时完毕的方法
        @Override
        public void onFinish() {
            //重新给Button设置文字
            getYanzheng.setText("重新获取验证码");
            //设置可点击
            getYanzheng.setClickable(true);
        }
    }
}
