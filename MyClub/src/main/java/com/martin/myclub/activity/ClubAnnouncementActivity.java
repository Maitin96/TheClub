package com.martin.myclub.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.github.jdsjlzx.recyclerview.LRecyclerView;
import com.github.jdsjlzx.recyclerview.LRecyclerViewAdapter;
import com.github.jdsjlzx.recyclerview.ProgressStyle;
import com.martin.myclub.R;
import com.martin.myclub.adapter.AdapterClubActiveDetail;
import com.martin.myclub.bean.CLubAnnouncement;
import com.martin.myclub.bean.ClubSendActivity;
import com.martin.myclub.bean.MyUser;
import com.martin.myclub.util.Global;

import java.io.Serializable;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobPointer;
import cn.bmob.v3.datatype.BmobRelation;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.QueryListener;
import cn.bmob.v3.listener.UpdateListener;

/**
 * 社团内部通告页面
 * Created by Edward on 2017/9/26.
 */

public class ClubAnnouncementActivity extends AppCompatActivity implements View.OnClickListener{

    private String TAG = "ClubActiveActivity";

    private MyUser currentUser;
    private String announcementId;
    private LinearLayout loading;
    private TextView title;
    private ImageView iv_return;
    private ImageView iv_dp;
    private TextView tv_username;
    private TextView tv_content;
    private ImageView iv_pic;
    private LRecyclerView mLRecyclerView;
    private LRecyclerViewAdapter recyclerViewAdapter;
    private AdapterClubActiveDetail adapter;
    private Button btn_read;
    private LinearLayout ll_main_view;
    private TextView tv_time;
    private TextView tv_look_member;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        currentUser = BmobUser.getCurrentUser(MyUser.class);
        announcementId = getIntent().getStringExtra("announcementId");
        setContentView(R.layout.activity_club_announcement);
        initView();
    }

    private void initView() {
        loading = (LinearLayout) findViewById(R.id.loading);
        showLoadingView(true);
        ll_main_view = (LinearLayout) findViewById(R.id.ll_main_view);
        showMainView(false);

        title = (TextView) findViewById(R.id.title);
        iv_return = (ImageView) findViewById(R.id.iv_return);

        //不使用 send按钮
        ImageView iv_send = (ImageView) findViewById(R.id.iv_send);
        iv_send.setClickable(false);
        iv_send.setVisibility(View.GONE);

        iv_dp = (ImageView) findViewById(R.id.iv_dp);
        tv_username = (TextView) findViewById(R.id.tv_username);
        tv_time = (TextView) findViewById(R.id.tv_time);
        tv_content = (TextView) findViewById(R.id.tv_content);
        iv_pic = (ImageView) findViewById(R.id.iv_pic);
        btn_read = (Button) findViewById(R.id.btn_read);
        tv_look_member = (TextView) findViewById(R.id.tv_look_member);  //查看已读成员

        iv_return.setOnClickListener(this);
        btn_read.setOnClickListener(this);
        tv_look_member.setOnClickListener(this);

        mLRecyclerView = (LRecyclerView) findViewById(R.id.lRecyclerView);
        GridLayoutManager layoutManager = new GridLayoutManager(this,10);
        mLRecyclerView.setLayoutManager(layoutManager);
        mLRecyclerView.setRefreshProgressStyle(ProgressStyle.Pacman);
        mLRecyclerView.setArrowImageView(R.drawable.ic_pulltorefresh_arrow);
        adapter = new AdapterClubActiveDetail(this);
        recyclerViewAdapter = new LRecyclerViewAdapter(adapter);
        mLRecyclerView.setAdapter(recyclerViewAdapter);
        mLRecyclerView.setLoadingMoreProgressStyle(ProgressStyle.BallSpinFadeLoader);
        mLRecyclerView.setPullRefreshEnabled(false);

        requestData();
    }

    private void showMainView(boolean show) {
        if(show){
            ll_main_view.setVisibility(View.VISIBLE);
        }else{
            ll_main_view.setVisibility(View.GONE);
        }
    }

    private void requestData() {
        if(!TextUtils.isEmpty(announcementId)){
            BmobQuery<CLubAnnouncement> query = new BmobQuery<>();
            query.include("user");
            query.getObject(announcementId, new QueryListener<CLubAnnouncement>() {
                @Override
                public void done(CLubAnnouncement cLubAnnouncement, BmobException e) {
                    if(e == null){
                        setTitle(cLubAnnouncement.getTitle());
                        setMainView(cLubAnnouncement);
                        queryReadMember();
                    }else{
                        showRetryDialog();
                    }
                }
            });
        }else{
            Toast.makeText(this,"无法获取该活动的id,id为空",Toast.LENGTH_SHORT).show();
        }

    }

    /**
     * 查询已读的成员
     */
    List<MyUser> memberList;
    private void queryReadMember() {
        BmobQuery<MyUser> query = new BmobQuery<>();
        CLubAnnouncement c = new CLubAnnouncement();
        c.setObjectId(announcementId);
        query.addWhereRelatedTo("readedMember",new BmobPointer(c));
        query.findObjects(new FindListener<MyUser>() {
            @Override
            public void done(List<MyUser> list, BmobException e) {
                if(e == null){
                    memberList = list;
                    updateReadedMember(memberList.size());
                    setDataToAdapter(memberList);
                    showMainView(true);
                    showLoadingView(false);
                }else{
                    Log.e(TAG, "queryReadMember: " +e.toString());
                    queryReadMember();
                }
            }
        });
    }

    /**
     * 同步已阅读成员的数量
     * @param count
     */
    private void updateReadedMember(final int count) {
        new Thread(){
            @Override
            public void run() {
                CLubAnnouncement bean = new CLubAnnouncement();
                bean.setObjectId(announcementId);
                bean.setReadedCount(count);
                bean.update(new UpdateListener() {
                    @Override
                    public void done(BmobException e) {
                        if(e == null){
                            Log.d(TAG, "done: 更新通告阅读人数成功");
                        }
                    }
                });
            }
        }.start();

    }

    private void setDataToAdapter(List<MyUser> memberList) {
        checkMe();
        adapter.setData(memberList);
        recyclerViewAdapter.notifyDataSetChanged();
    }

    /**
     * 设置数据
     * @param cLubAnnouncement
     */
    private void setMainView(CLubAnnouncement cLubAnnouncement) {
        MyUser user = cLubAnnouncement.getUser();
        if(user.getDp() != null){
            Glide.with(this).load(user.getDp().getUrl()).into(iv_dp);
        }else{
            Glide.with(this).load(Global.defDpUrl).into(iv_dp);
        }
        tv_username.setText(user.getUsername());
        tv_time.setText(cLubAnnouncement.getCreatedAt());
        tv_content.setText(cLubAnnouncement.getContent());
        if(cLubAnnouncement.getPic() != null){
            Glide.with(this).load(cLubAnnouncement.getPic().getUrl()).into(iv_pic);
        }else{
            iv_pic.setImageDrawable(null);
            iv_pic.setVisibility(View.GONE);
        }

    }

    private void setTitle(String title) {
        this.title.setText(title);
    }

    private void showLoadingView(boolean show) {
        if(show){
            loading.setVisibility(View.VISIBLE);
        }else{
            loading.setVisibility(View.GONE);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.iv_return:
                finish();
                break;
            case R.id.btn_read:
                uploadRead();
                break;
            case R.id.tv_look_member:
                lookMember();
                break;
        }
    }

    /**
     * 查看已读成员
     */
    private void lookMember() {
        Intent intent = new Intent(ClubAnnouncementActivity.this, AnnouncementLookMemberActivity.class);
        intent.putExtra("memberList",(Serializable) memberList);
        startActivity(intent);
    }

    /**
     * 上传‘我’已读过
     */
    private void uploadRead() {
        CLubAnnouncement clubSendActivity = new CLubAnnouncement();
        clubSendActivity.setObjectId(announcementId);
        BmobRelation bmobRelation = new BmobRelation();
        bmobRelation.add(currentUser);
        clubSendActivity.setReadedMember(bmobRelation);
        clubSendActivity.update(new UpdateListener() {
            @Override
            public void done(BmobException e) {
                if(e == null){
                    memberList.add(currentUser);
                    setDataToAdapter(memberList);
                    hintReadButton(true);
                }else{
                    Toast.makeText(ClubAnnouncementActivity.this,"发送请求失败，请重试",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    private boolean checkMe() {
        if(memberList != null){
            for(int i = 0; i < memberList.size(); i ++){
                //检测自己是否在其中
                if(currentUser.getObjectId().equals(memberList.get(i).getObjectId())){
                    //  我已阅读
                    hintReadButton(true);
                    return true;
                }
            }
            // 我暂无阅读
            hintReadButton(false);
            return false;
        }
        return false;
    }

    private void hintReadButton(boolean hint) {
        if(hint){
            btn_read.setVisibility(View.GONE);
        }else{
            btn_read.setVisibility(View.VISIBLE);
        }
    }


    private void showRetryDialog() {

    }
}
