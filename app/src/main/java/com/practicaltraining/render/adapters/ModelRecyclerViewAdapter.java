package com.practicaltraining.render.adapters;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.practicaltraining.render.MainActivity;
import com.practicaltraining.render.R;
import com.practicaltraining.render.callbacks.OnSettingItemClickListener;
import com.practicaltraining.render.objects.ModelItem;

import java.util.List;

public class ModelRecyclerViewAdapter extends RecyclerView.Adapter<ModelRecyclerViewAdapter.VH> {
    public static class VH extends RecyclerView.ViewHolder{
        TextView name;
        ImageView icon;
        public VH(View v) {
            //this is a view holder
            super(v);
            this.name=itemView.findViewById(R.id.names);
            this.icon=itemView.findViewById(R.id.imgs);
        }
    }
    private List<ModelItem> mData;//Adapter接收list作为参数
    public ModelRecyclerViewAdapter(List<ModelItem>lis){
        this.mData =lis;
    }
    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull final ViewGroup parent, int viewType) {
        //创建组件
        final View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.resourcesrecyclerview_item, parent, false);

        return new VH(v);
    }
    private OnSettingItemClickListener onSettingItemClickListener;
    public void setOnSettingItemClickListener(OnSettingItemClickListener onSettingItemClickListener) {
        this.onSettingItemClickListener = onSettingItemClickListener;
    }
    @Override
    public void onBindViewHolder(@NonNull VH holder, final int position) {
        //传递数据
        holder.name.setText(mData.get(position).getName());
        holder.icon.setImageResource(mData.get(position).getId());
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

    public ModelItem getModelItem(int position){
        return mData.get(position)!=null?mData.get(position):null;
    }
}
