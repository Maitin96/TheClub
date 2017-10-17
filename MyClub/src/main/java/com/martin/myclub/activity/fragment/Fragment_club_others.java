package com.martin.myclub.activity.fragment;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.martin.myclub.R;
import com.martin.myclub.activity.ClubActivity;
import com.martin.myclub.activity.ClubMemberActivity;
import com.martin.myclub.activity.MemberApplyActivity;
import com.martin.myclub.bean.ClubApply;
import com.martin.myclub.bean.MyUser;
import com.martin.myclub.view.CircleImageView;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.QueryListener;

/**
 * 社团管理员进行社团管理
 * Created by Edward on 2017/8/26.
 */

public class Fragment_club_others extends Fragment {

    private View rootView;
    private MyUser currentUser;
    private LinearLayout ll_member;
    private LinearLayout ll_brief;
    private String clubObjId;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_club_others, container, false);
        currentUser = BmobUser.getCurrentUser(MyUser.class);
        clubObjId = getActivity().getIntent().getStringExtra("clubObjId");
        initView();
        return rootView;
    }

    private void initView() {
        ll_member = (LinearLayout) rootView.findViewById(R.id.ll_member);
        ll_brief = (LinearLayout) rootView.findViewById(R.id.ll_brief);

        ll_member.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), ClubMemberActivity.class);
                intent.putExtra("clubObjId",clubObjId);
                startActivity(intent);
            }
        });

        ll_brief.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestData();
            }
        });
    }

    private void requestData() {
        BmobQuery<ClubApply> query = new BmobQuery<>();
        query.getObject(clubObjId, new QueryListener<ClubApply>() {
            @Override
            public void done(ClubApply clubApply, BmobException e) {
                if(e == null){
                    showClubDialog(clubApply);
                }else{
                    Toast.makeText(getContext(),"获取社团资料失败",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void showClubDialog(ClubApply clubApply) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        final AlertDialog dialog = builder.create();
        View view = View.inflate(getContext(), R.layout.dialog_club_brief, null);
        CircleImageView iv_club_logo = (CircleImageView) view.findViewById(R.id.iv_club_logo);
        TextView tv_club_name = (TextView) view.findViewById(R.id.tv_club_name);
        TextView tv_brief = (TextView) view.findViewById(R.id.tv_brief);
        Button button = (Button) view.findViewById(R.id.btn_enter);

        if(clubApply.getLogo() != null){
            Glide.with(getActivity()).load(clubApply.getLogo().getUrl()).into(iv_club_logo);
        }

        tv_club_name.setText(clubApply.getName());
        tv_brief.setText(clubApply.getIntroduction());

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ClubActivity.class);
                intent.putExtra("clubObjId",clubObjId);
                startActivity(intent);
                dialog.dismiss();
            }
        });
        dialog.setView(view);
        dialog.show();
    }
}
