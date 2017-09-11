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

import java.util.List;

/**
 * Created by Edward on 2017/8/26.
 * 社团管理中已加入的社团列表Adapter
 */
public class AdapterClubManager extends RecyclerView.Adapter<AdapterClubManager.ViewHolder> implements View.OnClickListener{

    private List<ClubApply> mClubList;
    private Context mContext;
    private OnItemClickListener mOnItemClickListener = null;

    public AdapterClubManager(Context context){
        mContext = context;
    }

    public void setData(List<ClubApply> list){
        mClubList = list;
    }

    @Override
    public AdapterClubManager.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_club_manager,parent,false);//填充item到布局文件
        ViewHolder holder = new ViewHolder(view);
        view.setOnClickListener(this);
        return holder;
    }

    @Override
    public void onBindViewHolder(AdapterClubManager.ViewHolder holder, int position) {
        holder.itemView.setTag(position);
        ClubApply club = mClubList.get(position);
        holder.tv_club_name.setText(club.getName());
        holder.tv_principal.setText(club.getRealName());
        Glide.with(mContext).load(club.getLogo().getUrl()).into(holder.iv_club_logo);
    }

    @Override
    public int getItemCount() {
        return mClubList == null ? 0 : mClubList.size() ;
    }

    @Override
    public void onClick(View v) {
        if(mOnItemClickListener != null){
            mOnItemClickListener.onItemClick(v, (int) v.getTag());
        }
    }

    public void setOnItemClickListener(OnItemClickListener listener){
        this.mOnItemClickListener = listener;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView iv_club_logo;
        private TextView tv_club_name;
        private TextView tv_principal;

        public ViewHolder(View itemView) {
            super(itemView);
            iv_club_logo = (ImageView) itemView.findViewById(R.id.iv_club_logo);
            tv_club_name = (TextView) itemView.findViewById(R.id.tv_club_name);
            tv_principal = (TextView) itemView.findViewById(R.id.tv_principal);

        }
    }

    public static interface OnItemClickListener{
        void onItemClick(View v, int position);
    }
}

