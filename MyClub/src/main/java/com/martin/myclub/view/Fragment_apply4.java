package com.martin.myclub.view;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.martin.myclub.R;

/**
 * Created by Administrator on 2017/8/15.
 */

public class Fragment_apply4 extends Fragment {

    private View rootView;

    private ClubApplyInterface.setPage mCallback;
    private Button btn_finish;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_apply4,container,false);
        initViews();
        return rootView;
    }

    private void initViews() {
        btn_finish = (Button) rootView.findViewById(R.id.btn_finish);
        btn_finish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCallback.setPageByName("finish",3);
            }
        });
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if(context != null){
            mCallback = (ClubApplyInterface.setPage) context;

        }
    }
}
