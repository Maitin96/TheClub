package com.martin.myclub.activity.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.martin.myclub.R;
import com.martin.myclub.adapter.AdapterDynamicItem;
import com.martin.myclub.bean.DynamicMsg;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Martin on 2017/7/10.
 * 我的关注页
 */
public class LayoutFocus extends Fragment {

    private View rootView;

    private List<DynamicMsg> dynamicMsgList;
    private RecyclerView dynamicRecyclerView;
    private AdapterDynamicItem adapterDynamicItem;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.layout_focus,container,false);
        initViews();
        return rootView;
    }

    private void initViews(){
        dynamicRecyclerView = (RecyclerView) rootView.findViewById(R.id.rv_list_focus);

        dynamicMsgList = new ArrayList<>();
        for (int i = 1;i < 9;i++){
            DynamicMsg dynamicMsg = new DynamicMsg();
            dynamicMsg.setContent("ah,ah,ah,let's go!");

            long currentTime = System.currentTimeMillis();
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy年-MM月dd日-HH时mm分ss秒");
            Date date = new Date(currentTime);

            dynamicMsg.setTime(""+formatter.format(date));
            dynamicMsgList.add(dynamicMsg);

            adapterDynamicItem = new AdapterDynamicItem(getContext());
            dynamicRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            dynamicRecyclerView.setAdapter(adapterDynamicItem);
        }
    }
}
