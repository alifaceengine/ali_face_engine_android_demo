package com.alibaba.cloud.alifaceenginedemo;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

public class GroupAdapter extends RecyclerView.Adapter<GroupAdapter.ViewHolder> {
    public static class Group {
        public com.alibaba.cloud.faceengine.Group group;
        public int personNum;
    }

    private Context mContext;
    private List<Group> mGroups;

    public GroupAdapter(Context context, List<Group> datas) {
        this.mContext = context;
        this.mGroups = datas;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_ry_facelibrary, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.name.setText(mGroups.get(position).group.name);
        holder.amount.setText(mGroups.get(position).personNum + mContext.getString(R.string.face_library_peolpe));
    }

    @Override
    public int getItemCount() {
        if (mGroups == null) {
            return 0;
        }
        return mGroups.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView name;
        private TextView amount;

        public ViewHolder(View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.item_ry_facelibrary_tv1);
            amount = (TextView) itemView.findViewById(R.id.item_ry_facelibrary_tv2);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onItemClickListener != null) {
                        onItemClickListener.onItemClick(v, mGroups.get(getLayoutPosition()));
                    }
                }
            });
            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    if (onItemLongClickListener != null) {
                        onItemLongClickListener.OnItemLongClick(v, getLayoutPosition());
                    }
                    return true;
                }
            });
        }
    }

    /**
     * 点击事件
     */
    public interface onItemClickListener {
        /**
         * @param view 点击的视图
         * @param data 点击得到的数据
         */
        public void onItemClick(View view, Group data);
    }

    public onItemClickListener onItemClickListener;

    public void setOnItemClickListener(onItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    interface OnItemLongClickListener {
        void OnItemLongClick(View view, int position);
    }

    public OnItemLongClickListener onItemLongClickListener;

    public void setOnItemLongClickListener(OnItemLongClickListener listener) {
        onItemLongClickListener = listener;
    }
}
