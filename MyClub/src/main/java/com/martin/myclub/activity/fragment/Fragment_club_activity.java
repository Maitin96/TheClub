package com.martin.myclub.activity.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.github.jdsjlzx.interfaces.OnItemClickListener;
import com.github.jdsjlzx.interfaces.OnRefreshListener;
import com.github.jdsjlzx.recyclerview.LRecyclerView;
import com.github.jdsjlzx.recyclerview.LRecyclerViewAdapter;
import com.github.jdsjlzx.recyclerview.ProgressStyle;
import com.martin.myclub.R;
import com.martin.myclub.activity.ClubActiveActivity;
import com.martin.myclub.adapter.AdapterClubActivity;
import com.martin.myclub.adapter.AdapterClubDynamic;
import com.martin.myclub.bean.ClubSendActivity;

import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

/**
 * 社团举办的活动
 * Created by Edward on 2017/8/26.
 */

public class Fragment_club_activity extends Fragment {

    private String TAG = "Fragment_club_activity";

    private View rootView;
    private LRecyclerView mLRecyclerView;
    private AdapterClubActivity adapter;
    private LRecyclerViewAdapter recyclerViewAdapter;
    private LinearLayout loading;
    private List<ClubSendActivity> activityList;
    private String clubObjId;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        clubObjId = getActivity().getIntent().getStringExtra("clubObjId");
        if (rootView != null) {
            ViewGroup parent = (ViewGroup) rootView.getParent();
            if (parent != null) {
                parent.removeView(rootView);
            }
            initView();
            return rootView;
        }
        rootView = inflater.inflate(R.layout.fragment_club_dynamic,
                container, false);
        initView();
        return rootView;

    }

    private void initView() {
        loading = (LinearLayout) rootView.findViewById(R.id.loading);
        showLoadingView(true);

        mLRecyclerView = (LRecyclerView) rootView.findViewById(R.id.lRecyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        mLRecyclerView.setLayoutManager(layoutManager);
        mLRecyclerView.setRefreshProgressStyle(ProgressStyle.Pacman);
        mLRecyclerView.setArrowImageView(R.drawable.ic_pulltorefresh_arrow);
        adapter = new AdapterClubActivity(getActivity());
        recyclerViewAdapter = new LRecyclerViewAdapter(adapter);
        mLRecyclerView.setAdapter(recyclerViewAdapter);
        mLRecyclerView.setLoadingMoreProgressStyle(ProgressStyle.BallSpinFadeLoader);
        mLRecyclerView.setLoadMoreEnabled(false);
        mLRecyclerView.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh() {
                requestData();
            }
        });
        recyclerViewAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                if(activityList != null){
                    Intent intent = new Intent();
                    intent.setClass(getContext(), ClubActiveActivity.class);
                    intent.putExtra("activityId",activityList.get(position).getObjectId());
                    startActivity(intent);
                }
            }
        });

        requestData();

    }

    private void showLoadingView(boolean show) {
        if(show){
            loading.setVisibility(View.VISIBLE);
        }else{
            loading.setVisibility(View.GONE);
        }
    }

    private void requestData() {
        BmobQuery<ClubSendActivity> query = new BmobQuery<>();
        query.include("user");
        query.addWhereEqualTo("clubId",clubObjId);
        query.findObjects(new FindListener<ClubSendActivity>() {
            @Override
            public void done(List<ClubSendActivity> list, BmobException e) {
                if(e == null){
                    showLoadingView(false);
                    activityList = list;
                    setMainView(list);
                    finishRefresh();
                }else{
                    requestData();
                    Log.e(TAG, "requestData: " + e.toString() );
                }
            }
        });
    }

    /**
     * 结束下拉刷新和上划加载
     */
    private void finishRefresh(){
        mLRecyclerView.refreshComplete(1);
    }

    /**
     * 设置主界面
     * @param list
     */
    private void setMainView(List<ClubSendActivity> list) {
        adapter.setData(list);
        recyclerViewAdapter.notifyDataSetChanged();
    }
}
