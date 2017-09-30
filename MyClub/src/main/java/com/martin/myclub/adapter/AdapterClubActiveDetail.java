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
import com.martin.myclub.bean.ClubSendActivity;
import com.martin.myclub.bean.MyUser;
import com.martin.myclub.util.Global;
import com.martin.myclub.view.CircleImageView;
import com.martin.myclub.view.IdentityImageView;

import java.util.List;

/**
 * Created by Edward
 * 社团活动中显示已读成员的adapter
 */
public class AdapterClubActiveDetail extends RecyclerView.Adapter<AdapterClubActiveDetail.ViewHolder> {

    private List<MyUser> mClubList;
    private Context mContext;

    public AdapterClubActiveDetail(Context context){
        mContext = context;
    }

    public void setData(List<MyUser> list){
        mClubList = list;
    }

    @Override
    public AdapterClubActiveDetail.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_club_activity_member,parent,false);//填充item到布局文件
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(AdapterClubActiveDetail.ViewHolder holder, int position) {
        //这个方法是赋予属性 这样理解 赋予给viewHolder 好放在item里
        MyUser member = mClubList.get(position);
        if(member.getDp() != null){
            Glide.with(mContext).load(member.getDp().getUrl()).into(holder.iv_dp);
        }else{
            Glide.with(mContext).load(Global.defDpUrl).into(holder.iv_dp);
        }

    }

    @Override
    public int getItemCount() {
        return mClubList == null ? 0 : mClubList.size() ;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private CircleImageView iv_dp;

        public ViewHolder(View itemView) {
            super(itemView);
            iv_dp = (CircleImageView) itemView.findViewById(R.id.iv_dp);
        }
    }
}

