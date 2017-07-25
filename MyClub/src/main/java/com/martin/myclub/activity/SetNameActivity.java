package com.martin.myclub.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.martin.myclub.R;

/**
 * Created by Martin on 2017/7/21.
 * 设置昵称修改页面
 */
public class SetNameActivity extends AppCompatActivity implements View.OnClickListener {

    private ImageView ivBack;
    private EditText etName;
    private Button btSave;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_name);
        initView();
        initListener();
    }

    private void initView(){
        ivBack = (ImageView) findViewById(R.id.iv_back);
        etName = (EditText) findViewById(R.id.et_name);
        btSave = (Button) findViewById(R.id.bt_save);
    }

    private void initListener(){
        ivBack.setOnClickListener(this);
        btSave.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.iv_back:
                finish();
                break;

            case R.id.bt_save:
                String name = etName.getText().toString().trim();
                if (!TextUtils.isEmpty(name)){
                    Intent intent = new Intent(this,PersonInfoActivity.class);
                    intent.putExtra("name",name);
                    setResult(RESULT_OK,intent);        //将EditText获取到的字符串传递到PersonInfo页面中
                }
                finish();
                break;
        }
    }
}
