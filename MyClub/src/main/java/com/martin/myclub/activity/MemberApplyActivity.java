package com.martin.myclub.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.github.jdsjlzx.interfaces.OnItemClickListener;
import com.github.jdsjlzx.interfaces.OnRefreshListener;
import com.github.jdsjlzx.recyclerview.LRecyclerView;
import com.github.jdsjlzx.recyclerview.LRecyclerViewAdapter;
import com.github.jdsjlzx.recyclerview.ProgressStyle;
import com.martin.myclub.R;
import com.martin.myclub.adapter.AdapterClubDynamic;
import com.martin.myclub.adapter.AdapterMemberApply;
import com.martin.myclub.bean.ApplyToAddClub;
import com.martin.myclub.bean.MyUser;

import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

/**
 * 成员申请审核
 * Created by Edward on 2017/9/27.
 */

public class MemberApplyActivity extends AppCompatActivity implements View.OnClickListener{

    private MyUser currentUser;
    private String clubObjId;
    private ImageView iv_return;
    private LinearLayout ll_loading;

    private AdapterMemberApply adapter;
    private LRecyclerViewAdapter recyclerViewAdapter;

    private List<ApplyToAddClub> applyList;
    private TextView title;
    private LRecyclerView mLRecyclerView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        currentUser = BmobUser.getCurrentUser(MyUser.class);
        clubObjId = getIntent().getStringExtra("clubObjId");
        setContentView(R.layout.activity_member_apply);
        initView();
    }

    private void initView() {
        iv_return = (ImageView) findViewById(R.id.iv_return);
        ImageView iv_send = (ImageView) findViewById(R.id.iv_send);
        iv_send.setVisibility(View.GONE);

        title = (TextView) findViewById(R.id.title);
        setTitle("成员审核");

        ll_loading = (LinearLayout) findViewById(R.id.loading);

        mLRecyclerView = (LRecyclerView) findViewById(R.id.lRecyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mLRecyclerView.setLayoutManager(layoutManager);
        mLRecyclerView.setRefreshProgressStyle(ProgressStyle.Pacman);
        mLRecyclerView.setArrowImageView(R.drawable.ic_pulltorefresh_arrow);
        adapter = new AdapterMemberApply(this);
        recyclerViewAdapter = new LRecyclerViewAdapter(adapter);
        mLRecyclerView.setAdapter(recyclerViewAdapter);
        mLRecyclerView.setLoadingMoreProgressStyle(ProgressStyle.BallSpinFadeLoader);
        mLRecyclerView.setLoadMoreEnabled(false);

        recyclerViewAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                // Todo 点击是否同意
            }
        });

        mLRecyclerView.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh() {
                requestData();
            }
        });

        requestData();
    }

    private void requestData() {
        showLoadingView(true);

        BmobQuery<ApplyToAddClub> query = new BmobQuery<>();
        query.addWhereEqualTo("clubId",clubObjId);
        query.include("user");
        query.findObjects(new FindListener<ApplyToAddClub>() {
            @Override
            public void done(List<ApplyToAddClub> list, BmobException e) {
                if(e == null){
                    showLoadingView(false);
                    applyList = list;
                    setDataToAdapter();
                }
                finishRefresh();
            }
        });
    }
    private void setTitle(String title){
        this.title.setText(title);
    }

    private void setDataToAdapter() {
        if(applyList != null){
            adapter.setData(applyList);
            recyclerViewAdapter.notifyDataSetChanged();
        }
    }

    private void showLoadingView(boolean show) {
        if (show) {
            ll_loading.setVisibility(View.VISIBLE);
        } else {
            ll_loading.setVisibility(View.INVISIBLE);
        }
    }

    private void finishRefresh(){
        if(mLRecyclerView != null){
            mLRecyclerView.refreshComplete(0);
        }
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
