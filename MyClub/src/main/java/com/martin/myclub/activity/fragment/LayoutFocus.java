package com.martin.myclub.activity.fragment;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.martin.myclub.R;
import com.martin.myclub.activity.ChatActivity;
import com.martin.myclub.activity.ClubActivity;
import com.martin.myclub.adapter.AdapterDynamicItem;
import com.martin.myclub.adapter.ClubSortAdapter;
import com.martin.myclub.bean.ClubApply;
import com.martin.myclub.bean.DynamicMsg;
import com.martin.myclub.util.User;
import com.martin.myclub.view.CircleImageView;
import com.martin.myclub.view.IdentityImageView;
import com.martin.myclub.view.SideBar;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

/**
 * Created by Martin on 2017/7/10.
 * 我的关注页
 */
public class LayoutFocus extends Fragment {

    private View rootView;

    private SideBar sideBar;

    private ClubSortAdapter adapter;
    private ListView list_view;
    List<ClubApply> clubList;
    private Button btn_reload;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.layout_focus,container,false);
        initView();
        return rootView;
    }

    private void initView(){
        btn_reload = (Button) rootView.findViewById(R.id.btn_reload);

        list_view = (ListView) rootView.findViewById(R.id.list_view);
        adapter = new ClubSortAdapter(getContext());
        list_view.setAdapter(adapter);

        sideBar = (SideBar) rootView.findViewById(R.id.side_bar);

        sideBar.setOnStrSelectCallBack(new SideBar.ISideBarSelectCallBack() {
            @Override
            public void onSelectStr(int index, String selectStr) {
                if(clubList != null){
                    for (int i = 0; i < clubList.size(); i++) {
                        if (selectStr.equalsIgnoreCase(clubList.get(i).getFirstLetter())) {
                            list_view.setSelection(i); // 选择到首字母出现的位置
                            return;
                        }
                    }
                }
            }
        });

        list_view.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                final AlertDialog dialog = builder.create();
                View dialogView = View.inflate(getContext(), R.layout.dialog_club_brief, null);
                CircleImageView iv_club_logo = (CircleImageView) dialogView.findViewById(R.id.iv_club_logo);
                TextView tv_club_name = (TextView) dialogView.findViewById(R.id.tv_club_name);
                TextView tv_brief = (TextView) dialogView.findViewById(R.id.tv_brief);
                Button button = (Button) dialogView.findViewById(R.id.btn_enter);

                final ClubApply club = clubList.get(position);
                if(club.getLogo() != null){
                    Glide.with(getActivity()).load(club.getLogo().getUrl()).into(iv_club_logo);
                }

                tv_club_name.setText(club.getName());
                tv_brief.setText(club.getIntroduction());

                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getActivity(), ClubActivity.class);
                        intent.putExtra("clubObjId",club.getObjectId());
                        startActivity(intent);
                        dialog.dismiss();
                    }
                });
                dialog.setView(dialogView);
                dialog.show();
            }
        });

        btn_reload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(btn_reload.getVisibility() == View.VISIBLE){
                    requestData();
                }
            }
        });

        requestData();
    }

    private void requestData() {
        BmobQuery<ClubApply> query = new BmobQuery<>();
        query.findObjects(new FindListener<ClubApply>() {
            @Override
            public void done(List<ClubApply> list, BmobException e) {
                if(e == null){
                    Log.d("联系人页面：", "获取到数据: " + list.size());
                    clubList = list;
                    for(ClubApply club : clubList){
                        club.exec();
                    }
                    Collections.sort(clubList); // 对list进行排序，需要让User实现Comparable接口重写compareTo方法
                    adapter.setData(clubList);
                    adapter.notifyDataSetChanged();

                    btn_reload.setVisibility(View.GONE);

                    callBack();
                }else{
                    btn_reload.setVisibility(View.VISIBLE);
                }
            }

            private void callBack() {
                sideBar.setOnStrSelectCallBack(new SideBar.ISideBarSelectCallBack() {
                    @Override
                    public void onSelectStr(int index, String selectStr) {
                        for (int i = 0; i < clubList.size(); i++) {
                            if (selectStr.equalsIgnoreCase(clubList.get(i).getFirstLetter())) {
                                list_view.setSelection(i); // 选择到首字母出现的位置
                                return;
                            }
                        }
                    }
                });
            }
        });
    }
}
