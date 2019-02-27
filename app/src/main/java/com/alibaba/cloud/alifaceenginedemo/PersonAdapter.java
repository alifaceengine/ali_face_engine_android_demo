package com.alibaba.cloud.alifaceenginedemo;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.cloud.faceengine.Person;

public class PersonAdapter extends RecyclerView.Adapter<PersonAdapter.ViewHolder> {
    private static final String TAG = "AFE_PersonAdapter";

    private Context context;
    private Person[] datas;

    public PersonAdapter(Context context, Person[] datas) {
        this.context = context;
        this.datas = datas;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_ry_facelibrary, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.name.setText(datas[position].name);
        if (datas[position].features == null) {
            holder.Feature_amount.setText("0 Feature");
        } else {
            holder.Feature_amount.setText(datas[position].features.length + " Feature");
        }
    }

    @Override
    public int getItemCount() {
        if (datas == null) {
            return 0;
        }
        return datas.length;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView name;
        private TextView Feature_amount;
        private ImageView iv;

        public ViewHolder(View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.item_ry_facelibrary_tv1);
            Feature_amount = (TextView) itemView.findViewById(R.id.item_ry_facelibrary_tv2);
            iv = (ImageView) itemView.findViewById(R.id.item_ry_facelibrary_iv);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onItemClickListener != null) {
                        onItemClickListener.onItemClick(v, datas[getLayoutPosition()]);
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

    public interface onItemClickListener {
        public void onItemClick(View view, Person data);
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
