package com.martin.myclub.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.martin.myclub.R;
import com.martin.myclub.bean.ClubSendActivity;
import com.martin.myclub.bean.MyUser;
import com.martin.myclub.util.Global;

import java.util.List;

/**
 * Created by Edward
 * 社团活动adapter
 */
public class AdapterClubMember extends RecyclerView.Adapter<AdapterClubMember.ViewHolder> {

    private List<MyUser> list;
    private Context mContext;

    public AdapterClubMember(Context context){
        mContext = context;
    }

    public void setData(List<MyUser> list){
        this.list = list;
    }

    @Override
    public AdapterClubMember.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_user,parent,false);//填充item到布局文件
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(AdapterClubMember.ViewHolder holder, int position) {
        //这个方法是赋予属性 这样理解 赋予给viewHolder 好放在item里
        MyUser user = list.get(position);
        System.out.println(user.getUsername());
        holder.userName.setText(user.getUsername());
        if (!TextUtils.isEmpty(user.getRealName())){
            holder.userRealName.setText("真实姓名：" + user.getRealName());
        }
        if(user.getDp() != null){   //设置用户头像
            Glide.with(mContext).load(user.getDp().getUrl()).into(holder.userDp);
        }else{
            Glide.with(mContext).load(Global.defDpUrl).into(holder.userDp);
        }

    }

    @Override
    public int getItemCount() {
        return list == null ? 0 : list.size() ;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView userDp;
        private TextView userName;
        private TextView userRealName;

        public ViewHolder(View itemView) {
            super(itemView);
            userDp = (ImageView) itemView.findViewById(R.id.iv_hp);
            userName = (TextView) itemView.findViewById(R.id.tv_item_username);
            userRealName = (TextView) itemView.findViewById(R.id.tv_item_sign);
        }
    }
}

