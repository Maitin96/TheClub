package com.martin.myclub.activity.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.github.jdsjlzx.interfaces.OnItemClickListener;
import com.github.jdsjlzx.interfaces.OnLoadMoreListener;
import com.github.jdsjlzx.interfaces.OnRefreshListener;
import com.github.jdsjlzx.recyclerview.LRecyclerView;
import com.github.jdsjlzx.recyclerview.LRecyclerViewAdapter;
import com.github.jdsjlzx.recyclerview.ProgressStyle;
import com.martin.myclub.R;
import com.martin.myclub.activity.ClubDynamicDetailsActivity;
import com.martin.myclub.adapter.AdapterClubDynamic;
import com.martin.myclub.bean.ClubDynamic;

import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.pedant.SweetAlert.SweetAlertDialog;

/**
 * 社团成员发表的内部动态
 * Created by Edward on 2017/8/26.
 */

public class Fragment_club_dynamic extends Fragment {

    private static final int IS_REFRESH = 1 << 3;
    private static final int IS_LOAD_MORE = 1 << 2;
    /**
     * 每次请求服务器要加载的数据量
     */
    private static final int REQUEST_COUNT = 10;
    /**
     * 隐藏下拉刷新或者上划加载
     */
    private static final int FINISH_N_REF = 3;
    /**
     * 请求Adapter刷新UI
     */
    private static final int NOTIFY = 1;
    /**
     * 出现错误隐藏下拉刷新
     */
    private static final int FINISH_REFRESH = 2;
    private static final int SET_DATA_TO_ADAPTER = 1 << 4;
    private static final int SHOW_RETRY = 1 << 5;

    private View rootView;
    private LRecyclerView mLRecyclerView;
    private AdapterClubDynamic adapter;
    private LRecyclerViewAdapter recyclerViewAdapter;
    private List<ClubDynamic> mClubDynamicList;

    private PreviewHandler mHandler = new PreviewHandler();
    private LinearLayout loading;
    private LinearLayout reload;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.e("dynamic", "onCreateView: 被触发！" );
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
            reload = (LinearLayout) rootView.findViewById(R.id.reload);
            showLoadingView(true);
            showRetryLayout(false);
            mLRecyclerView = (LRecyclerView) rootView.findViewById(R.id.lRecyclerView);
            LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
            mLRecyclerView.setLayoutManager(layoutManager);
            mLRecyclerView.setRefreshProgressStyle(ProgressStyle.Pacman);
            mLRecyclerView.setArrowImageView(R.drawable.ic_pulltorefresh_arrow);
            adapter = new AdapterClubDynamic(getActivity());
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
            reload.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showLoadingView(true);
                    showreLoadView(false);
                    requestData();
                }
            });
            recyclerViewAdapter.setOnItemClickListener(new OnItemClickListener() {
                @Override
                public void onItemClick(View view, int position) {
                    if (mClubDynamicList != null) {
                        // 设置点击事件
                        String dynamicObjId = mClubDynamicList.get(position).getObjectId();
                        Intent intent = new Intent();
                        intent.setClass(getContext(), ClubDynamicDetailsActivity.class);
                        intent.putExtra("dynamicObjId", dynamicObjId);
                        startActivity(intent);
                    } else {
                        Toast.makeText(getContext(),"请刷新重试下吧",Toast.LENGTH_SHORT).show();
                    }
                }
            });
            requestData();
        }

    /**
     * 获取动态
     */
    private void requestData() {
        Thread thread = new Thread() {
            @Override
            public void run() {
                String clubObjId = getActivity().getIntent().getStringExtra("clubObjId");
                BmobQuery<ClubDynamic> query = new BmobQuery<>();
                query.addWhereEqualTo("clubId",clubObjId);
                query.include("user");
                query.findObjects(new FindListener<ClubDynamic>() {
                    @Override
                    public void done(List<ClubDynamic> list, BmobException e) {
                        if (e == null) {
                            if(list.size() != 0){
                                mClubDynamicList = list;
                                Message msg = Message.obtain();
                                msg.what = SET_DATA_TO_ADAPTER;
                                mHandler.sendMessage(msg);
                            }else{
                                //  社团当前没有人发布过动态
                                Log.d("Fragment_club_dynamic:", "requestData  done: 没有查询到该社团的动态数据");
                            }
                        } else {
                            Log.e("Fragment_club_dynamic:", "requestData: " + e.toString());
                            Message msg = Message.obtain();
                            msg.what = SHOW_RETRY;
                            mHandler.sendMessage(msg);

                        }
                        Message message = Message.obtain();
                        message.what = FINISH_REFRESH;
                        mHandler.sendMessage(message);
                    }
                });
            }
        };
        thread.start();
    }

    private class PreviewHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case SET_DATA_TO_ADAPTER:
                    setDataToAdapter(mClubDynamicList);
                    showLoadingView(false);
                    break;
                case SHOW_RETRY:
                    showLoadingView(false);
                    showreLoadView(true);
                    break;
                case FINISH_REFRESH:
                    finishRefresh();
                    break;
            }
        }
    }

    private void showRetryLayout(boolean show) {

    }

    /**
     * 设置社团动态数据
     *
     * @param mClubDynamicList
     */
    private void setDataToAdapter(List<ClubDynamic> mClubDynamicList) {
        adapter.setData(mClubDynamicList);
        recyclerViewAdapter.notifyDataSetChanged();
    }

    private void showLoadingView(boolean show) {
        if(show){
            loading.setVisibility(View.VISIBLE);
        }else{
            loading.setVisibility(View.GONE);
        }
    }


    private void showreLoadView(boolean show) {
        if(show){
            reload.setVisibility(View.VISIBLE);
        }else{
            reload.setVisibility(View.GONE);
        }
    }

    /**
     * 结束下拉刷新和上划加载
     */
    private void finishRefresh(){
        mLRecyclerView.refreshComplete(0);
    }

}
