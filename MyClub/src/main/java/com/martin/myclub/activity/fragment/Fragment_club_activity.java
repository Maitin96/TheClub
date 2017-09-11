package com.martin.myclub.activity.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.martin.myclub.R;

/**
 * 社团举办的活动
 * Created by Edward on 2017/8/26.
 */

public class Fragment_club_activity extends Fragment {

    private View rootView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_club_dynamic, container, false);
        return rootView;
    }
}
