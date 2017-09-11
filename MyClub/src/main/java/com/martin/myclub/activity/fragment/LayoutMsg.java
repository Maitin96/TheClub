package com.martin.myclub.activity.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.martin.myclub.R;
import com.martin.myclub.adapter.AdapterUserItem;
import com.martin.myclub.bean.UserItemMsg;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Martin on 2017/7/3.
 * 联系人Fragment --> 消息页面
 */
public class LayoutMsg extends Fragment {
    private View rootView;
    private Context context;
    private List<UserItemMsg> userItemMsgList = new ArrayList<>();
    private RecyclerView rvMsg;
    private AdapterUserItem adapterUserItem;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.layout_msg,container,false);
        initViews();
        return rootView;
    }

    private void initViews(){
        context = getContext();
        rvMsg = (RecyclerView) rootView.findViewById(R.id.rv_msg);

        //滑动事件
        ItemTouchHelper.Callback callback = new ItemTouchHelper.SimpleCallback(
                ItemTouchHelper.UP|ItemTouchHelper.DOWN,ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                int fromPosition = viewHolder.getAdapterPosition();
                int toPosition = target.getAdapterPosition();
                if (fromPosition < toPosition) {
                    for (int i = fromPosition; i < toPosition; i++) {
                        Collections.swap(userItemMsgList, i, i + 1);
                    }
                } else {
                    for (int i = fromPosition; i > toPosition; i--) {
                        Collections.swap(userItemMsgList, i, i - 1);
                    }
                }
                adapterUserItem.notifyItemMoved(fromPosition, toPosition);
                return true;
            }
            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                userItemMsgList.remove(position);
                adapterUserItem.notifyItemRemoved(position);
            }
        };

        loadData();

        adapterUserItem = new AdapterUserItem(context,userItemMsgList);

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(callback);
        itemTouchHelper.attachToRecyclerView(rvMsg);

        rvMsg.setLayoutManager(new LinearLayoutManager(context));
        rvMsg.setItemAnimator(new DefaultItemAnimator());
        rvMsg.setAdapter(adapterUserItem);

    }

    private void loadData(){
        for (int i = 0; i < 10; i++) {
            UserItemMsg userItemMsg = new UserItemMsg();
            userItemMsg.setIconID(R.drawable.head_pic);
            userItemMsg.setUsername("Tony Stark");
            userItemMsg.setSign("You know who I am !");
            userItemMsgList.add(userItemMsg);
        }
    }
}
