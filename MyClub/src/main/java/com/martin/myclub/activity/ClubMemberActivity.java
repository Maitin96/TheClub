package com.martin.myclub.activity;

import android.app.AlertDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.github.jdsjlzx.interfaces.OnItemClickListener;
import com.github.jdsjlzx.interfaces.OnRefreshListener;
import com.github.jdsjlzx.recyclerview.LRecyclerView;
import com.github.jdsjlzx.recyclerview.LRecyclerViewAdapter;
import com.github.jdsjlzx.recyclerview.ProgressStyle;
import com.martin.myclub.R;
import com.martin.myclub.adapter.AdapterMember;
import com.martin.myclub.adapter.AdapterMemberApply;
import com.martin.myclub.bean.ApplyToAddClub;
import com.martin.myclub.bean.ClubApply;
import com.martin.myclub.bean.MyUser;
import com.martin.myclub.util.Global;
import com.martin.myclub.view.CircleImageView;

import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobPointer;
import cn.bmob.v3.datatype.BmobRelation;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.UpdateListener;

/**
 * 社团成员
 * Created by Edward on 2017/10/17.
 */

public class ClubMemberActivity extends AppCompatActivity implements View.OnClickListener{

    private MyUser currentUser;
    private String clubObjId;
    private ImageView iv_return;
    private LinearLayout ll_loading;

    private AdapterMember adapter;
    private LRecyclerViewAdapter recyclerViewAdapter;

    private List<MyUser> memberList;
    private TextView title;
    private LRecyclerView mLRecyclerView;
    private TextView error;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        currentUser = BmobUser.getCurrentUser(MyUser.class);
        clubObjId = getIntent().getStringExtra("clubObjId");
        setContentView(R.layout.activity_member_apply);
        initView();
    }

    private void initView() {
        error = (TextView) findViewById(R.id.error);

        ll_loading = (LinearLayout) findViewById(R.id.loading);
        showLoadingView(true);

        iv_return = (ImageView) findViewById(R.id.iv_return);
        ImageView iv_send = (ImageView) findViewById(R.id.iv_send);
        iv_send.setVisibility(View.GONE);

        title = (TextView) findViewById(R.id.title);
        setTitle("社团成员");
        mLRecyclerView = (LRecyclerView) findViewById(R.id.lRecyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mLRecyclerView.setLayoutManager(layoutManager);
        mLRecyclerView.setRefreshProgressStyle(ProgressStyle.Pacman);
        mLRecyclerView.setArrowImageView(R.drawable.ic_pulltorefresh_arrow);
        adapter = new AdapterMember(this);
        recyclerViewAdapter = new LRecyclerViewAdapter(adapter);
        mLRecyclerView.setAdapter(recyclerViewAdapter);
        mLRecyclerView.setLoadingMoreProgressStyle(ProgressStyle.BallSpinFadeLoader);
        mLRecyclerView.setLoadMoreEnabled(false);

        recyclerViewAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                // Todo 进入聊天页面
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

    private void showAgreeDialog(final int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final AlertDialog dialog = builder.create();
        View view = View.inflate(this, R.layout.dialog_apply_detail, null);

        dialog.setView(view);
        dialog.show();
    }

    private void requestData() {
        BmobQuery<MyUser> query = new BmobQuery<>();
        ClubApply clubApply = new ClubApply();
        clubApply.setObjectId(clubObjId);
        query.addWhereRelatedTo("member",new BmobPointer(clubApply));
        query.findObjects(new FindListener<MyUser>() {
            @Override
            public void done(List<MyUser> list, BmobException e) {
                if(e == null){
                    Log.d("社团成员数：", "done: " + list.size());
                    showLoadingView(false);
                    memberList = list;
                    setDataToAdapter();
                    if(list.size() == 0 ){
                        showError("暂无成员需要审核哦");
                    }
                }
                finishRefresh();
            }
        });
    }

    /**
     * ，没有
     * @param content
     */
    private void showError(String content) {
        error.setText(content);
        error.setVisibility(View.VISIBLE);
    }
    private void setTitle(String title){
        this.title.setText(title);
    }

    private void setDataToAdapter() {
        if(memberList != null){
            adapter.setData(memberList);
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
