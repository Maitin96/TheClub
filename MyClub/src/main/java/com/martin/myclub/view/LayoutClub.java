package com.martin.myclub.view;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.astuetz.PagerSlidingTabStrip;
import com.martin.myclub.R;
import com.martin.myclub.activity.ChatActivity;
import com.martin.myclub.adapter.AdapterMainViewPager;
import com.martin.myclub.adapter.SortAdapter;
import com.martin.myclub.util.User;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Martin on 2017/7/3.
 * 加入的社团列表
 */
public class LayoutClub extends Fragment{

    private View rootView;
    private List<User> list;
    private ListView lvMyClub;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.layout_club,container,false);
        initViews();
        return rootView;
    }

    private void initViews(){
        list = new ArrayList<>();
        lvMyClub = (ListView) rootView.findViewById(R.id.lv_my_club);

        list.add(new User("轮滑社"));
        list.add(new User("跆拳道社"));
        list.add(new User("学生会"));

        SortAdapter adapter = new SortAdapter(getContext(),list);
        lvMyClub.setAdapter(adapter);

        lvMyClub.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getContext(), ChatActivity.class);
                intent.putExtra("username",list.get(position).getName());
                getContext().startActivity(intent);
            }
        });
    }

}