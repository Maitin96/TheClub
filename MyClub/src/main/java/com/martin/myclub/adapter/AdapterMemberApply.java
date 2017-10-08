package com.martin.myclub.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.martin.myclub.R;
import com.martin.myclub.bean.ApplyToAddClub;
import com.martin.myclub.bean.MyUser;
import com.martin.myclub.util.Global;
import com.martin.myclub.view.CircleImageView;

import java.util.List;

/**
 * Created by Edward
 */
public class AdapterMemberApply extends RecyclerView.Adapter<AdapterMemberApply.BaseViewHolder> {

    private Context context;
    private List<ApplyToAddClub> list;

    public AdapterMemberApply(Context context) {
        this.context = context;
    }

    public void setData(List<ApplyToAddClub> list){
        this.list = list;
    }

    @Override
    public AdapterMemberApply.BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new BaseViewHolder(LayoutInflater.from(context).inflate(R.layout.item_user,parent,false));
    }

    @Override
    public void onBindViewHolder(AdapterMemberApply.BaseViewHolder holder, int position) {
        MyUser user = list.get(position).getUser();
        holder.tvUsername.setText(user.getUsername());
        holder.tvSign.setText(list.get(position).getContent());
        if(user.getDp() != null){
            Glide.with(context).load(user.getDp().getUrl()).into(holder.ivHeadPic);
        }else{
            Glide.with(context).load(Global.defDpUrl).into(holder.ivHeadPic);
        }
    }

    @Override
    public int getItemCount() {
        return list == null? 0 : list.size();
    }

    class BaseViewHolder extends RecyclerView.ViewHolder{

        private CircleImageView ivHeadPic;
        private TextView tvUsername;
        private TextView tvSign;

        public BaseViewHolder(View itemView) {
            super(itemView);
            ivHeadPic = (CircleImageView) itemView.findViewById(R.id.iv_hp);
            tvUsername = (TextView) itemView.findViewById(R.id.tv_item_username);
            tvSign = (TextView) itemView.findViewById(R.id.tv_item_sign);
        }
    }
}
