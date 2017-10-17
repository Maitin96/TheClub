package com.martin.myclub.activity.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.jdsjlzx.interfaces.OnItemClickListener;
import com.github.jdsjlzx.interfaces.OnRefreshListener;
import com.github.jdsjlzx.recyclerview.LRecyclerView;
import com.github.jdsjlzx.recyclerview.LRecyclerViewAdapter;
import com.github.jdsjlzx.recyclerview.ProgressStyle;
import com.martin.myclub.R;
import com.martin.myclub.activity.ChatActivity;
import com.martin.myclub.adapter.AdapterMemberApply;
import com.martin.myclub.adapter.ClubSortAdapter;
import com.martin.myclub.adapter.SortAdapter;
import com.martin.myclub.bean.ApplyToAddClub;
import com.martin.myclub.bean.ClubApply;
import com.martin.myclub.bean.MyUser;
import com.martin.myclub.util.User;
import com.martin.myclub.view.IdentityImageView;
import com.martin.myclub.view.SideBar;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

/**
 * Created by Martin on 2017/7/3.
 * 联系人Fragment --> 联系人页面
 */
public class LayoutContacts extends Fragment {
    private View rootView;
    private SideBar sideBar;
    private ArrayList<User> list;
    private IdentityImageView iivContacts;

    private ClubSortAdapter adapter;
    private ListView list_view;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.layout_contacts,container,false);
//        initViews();
        return rootView;
    }

    private void initViews(){


        list_view = (ListView) rootView.findViewById(R.id.list_view);
        adapter = new ClubSortAdapter(getContext());
        list_view.setAdapter(adapter);

        sideBar = (SideBar) rootView.findViewById(R.id.side_bar);
//        iivContacts = (IdentityImageView) rootView.findViewById(R.id.iiv_contacts);

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

        list_view.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                Toast.makeText(getContext(),list.get(position).getName(),Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getContext(), ChatActivity.class);
                intent.putExtra("username",list.get(position).getName());
                getContext().startActivity(intent);
            }
        });

        requestData();
    }

    List<ClubApply> clubList;
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
                }
            }
        });
    }

    private void initData() {
        list = new ArrayList<>();
        list.add(new User("亳州")); // 亳[bó]属于不常见的二级汉字
        list.add(new User("大娃"));
        list.add(new User("二娃"));
        list.add(new User("三娃"));
        list.add(new User("四娃"));
        list.add(new User("五娃"));
        list.add(new User("六娃"));
        list.add(new User("七娃"));
        list.add(new User("喜羊羊"));
        list.add(new User("美羊羊"));
        list.add(new User("懒羊羊"));
        list.add(new User("沸羊羊"));
        list.add(new User("暖羊羊"));
        list.add(new User("慢羊羊"));
        list.add(new User("灰太狼"));
        list.add(new User("红太狼"));
        list.add(new User("孙悟空"));
        list.add(new User("黑猫警长"));
        list.add(new User("舒克"));
        list.add(new User("贝塔"));
        list.add(new User("海尔"));
        list.add(new User("阿凡提"));
        list.add(new User("邋遢大王"));
        list.add(new User("哪吒"));
        list.add(new User("没头脑"));
        list.add(new User("不高兴"));
        list.add(new User("蓝皮鼠"));
        list.add(new User("大脸猫"));
        list.add(new User("大头儿子"));
        list.add(new User("小头爸爸"));
        list.add(new User("蓝猫"));
        list.add(new User("淘气"));
        list.add(new User("叶峰"));
        list.add(new User("楚天歌"));
        list.add(new User("江流儿"));
        list.add(new User("Tom"));
        list.add(new User("Jerry"));
        list.add(new User("12345"));
        list.add(new User("54321"));
        list.add(new User("_(:з」∠)_"));
        list.add(new User("……%￥#￥%#"));

        Collections.sort(list); // 对list进行排序，需要让User实现Comparable接口重写compareTo方法
        SortAdapter adapter = new SortAdapter(getContext(), list);
        list_view.setAdapter(adapter);
    }
}
