package com.martin.myclub.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.martin.myclub.R;
import com.martin.myclub.bean.Feedback;

import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;

import static com.martin.myclub.R.id.suggest;

/**
 * Created by Martin on 2017/7/13.
 * 问题反馈页面
 */
public class SuggestActivity extends AppCompatActivity {

    private EditText suggestContent;
    private ImageView ivSuggest;
    private EditText etContact;
    private Button btSubmit;
    private ImageView suggestBack;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        submitData();
    }

    private void initView(){
        setContentView(R.layout.activity_suggest);

        suggestContent = (EditText) findViewById(R.id.suggest_content);
        ivSuggest = (ImageView) findViewById(R.id.iv_suggest);
        etContact = (EditText) findViewById(R.id.et_contact);
        btSubmit = (Button) findViewById(R.id.bt_submit);

        suggestBack = (ImageView) findViewById(R.id.suggest_back);
        suggestBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void submitData(){
        btSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String content = suggestContent.getText().toString().trim();
                String contact = etContact.getText().toString().trim();

                Feedback feedback = new Feedback();
                feedback.setContent(content);
                feedback.setContact(contact);

                feedback.save(new SaveListener<String>() {
                    @Override
                    public void done(String s, BmobException e) {
                        if (e == null){
                            Log.d("SuggestActivity","反馈信息已发送到服务器");
                        } else {
                            Log.e("SuggestActivity","反馈信息失败" + e);
                        }
                    }
                });
                Toast.makeText(SuggestActivity.this,"提交成功，感谢您的反馈或建议",Toast.LENGTH_LONG).show();
                finish();
            }
        });


    }

}
