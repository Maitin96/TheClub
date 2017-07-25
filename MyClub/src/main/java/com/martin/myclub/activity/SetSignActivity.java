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
 * 设置更改签名页面
 */
public class SetSignActivity extends AppCompatActivity {

    private ImageView ivBack;
    private EditText etSign;
    private Button btSave;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_sign);
        initViews();
    }

    private void initViews() {
        ivBack = (ImageView) findViewById(R.id.iv_sign_back);
        etSign = (EditText) findViewById(R.id.et_sign);
        btSave = (Button) findViewById(R.id.bt_save_sign);

        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        btSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String sign = etSign.getText().toString().trim();
                if (!TextUtils.isEmpty(sign)){
                    Intent intent = new Intent(SetSignActivity.this,PersonInfoActivity.class);
                    intent.putExtra("sign",sign);
                    setResult(RESULT_OK,intent);
                }
                finish();
            }
        });
    }


}
