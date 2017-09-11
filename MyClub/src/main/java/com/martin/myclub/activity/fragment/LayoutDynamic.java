package com.martin.myclub.activity.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.martin.myclub.R;
import com.martin.myclub.activity.WriteDynamicActivity;
import com.martin.myclub.adapter.AdapterMainViewPager;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Martin on 2017/7/3.
 * 动态Fragment的排版页面
 */
public class LayoutDynamic extends Fragment {

    private View rootView;

    private ViewPager viewPager;
    private TabLayout tabLayout;
    private List<TabLayout.Tab> tabList;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.layout_dynamic,container,false);
        initViews();
        return rootView;
    }

    private void initViews(){
        ImageView imageView = (ImageView) rootView.findViewById(R.id.write);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), WriteDynamicActivity.class);
                startActivity(intent);
            }
        });

        viewPager = (ViewPager) rootView.findViewById(R.id.vp_dt);
        tabLayout = (TabLayout) rootView.findViewById(R.id.tl_dt);

        tabList = new ArrayList<>();

        AdapterMainViewPager adapterDynamic = new AdapterMainViewPager(getFragmentManager());

        adapterDynamic.addFragment(new LayoutAll());
        adapterDynamic.addFragment(new LayoutFocus());

        viewPager.setAdapter(adapterDynamic);
        viewPager.setOffscreenPageLimit(2);
        tabLayout.setupWithViewPager(viewPager);

        tabList.add(tabLayout.getTabAt(0));
        tabList.add(tabLayout.getTabAt(1));

        tabList.get(0).setText("推荐");
        tabList.get(1).setText("我的关注");
    }
}
