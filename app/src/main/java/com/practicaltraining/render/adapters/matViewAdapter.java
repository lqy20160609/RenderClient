package com.practicaltraining.render.adapters;

import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.practicaltraining.render.R;
import com.practicaltraining.render.callbacks.OnSettingItemClickListener;
import com.practicaltraining.render.objects.MatItem;
import com.practicaltraining.render.objects.ModelItem;

import java.util.ArrayList;
import java.util.List;

public class matViewAdapter extends RecyclerView.Adapter<matViewAdapter.VH>  {
    private OnSettingItemClickListener onSettingItemClickListener;
    private List<Boolean> isClicked;

    public void setOnSettingItemClickListener(OnSettingItemClickListener onSettingItemClickListener) {
        this.onSettingItemClickListener = onSettingItemClickListener;
    }
    public static class VH extends RecyclerView.ViewHolder{
        ImageView icon;
        TextView name;
        ConstraintLayout background;

        public VH(View v) {
            //this is a view holder
            super(v);
            this.icon=itemView.findViewById(R.id.matimg);
            this.name=itemView.findViewById(R.id.matname);
            this.background=itemView.findViewById(R.id.matbackground);
        }
    }
    private List<MatItem> mData;//Adapter接收list作为参数
    public matViewAdapter(List<MatItem>lis){
        this.mData =lis;
        isClicked=new ArrayList<>();
        for(int i=0;i<lis.size();i++){
            isClicked.add(false);
        }
    }
    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        final View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.material_frag_single, parent, false);
        return new matViewAdapter.VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        holder.name.setText(mData.get(position).getName());
        holder.icon.setImageResource(mData.get(position).getId());
        holder.background.setOnClickListener(v ->{
            //onSettingItemClickListener.onSettingItemClick(position);
            for(int i=0;i<isClicked.size();i++){
                isClicked.set(i,false);
            }
            isClicked.set(position,true);
            notifyDataSetChanged();
        });
        if(isClicked.get(position)){
            holder.background.setBackgroundColor(Color.parseColor("#D8E2ED"));
        }
        else{
            holder.background.setBackgroundColor(Color.parseColor("#FFFFFF"));
        }
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }
}
