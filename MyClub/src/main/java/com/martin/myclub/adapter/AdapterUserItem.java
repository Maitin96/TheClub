package com.martin.myclub.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.martin.myclub.R;
import com.martin.myclub.activity.ChatActivity;
import com.martin.myclub.bean.UserItemMsg;

import java.util.List;

/**
 * Created by Martin on 2017/7/17.
 */
public class AdapterUserItem extends RecyclerView.Adapter<AdapterUserItem.BaseViewHolder> {

    private Context context;
    private List<UserItemMsg> userItemMsgList;

    public AdapterUserItem(Context context, List<UserItemMsg> userItemMsgList) {
        this.context = context;
        this.userItemMsgList = userItemMsgList;
    }

    @Override
    public AdapterUserItem.BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new BaseViewHolder(LayoutInflater.from(context).inflate(R.layout.item_user,parent,false));
    }

    @Override
    public void onBindViewHolder(AdapterUserItem.BaseViewHolder holder, int position) {
        holder.ivHeadPic.setImageResource(userItemMsgList.get(position).getIconID());
        holder.tvUsername.setText(userItemMsgList.get(position).getUsername());
        holder.tvSign.setText(userItemMsgList.get(position).getSign());
    }

    @Override
    public int getItemCount() {
        return userItemMsgList == null? 0:userItemMsgList.size();
    }

    class BaseViewHolder extends RecyclerView.ViewHolder{

        private ImageView ivHeadPic;
        private TextView tvUsername;
        private TextView tvSign;

        public BaseViewHolder(View itemView) {
            super(itemView);
            ivHeadPic = (ImageView) itemView.findViewById(R.id.iv_hp);
            tvUsername = (TextView) itemView.findViewById(R.id.tv_item_username);
            tvSign = (TextView) itemView.findViewById(R.id.tv_item_sign);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    Toast.makeText(context, tvUsername.getText().toString(), Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(context, ChatActivity.class);
                    intent.putExtra("username",tvUsername.getText().toString());
                    context.startActivity(intent);
                }
            });
        }
    }
}
