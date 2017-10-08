package com.martin.myclub.activity;

import android.app.AlertDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.github.jdsjlzx.interfaces.OnItemClickListener;
import com.github.jdsjlzx.interfaces.OnRefreshListener;
import com.github.jdsjlzx.recyclerview.LRecyclerView;
import com.github.jdsjlzx.recyclerview.LRecyclerViewAdapter;
import com.github.jdsjlzx.recyclerview.ProgressStyle;
import com.martin.myclub.R;
import com.martin.myclub.adapter.AdapterClubDynamic;
import com.martin.myclub.adapter.AdapterMemberApply;
import com.martin.myclub.bean.ApplyToAddClub;
import com.martin.myclub.bean.ClubApply;
import com.martin.myclub.bean.MyUser;
import com.martin.myclub.util.Global;
import com.martin.myclub.view.CircleImageView;

import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobRelation;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.UpdateListener;

/**
 * 成员申请审核
 * Created by Edward on 2017/9/27.
 */

public class MemberApplyActivity extends AppCompatActivity implements View.OnClickListener{

    private MyUser currentUser;
    private String clubObjId;
    private ImageView iv_return;
    private LinearLayout ll_loading;

    private AdapterMemberApply adapter;
    private LRecyclerViewAdapter recyclerViewAdapter;

    private List<ApplyToAddClub> applyList;
    private TextView title;
    private LRecyclerView mLRecyclerView;
    private TextView error;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        currentUser = BmobUser.getCurrentUser(MyUser.class);
        clubObjId = getIntent().getStringExtra("clubObjId");
        setContentView(R.layout.activity_member_apply);
        initView();
    }

    private void initView() {
        error = (TextView) findViewById(R.id.error);

        ll_loading = (LinearLayout) findViewById(R.id.loading);
        showLoadingView(true);

        iv_return = (ImageView) findViewById(R.id.iv_return);
        ImageView iv_send = (ImageView) findViewById(R.id.iv_send);
        iv_send.setVisibility(View.GONE);

        title = (TextView) findViewById(R.id.title);
        setTitle("成员审核");
        mLRecyclerView = (LRecyclerView) findViewById(R.id.lRecyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mLRecyclerView.setLayoutManager(layoutManager);
        mLRecyclerView.setRefreshProgressStyle(ProgressStyle.Pacman);
        mLRecyclerView.setArrowImageView(R.drawable.ic_pulltorefresh_arrow);
        adapter = new AdapterMemberApply(this);
        recyclerViewAdapter = new LRecyclerViewAdapter(adapter);
        mLRecyclerView.setAdapter(recyclerViewAdapter);
        mLRecyclerView.setLoadingMoreProgressStyle(ProgressStyle.BallSpinFadeLoader);
        mLRecyclerView.setLoadMoreEnabled(false);

        recyclerViewAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                showAgreeDialog(position);
            }
        });

        mLRecyclerView.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh() {
                requestData();
            }
        });

        requestData();
    }

    private void showAgreeDialog(final int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final AlertDialog dialog = builder.create();
        View view = View.inflate(this, R.layout.dialog_apply_detail, null);
        CircleImageView iv_dp = (CircleImageView) view.findViewById(R.id.iv_dp);
        TextView tv_username = (TextView) view.findViewById(R.id.tv_username);
        TextView tv_apply_time = (TextView) view.findViewById(R.id.tv_apply_time);
        TextView tv_content = (TextView) view.findViewById(R.id.tv_content);
        Button btn_ok = (Button) view.findViewById(R.id.btn_ok);
        Button btn_no = (Button) view.findViewById(R.id.btn_no);
        Button btn_think = (Button) view.findViewById(R.id.btn_think);

        if(applyList != null){
            final MyUser user = applyList.get(position).getUser();
            if(user.getDp() != null){
                Glide.with(this).load(user.getDp().getUrl()).into(iv_dp);
            }else{
                Glide.with(this).load(Global.defDpUrl).into(iv_dp);
            }
            tv_username.setText(user.getUsername());
            tv_content.setText(applyList.get(position).getContent());
            tv_apply_time.setText("申请时间:" + applyList.get(position).getCreatedAt());

            final ClubApply clubApply = new ClubApply();

            btn_ok.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    BmobRelation relation = new BmobRelation();
                    relation.add(user);
                    clubApply.setMember(relation);
                    clubApply.update(clubObjId, new UpdateListener() {
                        @Override
                        public void done(BmobException e) {
                             if(e == null){
                                 //同意后删掉申请信息
                                 ApplyToAddClub a = new ApplyToAddClub();
                                 a.setObjectId(applyList.get(position).getObjectId());
                                 a.delete(new UpdateListener() {
                                     @Override
                                     public void done(BmobException e) {
                                         if(e == null){
                                             applyList.remove(position);
                                             setDataToAdapter();
                                             Toast.makeText(MemberApplyActivity.this,user.getUsername() +" 成功加入您的社团！",Toast.LENGTH_SHORT).show();
                                             dialog.dismiss();
                                         }else{
                                             Toast.makeText(MemberApplyActivity.this,"网络异常,请重试",Toast.LENGTH_SHORT).show();
                                         }
                                     }
                                 });
                                 Log.d("同意该成员加入社团", "done: 成功!");
                             }else{
                                 Toast.makeText(MemberApplyActivity.this,"网络异常,请重试",Toast.LENGTH_SHORT).show();
                             }
                        }
                    });
                }
            });

            btn_no.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    applyList.remove(position);
                    setDataToAdapter();
                    dialog.dismiss();
                }
            });

            btn_think.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //不同意后删掉申请信息
                    ApplyToAddClub a = new ApplyToAddClub();
                    a.setObjectId(applyList.get(position).getObjectId());
                    a.delete(new UpdateListener() {
                        @Override
                        public void done(BmobException e) {
                            if(e == null){
                                applyList.remove(position);
                                setDataToAdapter();
                                Toast.makeText(MemberApplyActivity.this,"您没有同意 "+ user.getUsername() +" 加入您的社团",Toast.LENGTH_SHORT).show();
                                dialog.dismiss();
                            }else{
                                Toast.makeText(MemberApplyActivity.this,"网络异常,请重试",Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            });
        }
        dialog.setView(view);
        dialog.show();
    }

    private void requestData() {
        BmobQuery<ApplyToAddClub> query = new BmobQuery<>();
        query.addWhereEqualTo("clubId",clubObjId);
        query.include("user");
        query.findObjects(new FindListener<ApplyToAddClub>() {
            @Override
            public void done(List<ApplyToAddClub> list, BmobException e) {
                if(e == null){
                    showLoadingView(false);
                    applyList = list;
                    setDataToAdapter();
                    if(list.size() == 0 ){
                        showError("暂无成员需要审核哦");
                    }
                }
                finishRefresh();
            }
        });
    }

    /**
     * ，没有
     * @param content
     */
    private void showError(String content) {
        error.setText(content);
        error.setVisibility(View.VISIBLE);
    }
    private void setTitle(String title){
        this.title.setText(title);
    }

    private void setDataToAdapter() {
        if(applyList != null){
            adapter.setData(applyList);
            recyclerViewAdapter.notifyDataSetChanged();
        }
    }

    private void showLoadingView(boolean show) {
        if (show) {
            ll_loading.setVisibility(View.VISIBLE);
        } else {
            ll_loading.setVisibility(View.INVISIBLE);
        }
    }

    private void finishRefresh(){
        if(mLRecyclerView != null){
            mLRecyclerView.refreshComplete(0);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.iv_return:
                finish();
                break;
        }
    }
}
