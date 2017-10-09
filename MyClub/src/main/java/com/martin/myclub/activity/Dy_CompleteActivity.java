package com.martin.myclub.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.github.jdsjlzx.interfaces.OnItemClickListener;
import com.github.jdsjlzx.interfaces.OnRefreshListener;
import com.github.jdsjlzx.recyclerview.LRecyclerView;
import com.github.jdsjlzx.recyclerview.LRecyclerViewAdapter;
import com.martin.myclub.R;
import com.martin.myclub.adapter.AdapterDyComp;
import com.martin.myclub.bean.DynamicComment;
import com.martin.myclub.bean.DynamicMsg;
import com.martin.myclub.bean.MyUser;
import com.martin.myclub.util.UIUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobPointer;
import cn.bmob.v3.datatype.BmobRelation;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.QueryListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;

/**
 * Powered by Edward on 2017/7/23.
 * 动态的详情页
 */

public class Dy_CompleteActivity extends AppCompatActivity implements View.OnClickListener{

    private static final int SHOW_LIKES = 1;
    private static final int SHOW_COLLECTS = 2;
    private boolean isLike = false;
    private boolean isCollect = false;

    private int looks = 0;  //浏览次数
    private int curr_comm_position;

    private String objId;   //获取到的动态Id
    private LinearLayout ll_dy;  // 没加载数据前先Gone
    private LinearLayout ll_loading;

    private EditText et_say;  // 来点评论吧~~~

    private ImageView iv_like;
    private ImageView iv_collect;
    private ImageView dy_content_image;

    private TextView tv_send;  // 发送评论按钮
    private TextView dy__name;  // 名字
    private TextView dy_time;
    private TextView dy_content;
    private TextView dy_title;

    private TextView tv_looked_time;
    private TextView tv_likes;
    private TextView tv_comments; //评论数
    private TextView tv_person_like; // Edward 等人觉得很赞~~~~

    private LRecyclerView rv_comment; // 评论内容


    private AdapterDyComp dycAdapter;
    private LRecyclerViewAdapter viewAdapter;

    private PreviewHandler mHandler = new PreviewHandler();
    private MyUser curr_user;

