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

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_club_dynamic, container, false);
        initView();
        return rootView;
    }

    private void initView() {
        if (rootView != null) {
            mLRecyclerView = (LRecyclerView) rootView.findViewById(R.id.lRecyclerView);
            LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
            mLRecyclerView.setLayoutManager(layoutManager);
            mLRecyclerView.setRefreshProgressStyle(ProgressStyle.Pacman);
            mLRecyclerView.setArrowImageView(R.drawable.ic_pulltorefresh_arrow);
            adapter = new AdapterClubDynamic(getContext());
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
                    if (mClubDynamicList != null) {
                        // 设置点击事件
                        String dynamicObjId = mClubDynamicList.get(position).getObjectId();
                        Intent intent = new Intent();
                        intent.setClass(getContext(), ClubDynamicDetailsActivity.class);
                        intent.putExtra("dynamicObjId", dynamicObjId);
                        startActivity(intent);
                    } else {
                        showRetryDialog(true);
                    }
                }
            });
            requestData();
        }
    }

    /**
     * 获取动态
     */
    private void requestData() {
        Thread thread = new Thread(new Runnable() {
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
        });
        thread.start();
    }

    private class PreviewHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case SET_DATA_TO_ADAPTER:
                    setDataToAdapter(mClubDynamicList);
                    showRetryDialog(false);
                    break;
                case SHOW_RETRY:
                    showRetryDialog(true);
                    break;
                case FINISH_REFRESH:
                    finishRefresh();
                    break;
            }
        }
    }

    /**
     * 设置社团动态数据
     *
     * @param mClubDynamicList
     */
    private void setDataToAdapter(List<ClubDynamic> mClubDynamicList) {
        adapter.setData(mClubDynamicList);
        adapter.notifyDataSetChanged();
    }


    /**
     * 加载失败重试提示
     */
    SweetAlertDialog mDialog;
    private void showRetryDialog(boolean show) {
        mDialog = new SweetAlertDialog(getActivity(), SweetAlertDialog.ERROR_TYPE);
        if (show) {
            mDialog.setTitleText("异常");
            mDialog.setContentText("加载出了点小问题,要重试一次吗？");
            mDialog.setConfirmText("再试试");
            mDialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                @Override
                public void onClick(SweetAlertDialog sweetAlertDialog) {
                    sweetAlertDialog.setTitleText("加载中");
                    sweetAlertDialog.setContentText("小团正在努力为您加载哦~");
                    sweetAlertDialog.setConfirmClickListener(null);
                    sweetAlertDialog.changeAlertType(SweetAlertDialog.PROGRESS_TYPE);
                    requestData();
                }
            });
            mDialog.show();
        }else{
            mDialog.dismiss();
        }
    }

    /**
     * 结束下拉刷新和上划加载
     */
    private void finishRefresh(){
        mLRecyclerView.refreshComplete(0);
    }

}
