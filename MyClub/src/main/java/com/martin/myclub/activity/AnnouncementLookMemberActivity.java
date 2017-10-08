package com.martin.myclub.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.jdsjlzx.recyclerview.LRecyclerView;
import com.github.jdsjlzx.recyclerview.LRecyclerViewAdapter;
import com.github.jdsjlzx.recyclerview.ProgressStyle;
import com.martin.myclub.R;
import com.martin.myclub.adapter.AdapterClubActivity;
import com.martin.myclub.adapter.AdapterClubMember;
import com.martin.myclub.bean.MyUser;
import com.martin.myclub.view.CircleImageView;

import java.io.Serializable;
import java.util.List;

/**
 * 查看已读公告成员
 * Created by 1033834071 on 2017/10/8.
 */

public class AnnouncementLookMemberActivity extends AppCompatActivity implements View.OnClickListener{

    private List<MyUser> memberList;
    private LRecyclerView mLRecyclerView;
    private LRecyclerViewAdapter recyclerViewAdapter;
    private AdapterClubMember adapter;
    private TextView error;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        memberList = (List<MyUser>)getIntent().getSerializableExtra("memberList");
        setContentView(R.layout.activity_alm);
        initView();
    }

    private void initView() {
        TextView title = (TextView) findViewById(R.id.title);
        title.setText("已读成员");

        error = (TextView) findViewById(R.id.error);

        ImageView iv_return = (ImageView) findViewById(R.id.iv_return);
        ImageView iv_send = (ImageView) findViewById(R.id.iv_send);
        iv_send.setClickable(false);
        iv_send.setVisibility(View.GONE);

        iv_return.setOnClickListener(this);

        mLRecyclerView = (LRecyclerView)findViewById(R.id.lRecyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mLRecyclerView.setLayoutManager(layoutManager);
        adapter = new AdapterClubMember(this);
        recyclerViewAdapter = new LRecyclerViewAdapter(adapter);
        mLRecyclerView.setAdapter(recyclerViewAdapter);
        mLRecyclerView.setPullRefreshEnabled(false);
        setData();
    }

    private void setData() {
        if(memberList != null && memberList.size() > 0){
            adapter.setData(memberList);
            adapter.notifyDataSetChanged();
        }else{
            showError();
        }
    }

    private void showError() {
        error.setText("暂无成员");
        error.setVisibility(View.VISIBLE);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.iv_return:
                finish();
                break;
        }
    }
}
