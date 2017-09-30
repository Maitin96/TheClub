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
import com.martin.myclub.bean.ClubDynamic;
import com.martin.myclub.bean.ClubSendActivity;

import java.util.List;

/**
 * Created by Edward
 * 社团活动adapter
 */
public class AdapterClubActivity extends RecyclerView.Adapter<AdapterClubActivity.ViewHolder> {

    private List<ClubSendActivity> list;
    private Context mContext;

    public AdapterClubActivity(Context context){
        mContext = context;
    }

    public void setData(List<ClubSendActivity> list){
        this.list = list;
    }

    @Override
    public AdapterClubActivity.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_club_activity,parent,false);//填充item到布局文件
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(AdapterClubActivity.ViewHolder holder, int position) {
        //这个方法是赋予属性 这样理解 赋予给viewHolder 好放在item里
        ClubSendActivity a = list.get(position);
        holder.userName.setText(a.getUser().getUsername());
        holder.title.setText(a.getTitle());
        holder.time.setText(a.getCreatedAt());

        if(a.getUser().getDp() != null){   //设置用户头像
            Glide.with(mContext).load(a.getUser().getDp().getUrl()).into(holder.userDp);
        }
        if(a.getPic() != null){  ///设置动态发表的图片
            Glide.with(mContext).load(a.getPic().getUrl()).into(holder.pic);
        }else{
            holder.pic.setImageDrawable(null);
            holder.pic.setVisibility(View.GONE);
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
        private TextView title;
        private ImageView pic;

        public ViewHolder(View itemView) {
            super(itemView);
            userDp = (ImageView) itemView.findViewById(R.id.iv_hp);
            userName = (TextView) itemView.findViewById(R.id.tv_username);
            time = (TextView) itemView.findViewById(R.id.tv_time);
            title = (TextView) itemView.findViewById(R.id.tv_title);
            pic = (ImageView) itemView.findViewById(R.id.iv_pic);
        }
    }
}

