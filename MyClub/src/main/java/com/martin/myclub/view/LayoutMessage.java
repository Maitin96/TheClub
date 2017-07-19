package com.martin.myclub.view;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.martin.myclub.R;
import com.martin.myclub.activity.FindFriendActivity;
import com.martin.myclub.activity.MainActivity;
import com.martin.myclub.adapter.AdapterMainViewPager;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Martin on 2017/7/3.
 * 消息页面
 */
public class LayoutMessage extends Fragment{

    private View rootView;
    private List<TabLayout.Tab> tabList;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.layout_message,container,false);
        initViews();
        return rootView;
    }

    private void initViews(){
        TabLayout tabLayout = (TabLayout) rootView.findViewById(R.id.tl_msg);
        ViewPager viewPager = (ViewPager) rootView.findViewById(R.id.vp_msg);
        ImageView ivFind = (ImageView) rootView.findViewById(R.id.iv_find);
        ivFind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), FindFriendActivity.class);
                getContext().startActivity(intent);
            }
        });

        tabList = new ArrayList<>();

        AdapterMainViewPager adapterMsg = new AdapterMainViewPager(getFragmentManager());

        adapterMsg.addFragment(new LayoutMsg());
        adapterMsg.addFragment(new LayoutContacts());
        adapterMsg.addFragment(new LayoutClub());

        viewPager.setAdapter(adapterMsg);
        viewPager.setOffscreenPageLimit(3);
        tabLayout.setupWithViewPager(viewPager);

        tabList.add(tabLayout.getTabAt(0));
        tabList.add(tabLayout.getTabAt(1));
        tabList.add(tabLayout.getTabAt(2));

        tabList.get(0).setText("消息");
        tabList.get(1).setText("联系人");
        tabList.get(2).setText("社团");

    }
}