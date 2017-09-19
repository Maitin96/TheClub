package com.martin.myclub.activity.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.jdsjlzx.interfaces.OnItemClickListener;
import com.github.jdsjlzx.interfaces.OnLoadMoreListener;
import com.github.jdsjlzx.recyclerview.LRecyclerView;
import com.github.jdsjlzx.recyclerview.LRecyclerViewAdapter;
import com.github.jdsjlzx.recyclerview.ProgressStyle;
import com.martin.myclub.R;
import com.martin.myclub.adapter.AdapterClubDynamic;
import com.martin.myclub.bean.ClubDynamic;

import cn.bmob.v3.BmobQuery;

/**
 * 社团成员发表的内部动态
 * Created by Edward on 2017/8/26.
 */

public class Fragment_club_dynamic extends Fragment {

    private static final int IS_REFRESH = 1 << 3;
    private static final int IS_LOAD_MORE = 1 << 2;
    /**每次请求服务器要加载的数据量*/
    private static final int REQUEST_COUNT = 10;
    /**隐藏下拉刷新或者上划加载*/
    private static final int FINISH_N_REF = 3;
    /** 请求Adapter刷新UI*/
    private static final int NOTIFY = 1;
    /**出现错误隐藏下拉刷新*/
    private static final int FINISH_REFRESH = 2;

    private View rootView;
    private LRecyclerView mRecyclerView;
    private AdapterClubDynamic adapter;
    private LRecyclerViewAdapter recyclerViewAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_club_dynamic, container, false);
        initView();
        return rootView;
    }

    private void initView() {
        mRecyclerView = (LRecyclerView) rootView.findViewById(R.id.RecyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setRefreshProgressStyle(ProgressStyle.Pacman);
        mRecyclerView.setArrowImageView(R.drawable.ic_pulltorefresh_arrow);
        mRecyclerView.setLoadMoreEnabled(true);
        adapter = new AdapterClubDynamic(getContext());
        recyclerViewAdapter = new LRecyclerViewAdapter(adapter);
        mRecyclerView.setAdapter(recyclerViewAdapter);
        mRecyclerView.setLoadingMoreProgressStyle(ProgressStyle.BallSpinFadeLoader);
        mRecyclerView.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                requestData();
            }
        });
        mRecyclerView.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore() {

            }
        });

        recyclerViewAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {

            }
        });

        requestData();
    }

    /**
     * 获取动态
     */
    private void requestData() {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                BmobQuery<ClubDynamic> query = new BmobQuery<>();
                //Todo 显示社团动态
            }
        });
        thread.start();

//        query
    }
}