    private DynamicMsg mDynamic;
    private List<DynamicComment> dynamic_comment_list;



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dy_comp);
        Intent intent = getIntent();
        objId = intent.getStringExtra("objId");
        curr_user = BmobUser.getCurrentUser(MyUser.class);
        initViews();
    }

    /**
     * 浏览 + 1
     */
    int curr_looks = 0;
    private void save_looks() {
        if(mDynamic != null){
            looks = mDynamic.getLook_times();
            curr_looks = looks + 1;
            new Thread(){
                @Override
                public void run() {
                    mDynamic.setLook_times(looks + 1);
                    mDynamic.setObjectId(objId);
                    mDynamic.update(new UpdateListener() {
                        @Override
                        public void done(BmobException e) {
                            // 浏览次数 + 1
                            tv_looked_time.setText("浏览" +curr_looks + "次");
                        }
                    });
                }
            }.start();
        }
    }

    private void initViews() {
        View header = LayoutInflater.from(this).inflate(R.layout.header_dy_comp, null);
        ll_loading = (LinearLayout) findViewById(R.id.ll_loading);
        iv_collect = (ImageView) findViewById(R.id.iv_collect);

        ll_dy = (LinearLayout) header.findViewById(R.id.ll_dy);
        ll_dy.setVisibility(View.INVISIBLE);
        et_say = (EditText) header.findViewById(R.id.et_say);
        tv_send = (TextView) header.findViewById(R.id.tv_send);
        dy__name = (TextView) header.findViewById(R.id.dy__name);
        dy_time = (TextView) header.findViewById(R.id.dy_time);
        dy_title = (TextView) header.findViewById(R.id.dy_title);
        dy_content = (TextView) header.findViewById(R.id.dy_content);
        dy_content_image = (ImageView) header.findViewById(R.id.dy_content_image);
        tv_looked_time = (TextView) header.findViewById(R.id.tv_looked_time);
        iv_like = (ImageView) header.findViewById(R.id.iv_like);
        tv_likes = (TextView) header.findViewById(R.id.tv_likes);
        tv_person_like = (TextView) header.findViewById(R.id.tv_person_like);
        tv_comments = (TextView) header.findViewById(R.id.tv_comments);

        iv_like.setOnClickListener(this);
        iv_collect.setOnClickListener(this);
        tv_send.setOnClickListener(this);
        

        rv_comment = (LRecyclerView) findViewById(R.id.rv_comment);
        rv_comment.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh() {
                queryData();
            }
        });

        dycAdapter = new AdapterDyComp(this);

        viewAdapter = new LRecyclerViewAdapter(dycAdapter);
        viewAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                curr_comm_position = position;

                //弹到顶端 显示EditText
                LRecyclerView.LayoutManager layoutManager = rv_comment.getLayoutManager();
                if (layoutManager instanceof LinearLayoutManager) {
                    LinearLayoutManager linearManager = (LinearLayoutManager) layoutManager;
                    linearManager.scrollToPositionWithOffset(0, 0);
                    linearManager.setStackFromEnd(true);
                }
                String username = dynamic_comment_list.get(position).getUser().getUsername();
                String s = "回复:" + username + "  ";
                SpannableString ss = UIUtils.getColorStr(s,"#0099EE");
                et_say.setText(ss);
                et_say.setSelection(ss.length());
                showSoftInputFromWindow(Dy_CompleteActivity.this,et_say);
            }
        });
        viewAdapter.addHeaderView(header);
        rv_comment.setAdapter(viewAdapter);
        rv_comment.setLayoutManager(new LinearLayoutManager(this));
        rv_comment.setLoadMoreEnabled(false);

        queryData();
    }


    /**
     * 显示动态
     */
    private void queryData() {
        if(objId != null){
            BmobQuery<DynamicMsg> q = new BmobQuery<>();
            q.include("user");
            q.getObject(objId, new QueryListener<DynamicMsg>() {
                @Override
                public void done(DynamicMsg dynamicMsg, BmobException e) {
                    if (e == null){
                        mDynamic = dynamicMsg;
                        save_looks();

                        // 查询点赞的用户
                        BmobQuery<MyUser> q1 = new BmobQuery<>();
                        DynamicMsg dynamic = new DynamicMsg();
                        dynamic.setObjectId(objId);
                        q1.addWhereRelatedTo("likes", new BmobPointer(dynamic));
                        q1.findObjects(new FindListener<MyUser>() {
                            @Override
                            public void done(List<MyUser> list, BmobException e) {
                                if(e == null){
                                    mDynamic.setLike_users(list);
                                    mDynamic.setLike_counts(list.size());

                                    Message msg = Message.obtain();
                                    msg.obj = mDynamic;
                                    msg.what = SHOW_LIKES;
                                    mHandler.sendMessage(msg);
                                }
                            }
                        });

                        BmobQuery<MyUser> q2 = new BmobQuery<>();
                        DynamicMsg dynamic2 = new DynamicMsg();
                        dynamic2.setObjectId(objId);
                        q2.addWhereRelatedTo("collects",new BmobPointer(dynamic2));
                        q2.findObjects(new FindListener<MyUser>() {
                            @Override
                            public void done(List<MyUser> list, BmobException e) {
                                if(e == null){
                                    mDynamic.setCollect_users(list);

                                    Message msg = Message.obtain();
                                    msg.obj = mDynamic;
                                    msg.what = SHOW_COLLECTS;
                                    mHandler.sendMessage(msg);
                                }
                            }
                        });
                        /**查询评论*/
                        query_comment();
                    }
                }
            });
        }
    }

    /**
     * 查询评论
     */
    private void query_comment() {
        BmobQuery<DynamicComment> query = new BmobQuery<>();
        query.addWhereEqualTo("dyObjId",objId);
        query.include("user");
        query.findObjects(new FindListener<DynamicComment>() {
            @Override
            public void done(List<DynamicComment> list, BmobException e) {
                if(e == null){
                    dynamic_comment_list = list;

                    //设置评论数
                    mDynamic.setComment_counts(list.size());
                    tv_comments.setText(mDynamic.getComment_counts() + "");
                    sort();
                    refreshCommentData(dynamic_comment_list);
                }else{
                    query_comment();
                }
            }
        });
    }

    /**
     * 对评论进行排序
     */
    List<DynamicComment> rm_list;  // 要删除的列表
    private void sort() {
        rm_list = new ArrayList<>();

        List<String> sort = getSort();
        HashMap<String, List<DynamicComment>> tdSortLists = getTdSortLists(sort);

        for(int i = 0 ; i < dynamic_comment_list.size() ; i++){
            DynamicComment comment = dynamic_comment_list.get(i);
            if(!TextUtils.isEmpty(comment.getTarget_comm_id())){
                rm_list.add(comment);
            }
        }
        dynamic_comment_list.removeAll(rm_list);  //移除需要排序的评论

        for(Map.Entry<String, List<DynamicComment>> entry : tdSortLists.entrySet()){       // 开始排序
            for(int j = 0; j < dynamic_comment_list.size(); j++){
                DynamicComment comment = dynamic_comment_list.get(j);
                String target_comm_id = comment.getObjectId();
                if(entry.getKey().equals(target_comm_id)){
                    Log.e("获取到list", entry.getKey());
                    dynamic_comment_list.addAll(j+1,entry.getValue());
                    break;
                }
            }
        }
    }

    /**
     *   通过target comm id 给评论分组
     * @param sort
     * @return
     */
    private LinkedHashMap<String,List<DynamicComment>> getTdSortLists(List<String> sort) {
        LinkedHashMap<String,List<DynamicComment>> maps = new LinkedHashMap<>();
        for(String tId : sort){
            List<DynamicComment> list = new ArrayList<>();
            //根据 target_comm_id 给回复分组
            for(int i = 0 ; i < dynamic_comment_list.size() ; i++){
                DynamicComment comment = dynamic_comment_list.get(i);
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
     * 确定target Comment id 有多少组
     * @return
     */
    private List<String> getSort(){
        ArrayList<String> list = new ArrayList<>();
        for(int i = 0; i < dynamic_comment_list.size(); i ++ ){
            DynamicComment comment = dynamic_comment_list.get(i);

            String target_comm_id = comment.getTarget_comm_id();
            if(target_comm_id != null && !list.contains(target_comm_id)){
                list.add(target_comm_id);
            }
        }
        return list;
    }

    /**
     * 向评论Adapter传数据
     * @param list
     */
    private void refreshCommentData(List<DynamicComment> list) {
        dycAdapter.setData(list);
        viewAdapter.notifyDataSetChanged();
        rv_comment.refreshComplete(1);
    }

    StringBuffer stringBuffer;
    /**
     * 信息展示
     * @param dynamicMsg
     */
    private void show(DynamicMsg dynamicMsg) {
        ll_loading.setVisibility(View.GONE);
        ll_dy.setVisibility(View.VISIBLE);
        dy__name.setText(dynamicMsg.getUser().getUsername());
        dy_time.setText(dynamicMsg.getCreatedAt());
        if(dynamicMsg.getPicture() != null){
            Glide.with(this).load(dynamicMsg.getPicture().getUrl()).into(dy_content_image);
        }else{
            dy_content_image.setImageDrawable(null);
            dy_content_image.setVisibility(View.GONE);
        }
        dy_title.setText(dynamicMsg.getTitle());
        dy_content.setText(dynamicMsg.getContent());
        tv_likes.setText(dynamicMsg.getLike_counts() + "");
        //   Edward觉得很赞!
        List<MyUser> like_users = dynamicMsg.getLike_users();
        stringBuffer = new StringBuffer();
        for (MyUser user : like_users){
            stringBuffer.append("," + user.getUsername());
        }
        if(stringBuffer != null && stringBuffer.length() > 0){
            String text = stringBuffer.toString().substring(1,stringBuffer.length()) + "觉得很赞!";
            tv_person_like.setText(text);
        }


        // 设置赞和收藏

        if(like_users != null){
            for(MyUser user : like_users){
                if(curr_user.getObjectId().equals(user.getObjectId())){
                    isLike = true;
                    iv_like.setImageResource(R.drawable.like_red);
                }else{
                    isLike = false;
                    iv_like.setImageResource(R.drawable.like_w);
                }
            }
        }

        List<MyUser> collect_users = dynamicMsg.getCollect_users();
        if (collect_users != null){
            for(MyUser user : collect_users){
                if(curr_user.getObjectId().equals(user.getObjectId())){
                    isCollect = true;
                    iv_collect.setImageResource(R.drawable.collect_y);
                }else{
                    isCollect = false;
                    iv_collect.setImageResource(R.drawable.collect_w);
                }
            }
        }

        ll_dy.requestLayout();
        rv_comment.refreshComplete(0);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            /**点赞*/
            case R.id.iv_like:
                setLike();
                break;
            case R.id.iv_collect:
                setCollect();
                break;
            case R.id.tv_send:
                setComment();
                break;
        }
    }

    /**
     * 添加评论
     */
    DynamicComment dc = null;
    private void setComment() {
        String sp_head = "";
        String sp_body = "";
        String sp_target_user = "";
        String s = et_say.getText().toString();
        if(!TextUtils.isEmpty(s.trim())){
            if(s.startsWith("回复")){
                DynamicComment comment = dynamic_comment_list.get(curr_comm_position);
                String target_comm_id = comment.getObjectId();
                String[] split = s.split("  ");
                sp_head = split[0];
                sp_body = split[1];
                sp_target_user = sp_head.split(":")[1];
                // 将回复信息插入到对应位置
                dc = new DynamicComment();
                dc.setUser(curr_user);
                dc.setTarget_comm_username(sp_target_user);
                dc.setComment(sp_body);
                dc.setDyObjId(objId);
                dc.setTarget_comm_id(target_comm_id);
                dc.save(new SaveListener<String>() {
                    @Override
                    public void done(String s, BmobException e) {
                        if(e == null){
                            et_say.setText("");
                            dynamic_comment_list.add(curr_comm_position + 1,dc);
                            refreshCommentData(dynamic_comment_list);
                        }
                    }
                });

            }else{
                dc = new DynamicComment();
                dc.setUser(curr_user);
                dc.setDyObjId(objId);
                dc.setComment(s);
                dc.save(new SaveListener<String>() {
                    @Override
                    public void done(String s, BmobException e) {
                        if(e == null){
                            et_say.setText("");
                            dynamic_comment_list.add(dc);
                            refreshCommentData(dynamic_comment_list);
                        }else{
                            setComment();
                        }
                    }
                });
            }
            mDynamic.setComment_counts(dynamic_comment_list.size() + 1);
            mDynamic.update(new UpdateListener() {
                @Override
                public void done(BmobException e) {
                    //设置评论数
                    mDynamic.setComment_counts(dynamic_comment_list.size());
                    tv_comments.setText(mDynamic.getComment_counts() + "");
                    refreshCommentData(dynamic_comment_list);
                }
            });
        }
    }


    private class PreviewHandler extends Handler{
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case SHOW_LIKES:
                    show((DynamicMsg) msg.obj);
                    break;
                case SHOW_COLLECTS:
                    List<MyUser> collect_users = mDynamic.getCollect_users();
                    if (collect_users != null){
                        for(MyUser user : collect_users){
                            if(curr_user.getObjectId().equals(user.getObjectId())){
                                iv_collect.setImageResource(R.drawable.collect_y);
                            }else{
                                iv_collect.setImageResource(R.drawable.collect_w);
                            }
                        }
                    }
                    break;
            }
        }
    }

    /**
     * 点赞
     */
    private void setLike(){
        if (!isLike){
            DynamicMsg dynamic = new DynamicMsg();
            dynamic.setObjectId(objId);
            //将当前用户添加到DynamicMsg中的likes字段值中
            BmobRelation relation = new BmobRelation();
            //将当前用户添加到多对多关联中
            relation.add(curr_user);
            dynamic.setLikes(relation);
            dynamic.update(new UpdateListener() {
                @Override
                public void done(BmobException e) {
                    if(e == null){
                        isLike = true;
                        iv_like.setImageResource(R.drawable.like_red);
                        update_like_count();
//                        stringBuffer.append("," + curr_user.getUsername());
//                        String text = stringBuffer.toString().substring(1,stringBuffer.length()) + "觉得很赞!";
//                        tv_person_like.setText(text);
                    }else{
                        Toast.makeText(getApplicationContext(),"出了点小问题~",Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }else{
            DynamicMsg dynamic = new DynamicMsg();
            dynamic.setObjectId(objId);
            BmobRelation relation = new BmobRelation();
            relation.remove(curr_user);
            dynamic.setLikes(relation);
            dynamic.update(new UpdateListener() {
                @Override
                public void done(BmobException e) {
                    if(e == null){
                        isLike = false;
                        iv_like.setImageResource(R.drawable.like_w);
                        update_like_count();
//                        String s = stringBuffer.toString();
//                        s.replace("," + curr_user.getUsername(),"");
//                        String text = s.replace("," + curr_user.getUsername(),"").substring(1,stringBuffer.length()) + "觉得很赞!";
//                        tv_person_like.setText(text);
                    }else{
                        Toast.makeText(getApplicationContext(),"出了点小问题~",Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    /**
     * 更新评论数
     */
    int curr_likes;
    private void update_like_count() {
        int like_counts = mDynamic.getLike_counts();
        if(like_counts < 0){
            like_counts = 0;
        }
        if(!isLike){
            curr_likes = like_counts - 1;
            mDynamic.setLike_counts(curr_likes);
            mDynamic.setObjectId(objId);
            mDynamic.update( new UpdateListener() {
                @Override
                public void done(BmobException e) {
                    /**更新UI*/
                    tv_likes.setText(curr_likes + "");
                }
            });
        }else{
            curr_likes = like_counts + 1;
            mDynamic.setLike_counts(curr_likes);
            mDynamic.setObjectId(objId);
            mDynamic.update(new UpdateListener() {
                @Override
                public void done(BmobException e) {
                    /**更新UI*/
                    tv_likes.setText(curr_likes + "");
                }
            });
        }
    }

    /**
     * 收藏
     */
    private void setCollect() {
        if (!isCollect){
            DynamicMsg dynamic = new DynamicMsg();
            dynamic.setObjectId(objId);
            BmobRelation relation = new BmobRelation();
            relation.add(curr_user);
            dynamic.setCollects(relation);
            dynamic.update(new UpdateListener() {
                @Override
                public void done(BmobException e) {
                    if(e == null){
                        isCollect = true;
                        iv_collect.setImageResource(R.drawable.collect_y);
                    }else{
                        Toast.makeText(getApplicationContext(),"出了点小问题~",Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }else{
            DynamicMsg dynamic = new DynamicMsg();
            dynamic.setObjectId(objId);
            BmobRelation relation = new BmobRelation();
            relation.remove(curr_user);
            dynamic.setCollects(relation);
            dynamic.update(new UpdateListener() {
                @Override
                public void done(BmobException e) {
                    if(e == null){
                        isCollect = false;
                        iv_collect.setImageResource(R.drawable.collect_w);
                    }else{
                        Toast.makeText(getApplicationContext(),"出了点小问题~",Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    /**
     * EditText获取焦点并显示软键盘
     */
    public static void showSoftInputFromWindow(Activity activity, EditText editText) {
        editText.setFocusable(true);
        editText.setFocusableInTouchMode(true);
        editText.requestFocus();
        activity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
    }
}
