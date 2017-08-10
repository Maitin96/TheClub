package com.martin.myclub.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.martin.myclub.R;
import com.martin.myclub.adapter.AdapterDynamicItem;
import com.martin.myclub.bean.DynamicMsg;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static cn.bmob.newim.core.BmobIMClient.getContext;

/**
 * Created by Martin on 2017/7/13.
 * 我的帖子  页面
 */
public class MyPostActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private List<DynamicMsg> dynamicMsgList;
    private AdapterDynamicItem adapterDynamicItem;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initViews();
    }

    private void initViews() {
        setContentView(R.layout.activity_my_post);
        recyclerView = (RecyclerView) findViewById(R.id.rv_my_post);
    }
}
