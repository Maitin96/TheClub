package com.martin.myclub.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.martin.myclub.R;
import com.martin.myclub.bean.DynamicMsg;

import java.util.List;

/**
 * Created by Martin on 2017/7/10.
 * Dynamic页面的适配器
 */
public class AdapterDynamicItem extends RecyclerView.Adapter<AdapterDynamicItem.BaseViewHolder>{

    private Context context;
    private List<DynamicMsg> dynamicMsgList;

    public AdapterDynamicItem(Context context, List<DynamicMsg> dynamicMsgList) {
        this.context = context;
        this.dynamicMsgList = dynamicMsgList;
    }

    @Override
    public AdapterDynamicItem.BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new BaseViewHolder(LayoutInflater.from(context).inflate(R.layout.item_dynamic,parent,false));
    }

    @Override
    public void onBindViewHolder(AdapterDynamicItem.BaseViewHolder holder, int position) {
        holder.headpic.setImageResource(dynamicMsgList.get(position).getHeadPic());
        holder.username.setText(dynamicMsgList.get(position).getUserName());
        holder.time.setText(dynamicMsgList.get(position).getTime());
        holder.content.setText(dynamicMsgList.get(position).getContent());
        holder.picture.setImageResource(dynamicMsgList.get(position).getPicture());


    }

    @Override
    public int getItemCount() {
        return dynamicMsgList == null? 0 : dynamicMsgList.size();
    }

    class BaseViewHolder extends RecyclerView.ViewHolder {

        private ImageView headpic;
        private TextView username;
        private TextView time;
        private TextView content;
        private ImageView picture;


        public BaseViewHolder(View itemView) {
            super(itemView);
            headpic = (ImageView) itemView.findViewById(R.id.dy_hp);
            username = (TextView) itemView.findViewById(R.id.dy_name);
            time = (TextView) itemView.findViewById(R.id.dy_time);
            content = (TextView) itemView.findViewById(R.id.dy_content);
            picture = (ImageView) itemView.findViewById(R.id.dy_content_image);
        }
    }
}
