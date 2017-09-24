package com.martin.myclub.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.martin.myclub.R;
import com.martin.myclub.adapter.AdapterClubManager;
import com.martin.myclub.bean.ClubApply;
import com.martin.myclub.bean.MyUser;
import com.martin.myclub.view.ClubApplyInterface;

import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobPointer;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.pedant.SweetAlert.SweetAlertDialog;

/**
 * 社团管理页面
 * Powered by Edward 8/15
 */
public class ClubManagerActivity extends AppCompatActivity{

    private RecyclerView mRecyclerView;
    private LinearLayout ll_loading;
    private Button btn_apply_club;
    private ImageView iv_return;
    private TextView tv_joined_number;
    private AdapterClubManager adapter;
    private MyUser currentUser;
    List<ClubApply> mClubList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_club_manage);
        currentUser = BmobUser.getCurrentUser(MyUser.class);
        initView();
    }

    private void initView() {
        ll_loading = (LinearLayout) findViewById(R.id.ll_loading);
        btn_apply_club = (Button) findViewById(R.id.btn_apply_club);
        iv_return = (ImageView) findViewById(R.id.iv_return);
        tv_joined_number = (TextView) findViewById(R.id.tv_joined_number);
        mRecyclerView = (RecyclerView) findViewById(R.id.RecyclerView);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new AdapterClubManager(this);
        mRecyclerView.setAdapter(adapter);

        adapter.setOnItemClickListener(new AdapterClubManager.OnItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                Intent intent = new Intent(ClubManagerActivity.this, ClubActivity.class);
                intent.putExtra("clubObjId",mClubList.get(position).getObjectId());
                startActivity(intent);
            }
        });

        btn_apply_club.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(getApplicationContext(),ClubApplyActivity.class);
                startActivity(intent);
            }
        });

        iv_return.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        requestData();
    }

    private void requestData() {
        showLoading();

        BmobQuery<ClubApply> query = new BmobQuery<>();
        MyUser user = new MyUser();
        user.setObjectId(currentUser.getObjectId());
        query.addWhereRelatedTo("club",new BmobPointer(user));
        query.findObjects(new FindListener<ClubApply>() {
            @Override
            public void done(List<ClubApply> list, BmobException e) {
                if(e == null){
                    mClubList = list;
                    adapter.setData(mClubList);
                    tv_joined_number.setText("已加入的社团(" + mClubList.size() + ")");
                    hideLoading();
                    notifyDataSetChanged();
                }else{
                    Log.e("ClubManagerActivity:", "error:" + e.toString() );
                    showRetryDialog();
                }
            }
        });
    }

    /**
     * 加载我加入的社团
     */
    private void notifyDataSetChanged() {
        adapter.notifyDataSetChanged();
    }

    private void showRetryDialog() {
        new SweetAlertDialog(this,SweetAlertDialog.ERROR_TYPE)
                .setTitleText("异常")
                .setContentText("加载出了点小问题,要重试一次吗？")
                .setConfirmText("再试试")
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        requestData();
                    }
                }).show();
    }

    private void showLoading(){
        ll_loading.setVisibility(View.VISIBLE);
    }

    private void hideLoading(){
        ll_loading.setVisibility(View.INVISIBLE);
    }
}
