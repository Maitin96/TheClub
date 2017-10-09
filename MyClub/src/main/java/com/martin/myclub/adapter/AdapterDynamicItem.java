package com.martin.myclub.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.martin.myclub.R;
import com.martin.myclub.bean.DynamicMsg;
import com.martin.myclub.bean.MyUser;
import com.martin.myclub.util.Global;

import java.util.List;

/**
 * Created by Martin on 2017/7/10.
 * Dynamic 列表页面的适配器
 */
public class AdapterDynamicItem extends RecyclerView.Adapter<AdapterDynamicItem.BaseViewHolder>{

    private Context context;
    private List<DynamicMsg> dynamicMsgList;

    public AdapterDynamicItem(Context context) {
        this.context = context;
    }

    public void setData(List<DynamicMsg> list){
        this.dynamicMsgList = list;
    }

    @Override
    public AdapterDynamicItem.BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new BaseViewHolder(LayoutInflater.from(context).inflate(R.layout.item_dynamic,parent,false));
    }

    @Override
    public void onBindViewHolder(AdapterDynamicItem.BaseViewHolder holder, int position) {
        MyUser user = dynamicMsgList.get(position).getUser();

        if(user.getDp() != null){
            Glide.with(context).load(user.getDp().getUrl()).into(holder.headpic);
        }else{
            Glide.with(context).load(Global.defDpUrl).into(holder.headpic);
        }
        holder.username.setText(dynamicMsgList.get(position).getUser().getUsername());
        holder.time.setText(dynamicMsgList.get(position).getCreatedAt());

        holder.title.setText(dynamicMsgList.get(position).getTitle());
        holder.content.setText(dynamicMsgList.get(position).getContent());
        holder.tv_comment.setText(dynamicMsgList.get(position).getComment_counts() + "");

        //图片不显示！！


        holder.tv_looked_time.setText("浏览" + dynamicMsgList.get(position).getLook_times() + "次");

    }

    @Override
    public int getItemCount() {
        return dynamicMsgList == null? 0 : dynamicMsgList.size();
    }

    class BaseViewHolder extends RecyclerView.ViewHolder {

        private ImageView headpic;    //Todo  图片暂未写
        private TextView username;
        private TextView time;
        private TextView title;
        private TextView content;
        private TextView tv_comment;  //评论数
        private TextView tv_looked_time; //浏览次数

        public BaseViewHolder(View itemView) {
            super(itemView);
            headpic = (ImageView) itemView.findViewById(R.id.dy_hp);
            username = (TextView) itemView.findViewById(R.id.dy_name);
            time = (TextView) itemView.findViewById(R.id.dy_time);
            content = (TextView) itemView.findViewById(R.id.dy_content);
            title = (TextView)itemView.findViewById(R.id.tv_title);

            tv_comment = (TextView) itemView.findViewById(R.id.tv_comment);
            tv_looked_time = (TextView) itemView.findViewById(R.id.tv_looked_time);
        }
    }
}
