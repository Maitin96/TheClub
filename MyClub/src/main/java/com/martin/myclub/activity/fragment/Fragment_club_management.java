package com.martin.myclub.activity.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.martin.myclub.R;
import com.martin.myclub.activity.MemberApplyActivity;
import com.martin.myclub.bean.MyUser;

import cn.bmob.v3.BmobUser;

/**
 * 社团管理员进行社团管理
 * Created by Edward on 2017/8/26.
 */

public class Fragment_club_management extends Fragment {

    private View rootView;
    private MyUser currentUser;
    private LinearLayout ll_check;
    private LinearLayout ll_basic;
    private String clubObjId;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_club_namagement, container, false);
        currentUser = BmobUser.getCurrentUser(MyUser.class);
        clubObjId = getActivity().getIntent().getStringExtra("clubObjId");
        initView();
        return rootView;
    }

    private void initView() {
        ll_check = (LinearLayout) rootView.findViewById(R.id.ll_check);
        ll_basic = (LinearLayout) rootView.findViewById(R.id.ll_basic);

        ll_check.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), MemberApplyActivity.class);
                intent.putExtra("clubObjId",clubObjId);
                startActivity(intent);
            }
        });

        ll_basic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }
}
