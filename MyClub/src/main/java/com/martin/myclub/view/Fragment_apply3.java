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

public class Fragment_apply3 extends Fragment {

    private View rootView;
    private Button btn_next;

    private ClubApplyInterface.setPage mCallback;
    private ClubApply mClubApply = new ClubApply();
    private EditText et_real_name;
    private EditText et_position;
    private EditText et_applicant_phone;
    private EditText et_applicant_qq;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_apply3,container,false);
        initViews();
        return rootView;
    }

    private void initViews() {
        et_real_name = (EditText) rootView.findViewById(R.id.et_real_name);
        et_position = (EditText) rootView.findViewById(R.id.et_position);
        et_applicant_phone = (EditText) rootView.findViewById(R.id.et_applicant_phone);
        et_applicant_qq = (EditText) rootView.findViewById(R.id.et_applicant_qq);

        btn_next = (Button) rootView.findViewById(R.id.btn_next);
        btn_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String realName = et_real_name.getText().toString();
                String appliPosition = et_position.getText().toString();  //申请人担任的职位
                String appliPhone = et_applicant_phone.getText().toString();
                String appliQQ = et_applicant_qq.getText().toString();
                if(!TextUtils.isEmpty(realName) && !TextUtils.isEmpty(appliPosition) &&
                        !TextUtils.isEmpty(appliPhone) &&!TextUtils.isEmpty(appliQQ) ){
                    mClubApply.setRealName(realName);
                    mClubApply.setPosition(appliPosition);
                    mClubApply.setApplicatPhone(appliPhone);
                    mClubApply.setApplicatQQ(appliQQ);

                    mCallback.saveClubMsg(mClubApply,3);
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

    public void setValues(){

    }
}
