package com.martin.myclub.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
public class AdapterMember extends RecyclerView.Adapter<AdapterMember.BaseViewHolder> {

    private Context context;
    private List<MyUser> list;

    public AdapterMember(Context context) {
        this.context = context;
    }

    public void setData(List<MyUser> list){
        this.list = list;
    }

    @Override
    public AdapterMember.BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new BaseViewHolder(LayoutInflater.from(context).inflate(R.layout.item_user,parent,false));
    }

    @Override
    public void onBindViewHolder(AdapterMember.BaseViewHolder holder, int position) {
        MyUser member = list.get(position);
        holder.tvUsername.setText(member.getUsername());
        holder.tvSign.setText(member.getRealName());
        if(member.getDp() != null){
            Glide.with(context).load(member.getDp().getUrl()).into(holder.ivHeadPic);
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
