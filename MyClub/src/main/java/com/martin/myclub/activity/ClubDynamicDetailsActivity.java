package com.martin.myclub.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.text.SpannableString;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.github.jdsjlzx.interfaces.OnItemClickListener;
import com.github.jdsjlzx.recyclerview.LRecyclerView;
import com.github.jdsjlzx.recyclerview.LRecyclerViewAdapter;
import com.martin.myclub.R;
import com.martin.myclub.adapter.AdapterClubDyComp;
import com.martin.myclub.bean.ClubDynamic;
import com.martin.myclub.bean.ClubDynamicComment;
import com.martin.myclub.bean.MyUser;
import com.martin.myclub.util.UIUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobPointer;
import cn.bmob.v3.datatype.BmobRelation;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.QueryListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;
import cn.pedant.SweetAlert.SweetAlertDialog;

/**
 * 社团动态单个详情页面
 * Created by Edward on 2017/9/22.
 */


public class ClubDynamicDetailsActivity extends AppCompatActivity implements View.OnClickListener{

    private String TAG = "ClubDynamicDetailsActiv";

    private MyUser currentUser;
    private String dynamicObjId;

    private ClubDynamic mDynamic;

    private LinearLayout ll_dy;  // 没加载数据前先Gone
    private LinearLayout ll_loading;
    private EditText et_say;  // 来点评论吧~~~

    private ImageView iv_good;
    private ImageView iv_collect;
    private ImageView dy_content_image;
    private ImageView dy_hp;

    private TextView tv_send;  // 发送评论按钮
    private TextView dy__name;  // 名字
    private TextView dy_time;
    private TextView dy_content;

    private TextView tv_looked_time;
    private TextView tv_goods;
    private TextView tv_comments; //评论数
    private TextView tv_person_like; // Edward 等人觉得很赞~~~~

    private LRecyclerView mLRecyclerView;
    private LRecyclerViewAdapter mLRecyclerViewAdapter;
    private AdapterClubDyComp adapter;

