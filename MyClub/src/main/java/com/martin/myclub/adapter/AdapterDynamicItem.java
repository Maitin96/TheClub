package com.martin.myclub.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.martin.myclub.R;
import com.martin.myclub.bean.DynamicMsg;
import com.martin.myclub.bean.MyUser;

import java.util.List;

/**
 * Created by Martin on 2017/7/10.
 * Dynamic 列表页面的适配器
 */
public class AdapterDynamicItem extends RecyclerView.Adapter<AdapterDynamicItem.BaseViewHolder>{

    private Context context;
    private List<DynamicMsg> dynamicMsgList;

    public AdapterDynamicItem(Context context) {
        this.context = context;
    }

    public void setData(List<DynamicMsg> list){
        this.dynamicMsgList = list;
    }

    @Override
    public AdapterDynamicItem.BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new BaseViewHolder(LayoutInflater.from(context).inflate(R.layout.item_dynamic,parent,false));
    }

    @Override
    public void onBindViewHolder(AdapterDynamicItem.BaseViewHolder holder, int position) {
//        holder.headpic.setImageResource(dynamicMsgList.get(position).getHeadPic());
        holder.username.setText(dynamicMsgList.get(position).getUser().getUsername());
        holder.time.setText(dynamicMsgList.get(position).getCreatedAt());
        holder.content.setText(dynamicMsgList.get(position).getContent());
        holder.picture.setImageResource(dynamicMsgList.get(position).getPicture());
        holder.tv_comment.setText(dynamicMsgList.get(position).getComment_counts() + "");

        holder.tv_looked_time.setText("浏览" + dynamicMsgList.get(position).getLook_times() + "次");

//        List<MyUser> like_users = dynamicMsgList.get(position).getLike_users();
//        if(like_users != null){
//            StringBuffer stringBuffer = new StringBuffer();
//            for (MyUser user : like_users){
//                stringBuffer.append("、" + user.getUsername());
//            }
//            if(stringBuffer.length() > 0){
//                String text = stringBuffer.toString().substring(1,stringBuffer.length()) + "等" + like_users.size() + "人觉得很赞!";
//                holder.tv_person_like.setText(text);
//            }
//        }

//        holder.tv_person_like
    }

    @Override
    public int getItemCount() {
        return dynamicMsgList == null? 0 : dynamicMsgList.size();
    }

    class BaseViewHolder extends RecyclerView.ViewHolder {

        private ImageView headpic;    //Todo  图片暂未写
        private TextView username;
        private TextView time;
        private TextView content;
        private ImageView picture;
//        private ImageView iv_collect;  //收藏
        private TextView tv_comment;  //评论数
        private TextView tv_looked_time; //浏览次数
        private TextView tv_person_like; //Edward 觉得很赞~~~~

        public BaseViewHolder(View itemView) {
            super(itemView);
            headpic = (ImageView) itemView.findViewById(R.id.dy_hp);
            username = (TextView) itemView.findViewById(R.id.dy_name);
            time = (TextView) itemView.findViewById(R.id.dy_time);
            content = (TextView) itemView.findViewById(R.id.dy_content);
            picture = (ImageView) itemView.findViewById(R.id.dy_content_image);

//            iv_collect = (ImageView) itemView.findViewById(R.id.iv_collect);
            tv_comment = (TextView) itemView.findViewById(R.id.tv_comment);
            tv_person_like = (TextView) itemView.findViewById(R.id.tv_person_like);
            tv_looked_time = (TextView) itemView.findViewById(R.id.tv_looked_time);
        }
    }
}
