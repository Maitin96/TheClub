package com.martin.myclub.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.martin.myclub.R;
import com.martin.myclub.bean.ClubApply;
import com.martin.myclub.util.User;
import com.martin.myclub.view.CircleImageView;

import java.util.List;

/**
 * Created by Martin on 2017/7/17.
 * 联系人列表ListView的adapter
 */
public class ClubSortAdapter extends BaseAdapter {

    private List<ClubApply> list = null;
    private Context mContext;

    public ClubSortAdapter(Context mContext) {
        this.mContext = mContext;
    }

    public void setData(List<ClubApply> list){
        this.list = list;
    }

    public int getCount() {
        return this.list == null ? 0 : list.size();
    }

    public Object getItem(int position) {
        return list.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(final int position, View view, ViewGroup arg2) {
        ViewHolder viewHolder;
        final ClubApply club = list.get(position);
        if (view == null) {
            viewHolder = new ViewHolder();
            view = LayoutInflater.from(mContext).inflate(R.layout.item_contacts, null);
            viewHolder.name = (TextView) view.findViewById(R.id.name);
            viewHolder.logo = (CircleImageView)view.findViewById(R.id.iv_club_logo);
            viewHolder.catalog = (TextView) view.findViewById(R.id.catalog);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }

        //根据position获取首字母作为目录catalog
        String catalog = list.get(position).getFirstLetter();

        //如果当前位置等于该分类首字母的Char的位置 ，则认为是第一次出现
        if(position == getPositionForSection(catalog)){
            viewHolder.catalog.setVisibility(View.VISIBLE);
            viewHolder.catalog.setText(club.getFirstLetter().toUpperCase());
        }else{
            viewHolder.catalog.setVisibility(View.GONE);
        }

        viewHolder.name.setText(this.list.get(position).getName());
        if(list.get(position).getLogo() != null){
            Glide.with(mContext).load(list.get(position).getLogo().getUrl()).into(viewHolder.logo);
        }

        return view;
    }

    final static class ViewHolder {
        CircleImageView logo;
        TextView catalog;
        TextView name;
    }

    /**
     * 获取catalog首次出现位置
     */
    public int getPositionForSection(String catalog) {
        for (int i = 0; i < getCount(); i++) {
            String sortStr = list.get(i).getFirstLetter();
            if (catalog.equalsIgnoreCase(sortStr)) {
                return i;
            }
        }
        return -1;
    }
}