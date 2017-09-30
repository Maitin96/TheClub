package com.martin.myclub.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.martin.myclub.R;
import com.martin.myclub.bean.ClubApply;
import com.martin.myclub.bean.ClubDynamic;

import java.util.List;

/**
 * Created by Martin on 2017/7/18.
 * 社团列表adapter
 */
public class AdapterClubDynamic extends RecyclerView.Adapter<AdapterClubDynamic.ViewHolder> {

    private List<ClubDynamic> list;
    private Context mContext;

    public AdapterClubDynamic(Context context){
        mContext = context;
    }

    public void setData(List<ClubDynamic> list){
        this.list = list;
    }

    @Override
    public AdapterClubDynamic.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_club_dynamic,parent,false);//填充item到布局文件
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(AdapterClubDynamic.ViewHolder holder, int position) {
        //这个方法是赋予属性 这样理解 赋予给viewHolder 好放在item里
        ClubDynamic dynamic = list.get(position);
        holder.userName.setText(dynamic.getUser().getUsername());
        holder.time.setText(dynamic.getCreatedAt());
        holder.content.setText(dynamic.getContent());
        holder.lookedTime.setText("浏览" + dynamic.getLookedTime() + "次");
        holder.good.setText(dynamic.getGood()+ "");
        holder.comment.setText(dynamic.getComment()+ "");


        if(dynamic.getUser().getDp() != null){   //设置用户头像
            Glide.with(mContext).load(dynamic.getUser().getDp().getUrl()).into(holder.userDp);
        }
        if(dynamic.getPicture() != null){  ///设置动态发表的图片
            Glide.with(mContext).load(dynamic.getPicture().getUrl()).into(holder.pic);
        }

    }

    @Override
    public int getItemCount() {
        return list == null ? 0 : list.size() ;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView userDp;
        private TextView userName;
        private TextView time;
        private TextView content;
        private ImageView pic;
        private TextView lookedTime; //浏览次数
        private TextView good;  //点赞次数
        private TextView comment;  //评论次数

        public ViewHolder(View itemView) {
            super(itemView);
            userDp = (ImageView) itemView.findViewById(R.id.dy_hp);
            userName = (TextView) itemView.findViewById(R.id.dy_username);
            time = (TextView) itemView.findViewById(R.id.dy_time);
            content = (TextView) itemView.findViewById(R.id.dy_content);
            pic = (ImageView) itemView.findViewById(R.id.iv_pic);
            lookedTime = (TextView) itemView.findViewById(R.id.tv_looked_time);
            good = (TextView) itemView.findViewById(R.id.tv_good);
            comment = (TextView) itemView.findViewById(R.id.tv_comment);
        }
    }
}

