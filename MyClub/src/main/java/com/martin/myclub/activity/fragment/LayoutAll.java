package com.martin.myclub.activity.fragment;

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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.jdsjlzx.interfaces.OnItemClickListener;
import com.github.jdsjlzx.interfaces.OnLoadMoreListener;
import com.github.jdsjlzx.interfaces.OnNetWorkErrorListener;
import com.github.jdsjlzx.interfaces.OnRefreshListener;
import com.github.jdsjlzx.recyclerview.LRecyclerView;
import com.github.jdsjlzx.recyclerview.LRecyclerViewAdapter;
import com.github.jdsjlzx.recyclerview.ProgressStyle;
import com.martin.myclub.R;
import com.martin.myclub.activity.Dy_CompleteActivity;
import com.martin.myclub.adapter.AdapterDynamicItem;
import com.martin.myclub.bean.DynamicMsg;
import com.martin.myclub.bean.MyUser;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobPointer;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.CountListener;
import cn.bmob.v3.listener.FindListener;


/**
 * Created by Martin on 2017/7/10.
 * Powered by Edward
 *
 * 动态页面
 */
public class LayoutAll extends Fragment {

    private String TAG = "LayoutAll";

    private static final int IS_REFRESH = 1 << 3;
    private static final int IS_LOAD_MORE = 1 << 2;
    /**服务器端一共多少条数据  默认100*/
    private static int TOTAL_COUNTER = 100;
    /**每次请求服务器要加载的数据量*/
    private static final int REQUEST_COUNT = 10;
    /**隐藏下拉刷新或者上划加载*/
    private static final int FINISH_N_REF = 3;

    /**  当前得到的数据量*/
    private static int mCurrentCounter = 0;
    /** 请求Adapter刷新UI*/
    private static final int NOTIFY = 1;
    /**出现错误隐藏下拉刷新*/
    private static final int FINISH_REFRESH = 2;

    private int flag;
    boolean isFirstRefresh = true;
    boolean isFirstLoadMore = true;

    private PreviewHandler mHandler = new PreviewHandler();

    private View rootView;

