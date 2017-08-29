package com.martin.myclub.view;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.martin.myclub.R;
import com.martin.myclub.bean.ClubApply;

/**
 * Created by Administrator on 2017/8/15.
 */

public class Fragment_apply2 extends Fragment {

    private View rootView;

    private ClubApplyInterface.setPage mCallback;
    private ClubApply mClubApply = new ClubApply();
    private Button btn_next;
    private EditText et_club_phone;
    private EditText et_club_email;
    private EditText et_club_qq;
    private EditText et_club_remarks;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_apply2,container,false);
        initViews();
        return rootView;
    }

    private void initViews() {
        et_club_phone = (EditText) rootView.findViewById(R.id.et_club_phone);
        et_club_email = (EditText) rootView.findViewById(R.id.et_club_email);
        et_club_qq = (EditText) rootView.findViewById(R.id.et_club_qq);
        et_club_remarks = (EditText) rootView.findViewById(R.id.et_club_remarks);

        btn_next = (Button) rootView.findViewById(R.id.btn_next);
        btn_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String clubPhone = et_club_phone.getText().toString();
                String clubEmail = et_club_email.getText().toString();
                String clubQQ = et_club_qq.getText().toString();
                String clubRemarks = et_club_remarks.getText().toString();
                if(!TextUtils.isEmpty(clubPhone) && !TextUtils.isEmpty(clubEmail) &&
                        !TextUtils.isEmpty(clubQQ) &&!TextUtils.isEmpty(clubRemarks) ){
                    mClubApply.setPhone(clubPhone);
                    mClubApply.setEmail(clubEmail);
                    mClubApply.setQQ(clubQQ);
                    mClubApply.setRemarks(clubRemarks);

                    mCallback.saveClubMsg(mClubApply,2);
                }else{
                    Toast.makeText(getActivity(),"您还有没填写完整哦",Toast.LENGTH_SHORT).show();
                }
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
