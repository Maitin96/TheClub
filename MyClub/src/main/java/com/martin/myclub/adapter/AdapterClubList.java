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

import com.martin.myclub.R;
import com.martin.myclub.bean.ClubMsg;

import java.util.List;

/**
 * Created by Martin on 2017/7/18.
 * 社团列表adapter
 */
public class AdapterClubList extends RecyclerView.Adapter<AdapterClubList.ViewHolder> {

    private List<ClubMsg> mClubMsgList;
    private Context mContext;

    public AdapterClubList(List<ClubMsg> fruitList){
        mClubMsgList = fruitList;
    }

    @Override
    public AdapterClubList.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (mContext == null)
            mContext = parent.getContext();//获得上下文对象
        View view = LayoutInflater.from(mContext).inflate(R.layout.club_item,parent,false);//填充item到布局文件
        final ViewHolder holder = new ViewHolder(view);
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //点击事件
                int position = holder.getAdapterPosition();
                ClubMsg clubMsg = mClubMsgList.get(position);
//                Intent intent = new Intent(mContext,DeatilActivity.class);
//                intent.putExtra(DeatilActivity.NAME,fruit.getName());
//                intent.putExtra(DeatilActivity.IMAGE_ID,fruit.getImageId());
//                mContext.startActivity(intent);
                Toast.makeText(view.getContext(),"You clicked this!" + clubMsg.getClubName(),Toast.LENGTH_SHORT).show();
            }
        });

        return holder;
    }

    @Override
    public void onBindViewHolder(AdapterClubList.ViewHolder holder, int position) {
        //这个方法是赋予属性 这样理解 赋予给viewHolder 好放在item里
        ClubMsg clubMsg = mClubMsgList.get(position);
        holder.clubName.setText(clubMsg.getClubName());
        holder.clubImage.setImageResource(clubMsg.getImage());
    }

    @Override
    public int getItemCount() {
        //这三个方法是继承Adapter必须实现的
        return mClubMsgList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private final CardView cardView;
        private ImageView clubImage;
        private TextView clubName;

        public ViewHolder(View itemView) {
            super(itemView);
            cardView = (CardView) itemView;
            clubImage = (ImageView) itemView.findViewById(R.id.item_image);
            clubName = (TextView) itemView.findViewById(R.id.item_text);

        }
    }

}

