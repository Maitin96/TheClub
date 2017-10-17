package com.martin.myclub.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.view.View;

import java.util.List;

/**
 * 社团详情页viewpager的适配器
 * Created by Edward on 2017/8/26.
 */

public class AdapterClub extends FragmentPagerAdapter {

    private List<Fragment> fragmentList;

    public AdapterClub(FragmentManager fm, List<Fragment> fragmentList) {
        super(fm);

        this.fragmentList = fragmentList;
    }

    @Override
    public Fragment getItem(int position) {
        Fragment fragment = fragmentList.get(position);
        return fragment;
    }

    @Override
    public Object instantiateItem(View container, int position) {
        Fragment fragment = fragmentList.get(position);
        fragment.notifyAll();
        return fragment;
    }

    @Override
    public int getItemPosition(Object object) {
        return PagerAdapter.POSITION_NONE;
    }

//    @Override
//    public int getItemPosition(Object object) {
//        return PagerAdapter.POSITION_UNCHANGED;
//    }

    @Override
    public int getCount() {
        return fragmentList.size();
    }
}
