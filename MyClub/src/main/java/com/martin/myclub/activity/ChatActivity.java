package com.martin.myclub.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.martin.myclub.R;
import com.martin.myclub.adapter.AdapterChatMsg;
import com.martin.myclub.bean.ChatMsg;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Martin on 2017/7/17.
 * 聊天页面
 */
public class ChatActivity extends AppCompatActivity{

    private TextView titleName;
    private ListView listView;
    private EditText myMsg;
    private Button btnSend;
    private List<ChatMsg> chatMsgList;
    private AdapterChatMsg adapterChatMsgList;
    private String chatObj;
    private Button back;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initViews();
    }

    private void initViews(){
        setContentView(R.layout.activity_chat);

        titleName = (TextView) findViewById(R.id.title_name);
        listView = (ListView) findViewById(R.id.lv_chat_room);
        myMsg = (EditText) findViewById(R.id.myMsg);
        btnSend = (Button) findViewById(R.id.btnSend);
        back = (Button) findViewById(R.id.back);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        chatMsgList = new ArrayList<>();

        chatObj = getIntent().getStringExtra("username");
        titleName.setText(chatObj);

        adapterChatMsgList = new AdapterChatMsg(ChatActivity.this, R.layout.chat_other, chatMsgList);

        listView.setAdapter(adapterChatMsgList);

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String content = myMsg.getText().toString();
                if (!content.isEmpty()) {
                    ChatMsg msg = new ChatMsg();
                    msg.setContent(content);
                    msg.setUsername("hello");
                    msg.setIconID(R.drawable.avasterwe);
                    msg.setMyInfo(true);
                    msg.setChatObj(chatObj);
                    chatMsgList.add(msg);
                    myMsg.setText("");
                }
            }
        });

    }
}
