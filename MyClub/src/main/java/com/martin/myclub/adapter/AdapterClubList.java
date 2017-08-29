package com.martin.myclub.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.martin.myclub.R;
import com.martin.myclub.bean.ClubApply;

import java.util.List;

/**
 * Created by Martin on 2017/7/18.
 * 社团列表adapter
 */
public class AdapterClubList extends RecyclerView.Adapter<AdapterClubList.ViewHolder> {

    private List<ClubApply> mClubList;
    private Context mContext;

    public AdapterClubList(Context context){
        mContext = context;
    }

    public void setData(List<ClubApply> list){
        mClubList = list;
    }

    @Override
    public AdapterClubList.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.club_item,parent,false);//填充item到布局文件
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(AdapterClubList.ViewHolder holder, int position) {
        //这个方法是赋予属性 这样理解 赋予给viewHolder 好放在item里
        ClubApply clubMsg = mClubList.get(position);
        holder.clubName.setText(clubMsg.getName());
        Glide.with(mContext).load(clubMsg.getLogo().getUrl()).into(holder.clubImage);
    }

    @Override
    public int getItemCount() {
        return mClubList == null ? 0 : mClubList.size() ;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView clubImage;
        private TextView clubName;

        public ViewHolder(View itemView) {
            super(itemView);
            clubImage = (ImageView) itemView.findViewById(R.id.item_image);
            clubName = (TextView) itemView.findViewById(R.id.item_text);

        }
    }
}

