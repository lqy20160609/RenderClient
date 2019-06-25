package com.practicaltraining.render.adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.practicaltraining.render.R;
import com.practicaltraining.render.callbacks.OnSettingItemClickListener;
import com.practicaltraining.render.objects.SettingItem;

import java.util.ArrayList;
import java.util.List;


public class SettingRecyclerViewAdapter extends RecyclerView.Adapter<SettingRecyclerViewAdapter.mViewHolder> {

    private List<SettingItem> mData = new ArrayList<>();
    private OnSettingItemClickListener onSettingItemClickListener;
    public void initData(List<SettingItem> data){
        this.mData = data;
        notifyDataSetChanged();
    }

    public void setOnSettingItemClickListener(OnSettingItemClickListener onSettingItemClickListener) {
        this.onSettingItemClickListener = onSettingItemClickListener;
    }

    public SettingItem getItem(int position){
        try {
            return mData.get(position);
        }catch (ArrayIndexOutOfBoundsException e){
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }

    @NonNull
    @Override
    public mViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View mView = LayoutInflater.from(parent.getContext()).inflate(R.layout.settingrecyclerviewitem,parent,false);
        return new mViewHolder(mView);
    }

    @Override
    public void onBindViewHolder(mViewHolder holder, final int position) {
        holder.icon.setImageResource(mData.get(position).getIconId());
        holder.description.setText(mData.get(position).getDescription());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onSettingItemClickListener.onSettingItemClick(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }



    public class mViewHolder extends RecyclerView.ViewHolder{
        ImageView icon;
        TextView description;
        public mViewHolder(View itemView) {
            super(itemView);
            icon = itemView.findViewById(R.id.setting_item_icon);
            description = itemView.findViewById(R.id.setting_item_description);
        }
    }
}
