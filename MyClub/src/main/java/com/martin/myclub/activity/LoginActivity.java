package com.martin.myclub.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.martin.myclub.R;
import com.martin.myclub.bean.MyUser;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;

/**
 * Created by Martin on 2017/7/2.
 * 登录页面
 */
public class LoginActivity extends AppCompatActivity{

    private EditText etUserName;
    private EditText etPassword;
    private Button btLogin;
    private TextView register;
    private CheckBox cbRemember;
    private SharedPreferences sp;
    private SharedPreferences.Editor editor;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initViews();
    }

    private void initViews(){

        etUserName = (EditText) findViewById(R.id.user_name);
        etPassword = (EditText) findViewById(R.id.password);
        btLogin = (Button) findViewById(R.id.loginBtn);
        register = (TextView) findViewById(R.id.register);
        cbRemember = (CheckBox) findViewById(R.id.cb_remember);

        sp = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        boolean isRemember = sp.getBoolean("remember_password",false);
        if (isRemember){
            //记住密码,将账号和密码设置到文本框中
            String spAccount = sp.getString("account","");
            String spPassword = sp.getString("password","");

            etUserName.setText(spAccount);
            etPassword.setText(spPassword);
            cbRemember.setChecked(true);
        } else{
            //不记住密码，只将账号设置到文本框中
            String spAccount = sp.getString("account","");
            etUserName.setText(spAccount);
            cbRemember.setChecked(false);
        }

        btLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final ProgressDialog dialog = new ProgressDialog(LoginActivity.this);
                dialog.setTitle("正在登录");
                dialog.setMessage("请稍候...");
                dialog.setCancelable(true);
                dialog.show();

                final String userName = etUserName.getText().toString();
                final String password = etPassword.getText().toString();

                BmobUser user = new BmobUser();
                user.setUsername(userName);
                user.setPassword(password);
                user.login(new SaveListener<MyUser>() {
                    @Override
                    public void done(MyUser myUser, BmobException e) {
                        if (e == null){
                            Log.e("Login","登录成功");

                            editor = sp.edit();
                            if (cbRemember.isChecked()){
                                editor.putBoolean("remember_password",true);
                                editor.putString("account",userName);
                                editor.putString("password",password);
                            } else {
                                editor.clear();
                                editor.putString("account",userName);
                            }
                            editor.apply();

                            BmobUser.getCurrentUser(MyUser.class);

                            Intent intent = new Intent(LoginActivity.this,MainActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            Toast.makeText(LoginActivity.this,"用户名或密码错误",Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                        }
                    }
                });
            }
        });

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this,RegisterActivity.class);
                startActivity(intent);
            }
        });

    }
}
