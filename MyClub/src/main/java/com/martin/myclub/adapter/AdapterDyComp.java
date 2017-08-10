package com.martin.myclub.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.martin.myclub.R;
import com.martin.myclub.bean.DynamicComment;
import com.martin.myclub.util.UIUtils;

import java.util.List;

/**
 * Powered by Edward on 2017/7/23.
 */

public class AdapterDyComp extends RecyclerView.Adapter<AdapterDyComp.Holder> {
    private Context context;
    private List<DynamicComment> commentList;

    public AdapterDyComp(Context context){
        this.context = context;
    }

    public void setData(List<DynamicComment> commentList){
        this.commentList = commentList;
    }

    @Override
    public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new Holder(LayoutInflater.from(context).inflate(R.layout.item_dy_comp,null));
    }

    @Override
    public void onBindViewHolder(Holder holder, int position) {
        DynamicComment comment = commentList.get(position);
        if(TextUtils.isEmpty(comment.getTarget_comm_id())){
            holder.tv_username.setText(comment.getUser().getUsername());
            holder.tv_user_comment.setText(comment.getComment());
        }else{
            String head = "      " + comment.getUser().getUsername() + " --> " + comment.getTarget_comm_username();
            SpannableString ss = UIUtils.getColorStr(head, "#0099EE");
            holder.tv_username.setTextSize(9);
            holder.tv_username.setText(ss);
            holder.tv_user_comment.setText("     " + comment.getComment());
        }

    }

    @Override
    public int getItemCount() {
        return commentList == null ? 0 : commentList.size();
    }

    class Holder extends RecyclerView.ViewHolder{

        private ImageView iv_head; //用户头像
        private TextView tv_username; //用户名
        private TextView tv_user_comment; //用户评论

        public Holder(View itemView) {
            super(itemView);
            iv_head = (ImageView) itemView.findViewById(R.id.iv_head);
            tv_username = (TextView) itemView.findViewById(R.id.tv_username);
            tv_user_comment = (TextView) itemView.findViewById(R.id.tv_user_comment);
        }
    }
}
