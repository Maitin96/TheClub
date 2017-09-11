package com.martin.myclub.activity;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;

import com.martin.myclub.R;
import com.martin.myclub.adapter.AdapterMainViewPager;
import com.martin.myclub.util.BottomNavigationViewHelper;
import com.martin.myclub.activity.fragment.LayoutAllClub;
import com.martin.myclub.activity.fragment.LayoutDynamic;
import com.martin.myclub.activity.fragment.LayoutMessage;
import com.martin.myclub.activity.fragment.LayoutPerson;
import com.martin.myclub.view.NoScrollViewPager;

/**
 * Created by Martin on 2017/7/2.
 * 主页面，包含4个tab
 */
public class MainActivity extends AppCompatActivity {

    private NoScrollViewPager viewPager;
    private BottomNavigationView navigationView;
    private MenuItem menuItem;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initViews();
        initBombIM();
    }

    private void initViews() {
        setContentView(R.layout.activity_main);

        viewPager = (NoScrollViewPager) findViewById(R.id.viewpager);
        navigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation);
        //去除BottomNavigationView个数大于3时的shiftMode效果
        BottomNavigationViewHelper.disableShiftMode(navigationView);
        navigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                //将ViewPager和底部的BottomNavigationView绑定
                switch (item.getItemId()){
                    case R.id.menu_dynamic:
                        viewPager.setCurrentItem(0);
                        break;
                    case R.id.menu_message:
                        viewPager.setCurrentItem(1);
                        break;
                    case R.id.menu_club:
                        viewPager.setCurrentItem(2);
                        break;
                    case R.id.menu_person:
                        viewPager.setCurrentItem(3);
                        break;
                }
                return false;
            }
        });

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            //设置点击获取焦点
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                if (menuItem != null){
                    menuItem.setChecked(false);
                } else {
                    navigationView.getMenu().getItem(0).setChecked(false);
                }
                menuItem = navigationView.getMenu().getItem(position);
                menuItem.setChecked(true);
            }

            @Override
            public void onPageSelected(int position) {
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        //禁止ViewPager滑动
        viewPager.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });

        AdapterMainViewPager adapter = new AdapterMainViewPager(getSupportFragmentManager());
        adapter.addFragment(new LayoutDynamic());
        adapter.addFragment(new LayoutMessage());
        adapter.addFragment(new LayoutAllClub());
        adapter.addFragment(new LayoutPerson());

        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(4);
    }

    private void initBombIM(){

    }
}