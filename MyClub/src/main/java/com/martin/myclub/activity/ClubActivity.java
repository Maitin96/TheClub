package com.martin.myclub.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import com.martin.myclub.R;
import com.martin.myclub.activity.fragment.Fragment_club_activity;
import com.martin.myclub.activity.fragment.Fragment_club_announcement;
import com.martin.myclub.activity.fragment.Fragment_club_dynamic;
import com.martin.myclub.activity.fragment.Fragment_club_management;
import com.martin.myclub.adapter.AdapterClub;
import com.martin.myclub.bean.ClubApply;
import com.martin.myclub.bean.MyUser;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobPointer;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.QueryListener;
import cn.pedant.SweetAlert.SweetAlertDialog;

/**
 * 社团页面
 * Created by Edward on 2017/8/24.
 */

public class ClubActivity extends AppCompatActivity {

    private String clubObjId;
    private MyUser currentUser;
    private LinearLayout loadingLayout;
    private AppBarLayout barLayout;
    private ViewPager mViewPager;
    private TabLayout tabLayout;
    private ArrayList<TabLayout.Tab> tabList = new ArrayList<>();
    private ArrayList<Fragment> fragmentList = new ArrayList<>();
    private AdapterClub adapter;
    private CoordinatorLayout mainLayout;
    private Toolbar toolBar;
    private CollapsingToolbarLayout collapsing_toolbar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        clubObjId = getIntent().getStringExtra("clubObjId");
        currentUser = BmobUser.getCurrentUser(MyUser.class);
        setContentView(R.layout.activity_club);
        initView();
    }

    private void initView() {
        loadingLayout = (LinearLayout) findViewById(R.id.loading);
        barLayout = (AppBarLayout) findViewById(R.id.bar_layout);
        mainLayout = (CoordinatorLayout) findViewById(R.id.coordinatorLayout);
        collapsing_toolbar = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        toolBar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolBar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        mViewPager = (ViewPager) findViewById(R.id.viewpager);
        mViewPager.setOffscreenPageLimit(2);

        requestData();
    }

    private void requestData() {
        showMainInterface(false);

        BmobQuery<ClubApply> query = new BmobQuery<>();
        query.getObject(clubObjId, new QueryListener<ClubApply>() {
            @Override
            public void done(ClubApply clubApply, BmobException e) {
                if (e == null) {
                    setUIContent(clubApply);
                    queryAdmin();
                } else {
                    Log.e("ClubActivity:", "requestData: " + e.toString());
                    showRetryDialog();
                }
            }
        });
    }

    /**
     * 设置界面要显示的内容
     *
     * @param club
     */
    private void setUIContent(ClubApply club) {
        collapsing_toolbar.setTitle(club.getName());
    }

    private void queryAdmin() {
        BmobQuery<MyUser> query = new BmobQuery<>();
        ClubApply club = new ClubApply();
        club.setObjectId(clubObjId);
        query.addWhereRelatedTo("admin", new BmobPointer(club));
        query.findObjects(new FindListener<MyUser>() {
            @Override
            public void done(List<MyUser> list, BmobException e) {
                if (e == null) {
                    //管理员检测
                    for (MyUser user : list) {
                        if (currentUser.getObjectId().equals(user.getObjectId())) {
                            // Todo ViewPager多一项管理tab
                            setViewPager(true);
                        } else {
                            setViewPager(false);
                        }
                        showMainInterface(true);
                    }
                } else {
                    Log.e("ClubActivity:", "queryAdmin: " + e.toString());
                    showRetryDialog();
                }
            }
        });
    }

    /**
     * 是否显示主界面
     *
     * @param show
     */
    private void showMainInterface(boolean show) {
        if (show) {
            loadingLayout.setVisibility(View.INVISIBLE);
            mainLayout.setVisibility(View.VISIBLE);
        } else {
            loadingLayout.setVisibility(View.VISIBLE);
            mainLayout.setVisibility(View.INVISIBLE);
        }
    }

    private List<Fragment> setFragment(boolean isAdmin) {
        fragmentList.add(new Fragment_club_dynamic());
        fragmentList.add(new Fragment_club_activity());
        fragmentList.add(new Fragment_club_announcement());
        if (isAdmin) {
            fragmentList.add(new Fragment_club_management());
        }
        return fragmentList;
    }

    /**
     * 设置ViewPager的显示
     *
     * @param isAdmin
     */
    private void setViewPager(boolean isAdmin) {
        adapter = new AdapterClub(getSupportFragmentManager(), setFragment(isAdmin));
        mViewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(mViewPager);

        tabList.add(tabLayout.getTabAt(0));
        tabList.add(tabLayout.getTabAt(1));
        tabList.add(tabLayout.getTabAt(2));
        tabList.get(0).setText("社团");
        tabList.get(1).setText("活动");
        tabList.get(2).setText("通告");
        if (isAdmin) {
            tabList.add(tabLayout.getTabAt(3));
            tabList.get(3).setText("管理");
        }
    }

    private void showRetryDialog() {
        new SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE)
                .setTitleText("异常")
                .setContentText("加载出了点小问题,要重试一次吗？")
                .setConfirmText("再试试")
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        requestData();
                    }
                });
    }
}
