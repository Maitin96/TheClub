package com.martin.myclub.view;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.martin.myclub.R;
import com.martin.myclub.adapter.AdapterDynamicItem;
import com.martin.myclub.bean.DynamicMsg;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


/**
 * Created by Martin on 2017/7/10.
 * 动态页面
 */
public class LayoutAll extends Fragment {

    private View rootView;

    private List<DynamicMsg> dynamicMsgList;
    private RecyclerView dynamicRecyclerView;
    private AdapterDynamicItem adapterDynamicItem;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.layout_all,container,false);
        initViews();
        return rootView;
    }

    private void initViews(){
        dynamicRecyclerView = (RecyclerView) rootView.findViewById(R.id.rv_list_moments);

        initData();

        adapterDynamicItem = new AdapterDynamicItem(getContext(),dynamicMsgList);
        dynamicRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        dynamicRecyclerView.setAdapter(adapterDynamicItem);
    }

    private void initData(){
        dynamicMsgList = new ArrayList<>();
        for (int i = 1; i < 10; i++){
            DynamicMsg dynamicMsg = new DynamicMsg();
            dynamicMsg.setUserName("Coco");
            dynamicMsg.setHeadPic(R.drawable.avasterwe);
            dynamicMsg.setContent("I'm waiting for you " + i +"years");

            long currentTime = System.currentTimeMillis();
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy年-MM月dd日-HH时mm分ss秒");
            Date date = new Date(currentTime);

            dynamicMsg.setTime(formatter.format(date));
            dynamicMsg.setPicture(R.drawable.good);
            dynamicMsgList.add(dynamicMsg);
        }
    }
}