    private List<DynamicMsg> dynamicMsgList = new ArrayList<>();
    private LRecyclerView dynamicRecyclerView;
    private AdapterDynamicItem adapterDynamicItem;
    private LRecyclerViewAdapter lRecyclerViewAdapter;
    private LinearLayout loading;
    private TextView error;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.layout_all,container,false);
        initViews();
        return rootView;
    }

    private void initViews(){
        loading = (LinearLayout) rootView.findViewById(R.id.loading);
        showLoadingView(true);

        error = (TextView) rootView.findViewById(R.id.error);

        dynamicRecyclerView = (LRecyclerView) rootView.findViewById(R.id.rv_list_moments);
        //  RecyclerView的适配器
        adapterDynamicItem = new AdapterDynamicItem(getContext());
        dynamicRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        lRecyclerViewAdapter = new LRecyclerViewAdapter(adapterDynamicItem);
        dynamicRecyclerView.setAdapter(lRecyclerViewAdapter);
        dynamicRecyclerView.setLoadMoreEnabled(true);
        dynamicRecyclerView.setRefreshProgressStyle(ProgressStyle.LineSpinFadeLoader);
        dynamicRecyclerView.setArrowImageView(R.drawable.ic_pulltorefresh_arrow);
        dynamicRecyclerView.setLoadingMoreProgressStyle(ProgressStyle.BallSpinFadeLoader);
        dynamicRecyclerView.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh() {
                flag = IS_REFRESH;
                requestData();
            }
        });
        dynamicRecyclerView.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                flag = IS_LOAD_MORE;
                if(mCurrentCounter < TOTAL_COUNTER){
                    requestData();
                }else{
                    mHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            Message msg = Message.obtain();
                            msg.what = FINISH_N_REF;
                            mHandler.sendMessage(msg);
                        }
                    },500);
                }
            }
        });
        dynamicRecyclerView.setOnNetWorkErrorListener(new OnNetWorkErrorListener() {
            @Override
            public void reload() {
                requestData();
            }
        });
        lRecyclerViewAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Intent intent = new Intent();
                intent.setClass(getContext(), Dy_CompleteActivity.class);
                intent.putExtra("objId",dynamicMsgList.get(position).getObjectId());
                startActivity(intent);
            }
        });

        flag = IS_REFRESH;
        requestData();
    }

    private void showLoadingView(boolean show) {
        if(show){
            loading.setVisibility(View.VISIBLE);
        }else{
            loading.setVisibility(View.GONE);
        }
    }

    /**获取网络数据*/
    private void requestData(){
        query();
    }

    /**
     * 查询动态并显示在列表
     * flag 判断是下拉刷新还是上划加载
     */
    private void query() {
        new Thread(){
            private int offset;  // 动态数量的差值  说明有新动态

            @Override
            public void run() {
                try{
                    BmobQuery<DynamicMsg> query = new BmobQuery<>();
                    query.count(DynamicMsg.class, new CountListener() {
                        @Override
                        public void done(Integer integer, BmobException e) {
                            if(e == null){
                                offset = integer - TOTAL_COUNTER;
                                TOTAL_COUNTER = integer;

                                BmobQuery<DynamicMsg> query1 = new BmobQuery<>();
                                query1.include("user");
                                query1.order("-createdAt");

                                /** 对加载数据的细节处理！！*/
                                if(flag == IS_REFRESH && !isFirstRefresh && dynamicMsgList.size() != 0){
                                    query1.setLimit(REQUEST_COUNT);
                                }else if(flag == IS_LOAD_MORE && !isFirstLoadMore){
                                    query1.setLimit(REQUEST_COUNT);
                                    query1.setSkip(mCurrentCounter + offset);
                                }else if(flag == IS_REFRESH && isFirstRefresh){
                                    query1.setLimit(REQUEST_COUNT);
                                    isFirstRefresh = false;
                                }else if(flag == IS_LOAD_MORE && isFirstLoadMore){
                                    query1.setLimit(REQUEST_COUNT);
                                    query1.setSkip(REQUEST_COUNT + offset);
                                    isFirstLoadMore = false;
                                }else if(flag == IS_REFRESH && !isFirstRefresh){
                                    query1.setLimit(REQUEST_COUNT);
                                }
                                query1.findObjects(new FindListener<DynamicMsg>() {
                                    @Override
                                    public void done(List<DynamicMsg> list, BmobException e) {
                                        if(e == null){
                                            showLoadingView(false);

                                            if(flag == IS_REFRESH){
                                                dynamicMsgList.clear();
                                                mCurrentCounter = 0;
                                                isFirstLoadMore = true;
                                                dynamicMsgList.addAll(list);
                                            }else if(flag == IS_LOAD_MORE){
                                                dynamicMsgList.addAll(list);
                                                mCurrentCounter += dynamicMsgList.size();
                                            }
//                                            query_like_people();  //Todo 查询觉得很赞的用户
                                            adapterDynamicItem.setData(dynamicMsgList);
                                            Message msg = Message.obtain();
                                            msg.what = NOTIFY;
                                            mHandler.sendMessage(msg);
                                        }else{
                                            showError("页面加载失败");
                                            Log.e(TAG, "done: " + e.toString());
                                            Toast.makeText(getContext(),"数据请求异常!",Toast.LENGTH_SHORT).show();
                                            Message msg = Message.obtain();
                                            msg.what = FINISH_REFRESH;
                                            mHandler.sendMessage(msg);
                                        }
                                    }
                                });
                            }else{
                                Toast.makeText(getActivity(),"啊欧~ 出了点小问题...",Toast.LENGTH_SHORT).show();
                                finishRefresh();
                            }
                        }
                    });

                }catch (Exception e){
                    Message msg = Message.obtain();
                    msg.what = FINISH_REFRESH;
                    mHandler.sendMessage(msg);
                }
            }
        }.start();
    }

    private void showError(String content) {
        error.setText(content);
        error.setVisibility(View.VISIBLE);
    }

    /**
     * 查询觉得很赞的用户
     */
    private void query_like_people() {
        for(int i = dynamicMsgList.size() - 1; i >= 0; i --){
            BmobQuery<MyUser> query = new BmobQuery();
            DynamicMsg dynamic = new DynamicMsg();
            dynamic.setObjectId(dynamicMsgList.get(i).getObjectId());
            query.addWhereRelatedTo("likes",new BmobPointer(dynamic));
            final int finalI = i;
            query.findObjects(new FindListener<MyUser>() {
                @Override
                public void done(List<MyUser> list, BmobException e) {
                    if(e ==null){
                        dynamicMsgList.get(finalI).setLike_users(list);
                    }else{
                        query_like_people();
                    }
                }
            });
        }
        adapterDynamicItem.setData(dynamicMsgList);
        Message msg = Message.obtain();
        msg.what = NOTIFY;
        mHandler.sendMessage(msg);
    }

    private class PreviewHandler extends Handler{
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case NOTIFY:
                    notifyDataSetChanged();
                    finishRefresh();
                    break;
                case FINISH_REFRESH:
                    finishRefresh();
                    break;
                case FINISH_N_REF:
                    finishRefresh();
                    dynamicRecyclerView.setNoMore(true);
                    break;
            }
        }
    }

    /**
     * 结束下拉刷新和上划加载
     */
    private void finishRefresh(){
        dynamicRecyclerView.refreshComplete(REQUEST_COUNT);
    }

    /**
     * 刷新UI
     */
    private void notifyDataSetChanged(){
        adapterDynamicItem.notifyDataSetChanged();
    }
}
