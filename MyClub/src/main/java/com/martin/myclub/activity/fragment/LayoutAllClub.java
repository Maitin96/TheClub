package com.martin.myclub.activity.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.astuetz.PagerSlidingTabStrip;
import com.github.jdsjlzx.interfaces.OnItemClickListener;
import com.github.jdsjlzx.interfaces.OnRefreshListener;
import com.github.jdsjlzx.recyclerview.LRecyclerView;
import com.github.jdsjlzx.recyclerview.LRecyclerViewAdapter;
import com.github.jdsjlzx.recyclerview.ProgressStyle;
import com.martin.myclub.R;
import com.martin.myclub.activity.ClubActivity;
import com.martin.myclub.adapter.AdapterClubList;
import com.martin.myclub.adapter.AdapterMainViewPager;
import com.martin.myclub.bean.ClubApply;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

/**
 * Created by Edward on 2017/7/10.
 * 社团列表，所有社团
 */
public class LayoutAllClub extends Fragment {
    private String TAG = "LayoutAllClub";

    private static final int SHOW_CLUB = 1 << 1;
    private int REQUEST_COUNT = 0;
    private View rootView;
    private LRecyclerView mLRecyclerView;
    private List<ClubApply> mClubLists;

    private PreviewHandler mHandler = new PreviewHandler();
    private AdapterClubList adapter;
    private LRecyclerViewAdapter lRecyclerViewAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.layout_all_club,container,false);
        initViews();
        return rootView;
    }

    private void initViews(){
        mLRecyclerView = (LRecyclerView) rootView.findViewById(R.id.LR_club_view);
        GridLayoutManager layoutManager = new GridLayoutManager(getActivity(),2);
        mLRecyclerView.setLayoutManager(layoutManager);
        mLRecyclerView.setRefreshProgressStyle(ProgressStyle.Pacman);
        mLRecyclerView.setArrowImageView(R.drawable.ic_pulltorefresh_arrow);
        adapter = new AdapterClubList(getContext());
        lRecyclerViewAdapter = new LRecyclerViewAdapter(adapter);
        mLRecyclerView.setAdapter(lRecyclerViewAdapter);
        mLRecyclerView.setLoadMoreEnabled(false);
        mLRecyclerView.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh() {
                requestData();
            }
        });
        lRecyclerViewAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                if(mClubLists != null){
                    Intent intent = new Intent(getContext(), ClubActivity.class);
                    intent.putExtra("clubObjId",mClubLists.get(position).getObjectId());
                    startActivity(intent);
                }else{
                    Log.e(TAG, "mClubLists: 为空！");
                }

            }
        });

        requestData();
    }

    /**
     * 查询全部
     */
    private void requestData() {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                BmobQuery<ClubApply> query = new BmobQuery<>();
                query.findObjects(new FindListener<ClubApply>() {
                    @Override
                    public void done(List<ClubApply> list, BmobException e) {
                        if(e == null){
                            mClubLists = list;
                            Message msg = Message.obtain();
                            msg.what = SHOW_CLUB;
                            mHandler.sendMessage(msg);
                        }else{
                            finishRefresh();
                            Toast.makeText(getContext(),"社团列表获取失败",Toast.LENGTH_SHORT).show();
                            Log.e("LayoutAllClub:", "requestData " + e.toString() );
                        }
                    }
                });
            }
        });
        thread.start();
    }

    private class PreviewHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case SHOW_CLUB:
                    show();
                    break;
            }
        }
    }

    /**
     * 展示社团列表
     */
    private void show() {
        if(mClubLists != null){
            adapter.setData(mClubLists);
            notifyDataSetChanged();
        }
    }

    /**
     * 刷新UI
     */
    private void notifyDataSetChanged(){
        adapter.notifyDataSetChanged();
        finishRefresh();
    }

    /**
     * 结束下拉刷新和上划加载
     */
    private void finishRefresh(){
        mLRecyclerView.refreshComplete(REQUEST_COUNT);
    }
}
