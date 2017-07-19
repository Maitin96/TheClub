package com.martin.myclub.view;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.astuetz.PagerSlidingTabStrip;
import com.martin.myclub.R;
import com.martin.myclub.adapter.AdapterClubList;
import com.martin.myclub.adapter.AdapterMainViewPager;
import com.martin.myclub.bean.ClubMsg;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by Martin on 2017/7/10.
 * 社团列表，所有社团
 */
public class LayoutAllClub extends Fragment {
    private View rootView;
    private RecyclerView recyclerView;

    private ClubMsg[] clubMsgs = {new ClubMsg("One",R.drawable.head_pic),new ClubMsg("Two",R.drawable.head_pic),
            new ClubMsg("Three",R.drawable.head_pic), new ClubMsg("Four",R.drawable.head_pic)};
    private List<ClubMsg> clubMsgList = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.layout_all_club,container,false);
        initViews();
        return rootView;
    }

    private void initViews(){
        recyclerView = (RecyclerView) rootView.findViewById(R.id.rv_club);

        initClubData();

        RecyclerView recyclerView = (RecyclerView) rootView.findViewById(R.id.rv_club);
        GridLayoutManager layoutManager = new GridLayoutManager(getActivity(),2);
        recyclerView.setLayoutManager(layoutManager);
        AdapterClubList adapter = new AdapterClubList(clubMsgList);
        recyclerView.setAdapter(adapter);
    }

    private void initClubData() {
        for (int i = 0; i<28;i++){
            Random random = new Random();
            int index = random.nextInt(clubMsgs.length);
            clubMsgList.add(clubMsgs[index]);
        }
    }
}