    private int curr_comm_positon;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        currentUser = BmobUser.getCurrentUser(MyUser.class);
        dynamicObjId = getIntent().getStringExtra("dynamicObjId");
        setContentView(R.layout.activity_club_dy_details);
        initView();
    }

    private void initView() {
        View header = LayoutInflater.from(this).inflate(R.layout.header_club_dy_comp, null);
        ll_loading = (LinearLayout) findViewById(R.id.ll_loading);
        iv_collect = (ImageView) findViewById(R.id.iv_collect);

        ll_dy = (LinearLayout) header.findViewById(R.id.ll_dy);
        ll_dy.setVisibility(View.INVISIBLE);
        et_say = (EditText) header.findViewById(R.id.et_say);
        tv_send = (TextView) header.findViewById(R.id.tv_send);
        dy__name = (TextView) header.findViewById(R.id.dy__name);
        dy_time = (TextView) header.findViewById(R.id.dy_time);
        dy_content = (TextView) header.findViewById(R.id.dy_content);
        dy_content_image = (ImageView) header.findViewById(R.id.dy_content_image);
        dy_hp = (ImageView) header.findViewById(R.id.dy_hp);
        tv_looked_time = (TextView) header.findViewById(R.id.tv_looked_time);
        iv_good = (ImageView) header.findViewById(R.id.iv_good);
        tv_goods = (TextView) header.findViewById(R.id.tv_goods);
        tv_person_like = (TextView) header.findViewById(R.id.tv_person_like);
        tv_comments = (TextView) header.findViewById(R.id.tv_comments);

        mLRecyclerView = (LRecyclerView) findViewById(R.id.lRecyclerView);
        adapter = new AdapterClubDyComp(this);
        mLRecyclerViewAdapter = new LRecyclerViewAdapter(adapter);
        mLRecyclerViewAdapter.addHeaderView(header);
        mLRecyclerView.setAdapter(mLRecyclerViewAdapter);
        mLRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mLRecyclerView.setPullRefreshEnabled(false);

        //对评论内容设置点击事件
        mLRecyclerViewAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                curr_comm_positon = position;

                //弹到顶端 显示EditText
                LRecyclerView.LayoutManager layoutManager = mLRecyclerView.getLayoutManager();
                if (layoutManager instanceof LinearLayoutManager) {
                    LinearLayoutManager linearManager = (LinearLayoutManager) layoutManager;
                    linearManager.scrollToPositionWithOffset(0, 0);
                    linearManager.setStackFromEnd(true);
                }

                String username = commentList.get(position).getUser().getUsername();
                String s = "回复:" + username + "  ";
                SpannableString ss = UIUtils.getColorStr(s,"#0099EE");
                et_say.setText(ss);
                et_say.setSelection(ss.length());

            }
        });

        iv_collect.setOnClickListener(this);
        iv_good.setOnClickListener(this);
        tv_send.setOnClickListener(this);

        requestData();
    }

    private void requestData() {
        showLoadingView(true);
        if (TextUtils.isEmpty(dynamicObjId)) {
            showErrorDialog();
        }
        BmobQuery<ClubDynamic> query = new BmobQuery<>();
        query.include("user");
        query.getObject(dynamicObjId.trim(), new QueryListener<ClubDynamic>() {
            @Override
            public void done(ClubDynamic clubDynamic, BmobException e) {
                if (e == null) {
                    mDynamic = clubDynamic;
                    //增加浏览次数
                    save_looks();

                    setMainView(clubDynamic);
                } else {
                    Log.e("ClubDynamicDetailsActiv", "requestData  done: " + e.toString());
                    showRetryDialog(true);
                }
            }
        });
    }

    /**
     * 显示主界面信息
     *
     * @param clubDynamic
     */
    private void setMainView(ClubDynamic clubDynamic) {
        if (clubDynamic != null) {
            MyUser user = clubDynamic.getUser();
            dy__name.setText(user.getUsername());  //姓名
            if (user.getDp() != null) {      //头像
                // 如果用户头像已经设置过
                Glide.with(this).load(user.getDp().getUrl()).into(dy_hp);
            } else {
                // 如果没有  可以设置加载默认的图片
            }
            dy_time.setText(clubDynamic.getCreatedAt() + "");  //时间
            dy_content.setText(clubDynamic.getContent());  //内容
            if (clubDynamic.getPicture() != null) {   //图片
                Glide.with(this).load(clubDynamic.getPicture().getUrl()).into(dy_content_image);
            } else {
                dy_content_image.setVisibility(View.GONE);   //让图片隐藏
            }
            //查询点赞的用户
            queryGoods();
            queryCollect();
            queryComment();
            showRetryDialog(false);
            showLoadingView(false);
        }
    }

    /**
     * 查询评论
     */
    boolean query_comments = false;
    private void queryComment() {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                BmobQuery<ClubDynamicComment> query = new BmobQuery<>();
                query.addWhereEqualTo("dyObjId",dynamicObjId);
                query.include("user");
                query.findObjects(new FindListener<ClubDynamicComment>() {
                    @Override
                    public void done(List<ClubDynamicComment> list, BmobException e) {
                        if(e == null){
                            query_comments = true;

                            commentList = list;
                            updateComment_GoodNum();   //同步评论数

                            // 设置评论数
                            tv_comments.setText(list.size() + "");
                            sort();
                            refreshCommentData(commentList);
                        }
                    }
                });
            }
        });
        thread.start();

    }

    /**
     * 同步评论数
     * @param count
     */
    Thread comment_thread;
    private void updateComment_GoodNum() {
        comment_thread = new Thread(new Runnable() {
            @Override
            public void run() {
                ClubDynamic dynamic = new ClubDynamic();
                dynamic.setObjectId(dynamicObjId);
                dynamic.setGood(goodNum);
                dynamic.setComment(commentList.size());
                dynamic.update(new UpdateListener() {
                    @Override
                    public void done(BmobException e) {
                        if (e == null) {
                            Log.d(TAG, "updateComment_GoodNum: 同步评论数和点赞数成功！！" + commentList.size() + ":" + goodNum);
                        } else {
                            updateComment_GoodNum();
                        }
                    }
                });
            }
        });
        comment_thread.start();
    }

    /**
     * 给评论排序
     */
    List<ClubDynamicComment> rm_list;  // 要删除的列表
    private void sort() {
        rm_list = new ArrayList<>();

        List<String> sort = getSort();
        HashMap<String, List<ClubDynamicComment>> tdSortLists = getTdSortLists(sort);

        for(int i = 0 ; i < commentList.size() ; i++){
            ClubDynamicComment comment = commentList.get(i);
            if(!TextUtils.isEmpty(comment.getTarget_comm_id())){
                rm_list.add(comment);
            }
        }
        commentList.removeAll(rm_list);  //移除需要排序的评论

        for(Map.Entry<String, List<ClubDynamicComment>> entry : tdSortLists.entrySet()){       // 开始排序
            for(int j = 0; j < commentList.size(); j++){
                ClubDynamicComment comment = commentList.get(j);
                String target_comm_id = comment.getObjectId();
                if(entry.getKey().equals(target_comm_id)){
                    Log.e("获取到list", entry.getKey());
                    commentList.addAll(j+1,entry.getValue());
                    break;
                }
            }
        }
    }

    /**
     * 确定target Comment id 有多少组
     * @return
     */
    private List<String> getSort(){
        ArrayList<String> list = new ArrayList<>();
        for(int i = 0; i < commentList.size(); i ++ ){
            ClubDynamicComment comment = commentList.get(i);

            String target_comm_id = comment.getTarget_comm_id();
            if(target_comm_id != null && !list.contains(target_comm_id)){
                list.add(target_comm_id);
            }
        }
        return list;
    }

    /**
     *   通过target comm id 给评论分组
     * @param sort
     * @return
     */
    private LinkedHashMap<String,List<ClubDynamicComment>> getTdSortLists(List<String> sort) {
        LinkedHashMap<String,List<ClubDynamicComment>> maps = new LinkedHashMap<>();
        for(String tId : sort){
            List<ClubDynamicComment> list = new ArrayList<>();
            //根据 target_comm_id 给回复分组
            for(int i = 0 ; i < commentList.size() ; i++){
                ClubDynamicComment comment = commentList.get(i);
                String iter_tId = comment.getTarget_comm_id();
                if(!TextUtils.isEmpty(iter_tId) && tId.equals(iter_tId)){
                    list.add(comment);
                }
            }
            maps.put(tId,list);
        }
        return maps;
    }

    /**
     * 查询收藏用户
     */
    boolean query_collects = false;
    private void queryCollect() {
        if(mDynamic != null){
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    BmobQuery<MyUser> query_collect = new BmobQuery<>();
                    mDynamic.setObjectId(dynamicObjId);
                    query_collect.addWhereRelatedTo("collects",new BmobPointer(mDynamic));
                    query_collect.findObjects(new FindListener<MyUser>() {
                        @Override
                        public void done(List<MyUser> list, BmobException e) {
                            if(e == null){
                                query_collects = true;

                                mDynamic.setCollect_users(list);

                                for(int i = 0; i < list.size(); i++){
                                    if(list.get(i).getObjectId().equals(currentUser.getObjectId())){
                                        // 当前用户收藏了该动态
                                        iv_collect.setImageResource(R.drawable.collect_y);
                                    }
                                }
                            }else{
                                queryCollect();
                            }
                        }
                    });
                }
            });
            thread.start();
        }
    }

    /**
     * 查询点赞的用户
     */
    boolean query_goods = false;   //记录该函数是否完成
    int goodNum = 0;
    private void queryGoods() {
        if(mDynamic != null){
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    BmobQuery<MyUser> query_good = new BmobQuery<>();
                    mDynamic.setObjectId(dynamicObjId);
                    query_good.addWhereRelatedTo("goods", new BmobPointer(mDynamic));
                    query_good.findObjects(new FindListener<MyUser>() {
                        @Override
                        public void done(List<MyUser> list, BmobException e) {
                            if (e == null) {
                                query_goods = true;
                                goodNum = list.size();
                                mDynamic.setGoods_users(list);

                                mDynamic.setGood(list.size());   //设置点赞的总数
                                tv_goods.setText(list.size() + "");   //显示点赞数量

                                for (int i = 0; i < list.size(); i++) {  //查询“我”是否点了赞
                                    if (list.get(i).getObjectId().equals(currentUser.getObjectId())) {
                                        //我点过赞
                                        iv_good.setImageResource(R.drawable.like_red);
                                    }
                                }
                            } else {
                                queryGoods();
                            }
                        }
                    });
                }
            });
            thread.start();
        }else{
            Log.e("ClubDynamicDetailsActiv", "queryGoods: mDynamic为空！！！" );
        }

    }



    /**
     * 浏览 + 1
     */
    int curr_looks = 0;
    private void save_looks() {
        if (mDynamic != null) {
            curr_looks = mDynamic.getLookedTime();
            curr_looks += 1;
            new Thread() {
                @Override
                public void run() {
                    mDynamic.setLookedTime(curr_looks);
                    mDynamic.update(new UpdateListener() {
                        @Override
                        public void done(BmobException e) {
                            // 浏览次数 + 1
                            if (e == null) {
                                tv_looked_time.setText("浏览" + curr_looks + "次");
                            }
                        }
                    });
                }
            }.start();
        }
    }

    SweetAlertDialog mDialog;

    private void showRetryDialog(boolean show) {
        mDialog = new SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE);
        if (show) {
            mDialog.setTitleText("异常");
            mDialog.setContentText("加载出了点小问题,要重试一次吗？");
            mDialog.setConfirmText("再试试");
            mDialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                @Override
                public void onClick(SweetAlertDialog sweetAlertDialog) {
                    sweetAlertDialog.setTitleText("加载中");
                    sweetAlertDialog.setContentText("小团正在努力为您加载哦~");
                    sweetAlertDialog.setConfirmClickListener(null);
                    sweetAlertDialog.changeAlertType(SweetAlertDialog.PROGRESS_TYPE);
                    requestData();
                }
            });
            mDialog.show();
        } else {
            mDialog.dismiss();
        }
    }

    private void showLoadingView(boolean show) {
        if (show) {
            ll_loading.setVisibility(View.VISIBLE);
            ll_dy.setVisibility(View.INVISIBLE);
        } else {
            ll_loading.setVisibility(View.INVISIBLE);
            ll_dy.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 没有接收到ObjId  无法加载数据  请求用户退出
     */
    private void showErrorDialog() {
        //
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.iv_collect:
                //收藏
                collect();
                break;
            case R.id.iv_good:
                //点赞
                good();
                break;
            case R.id.tv_send:
                send();
                break;
        }
    }

    /**
     * 发送评论 并显示
     */
    List<ClubDynamicComment> commentList = new ArrayList<>();
    ClubDynamicComment comment;
    private void send() {
        String content = et_say.getText().toString().trim();
        if(!TextUtils.isEmpty(content)){

            if(et_say.getHint().toString().startsWith("我")){
                // 表示不是对评论内容回复
                comment = new ClubDynamicComment();
                // 不设置目标id 说明是对此动态的一级评论
                comment.setUser(currentUser);
                comment.setDyObjId(dynamicObjId);
                comment.setComment(et_say.getText().toString());
                comment.save(new SaveListener<String>() {
                    @Override
                    public void done(String s, BmobException e) {
                        if(e == null){
                            //清空输入框
                            et_say.setText("");
                            commentList.add(comment);
                            refreshCommentData(commentList);
                        }
                    }
                });

            }else if(et_say.getText().toString().startsWith("回复")){
                String s = et_say.getText().toString();

                String[] split = s.split("  ");
                String sp_head = split[0];
                String sp_body = split[1];
                String sp_target_username = sp_head.split(":")[1];

                comment = new ClubDynamicComment();
                comment.setUser(currentUser);
                comment.setDyObjId(dynamicObjId);
                comment.setTarget_comm_id(commentList.get(curr_comm_positon).getObjectId());
                comment.setTarget_comm_username(sp_target_username);
                comment.setComment(sp_body);
                comment.save(new SaveListener<String>() {
                    @Override
                    public void done(String s, BmobException e) {
                        if(e == null){
                            et_say.setText("");
                            commentList.add(curr_comm_positon + 1,comment);
                            refreshCommentData(commentList);
                        }
                    }
                });

            }
        }else{
            Toast.makeText(this,"您还没有填写信息哦~",Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 更新评论的UI
     * @param commentList
     */
    private void refreshCommentData(List<ClubDynamicComment> commentList) {
        adapter.setData(commentList);
        mLRecyclerViewAdapter.notifyDataSetChanged();
        mLRecyclerView.refreshComplete(1);
    }

    /**
     * 收藏
     */
    private void collect() {
        if(query_collects){
            ClubDynamic collectDynamic = new ClubDynamic();
            collectDynamic.setObjectId(dynamicObjId);
            BmobRelation relation = new BmobRelation();

            if(iv_collect.getDrawable().getCurrent().getConstantState().equals(ContextCompat.getDrawable(this,R.drawable.collect_w).getConstantState())){
                //  如果现在收藏是白色   说明没有收藏
                relation.add(currentUser);
                collectDynamic.setGood(goodNum);
                collectDynamic.setComment(commentList.size());
                collectDynamic.setCollects(relation);
                collectDynamic.update(new UpdateListener() {
                    @Override
                    public void done(BmobException e) {
                        if(e == null){
                            //上传完成  改变收藏为黄色
                            iv_collect.setImageResource(R.drawable.collect_y);
                        }     
                    }
                });
            }else{
                relation.remove(currentUser);
                collectDynamic.setGood(goodNum);
                collectDynamic.setComment(commentList.size());
                collectDynamic.setCollects(relation);
                // 如果已经收藏
                collectDynamic.update(new UpdateListener() {
                    @Override
                    public void done(BmobException e) {
                        if(e == null){
                            //上传完成
                            iv_collect.setImageResource(R.drawable.collect_w);
                        }
                    }
                });
            }

        }else{
            queryCollect();
        }
    }

    /**
     * 用户点赞
     */
    private void good() {
        if(query_goods){
            ClubDynamic goodDynamic = new ClubDynamic();
            goodDynamic.setObjectId(dynamicObjId);
            BmobRelation relation = new BmobRelation();

            if(iv_good.getDrawable().getCurrent().getConstantState().equals(ContextCompat.getDrawable(this,R.drawable.like_w).getConstantState())){
                relation.add(currentUser);
                goodDynamic.setGood(goodNum + 1);
                goodDynamic.setComment(commentList.size());
                goodDynamic.setGoods(relation);
                goodDynamic.update(new UpdateListener() {
                    @Override
                    public void done(BmobException e) {
                        if(e == null){
                            //上传完成  改变点赞为红色
                            Log.e(TAG, "done: 改变颜色为红色" + goodNum);
                            iv_good.setImageResource(R.drawable.like_red);
                            goodNum += 1;
                            tv_goods.setText(goodNum + "" );
                        }
                    }
                });
            }else{
                relation.remove(currentUser);
                goodDynamic.setGood(goodNum - 1);
                goodDynamic.setComment(commentList.size());
                goodDynamic.setGoods(relation);
                goodDynamic.update(new UpdateListener() {
                    @Override
                    public void done(BmobException e) {
                        if(e == null){
                            // 上传完成   改变颜色为白色
                            iv_good.setImageResource(R.drawable.like_w);
                            goodNum -= 1;
                            tv_goods.setText(goodNum + "");
                        }
                    }
                });
            }

        }else{
            queryGoods();
        }
    }
}
