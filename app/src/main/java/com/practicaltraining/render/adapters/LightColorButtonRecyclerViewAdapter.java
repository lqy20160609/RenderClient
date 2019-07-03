package com.practicaltraining.render.adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import com.practicaltraining.render.MyWidget.CircleColorButton;
import com.practicaltraining.render.R;
import com.practicaltraining.render.callbacks.ChangeLightColor;
import com.practicaltraining.render.callbacks.LightButtonColor;
import com.practicaltraining.render.callbacks.OnSettingItemClickListener;
import com.practicaltraining.render.objects.LightColorButtonItem;

import java.util.ArrayList;
import java.util.List;

public class LightColorButtonRecyclerViewAdapter extends RecyclerView.Adapter<LightColorButtonRecyclerViewAdapter.VH> {

    private List<LightColorButtonItem> mData;
    public static LightButtonColor changeLightColor;

    public LightColorButtonRecyclerViewAdapter(List<LightColorButtonItem> mData){
        this.mData =mData;
    }
    public static void onChangeLightColor(LightButtonColor LightColor){
        changeLightColor =LightColor;
    }
    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull final ViewGroup parent, int viewType) {
        //创建组件
        final View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.light_color_button, parent, false);

        return new VH(v);
    }
    @Override
    public void onBindViewHolder(@NonNull VH holder, final int position) {
        //传递数据
        holder.circleColorButton.setChecked(mData.get(position).isChecked());
        holder.circleColorButton.setColor(mData.get(position).getColor());
        holder.itemView.setOnClickListener(view -> {
            changeLightColor.setChangeLightColorButton(holder.circleColorButton.getColor());
        });
    }


    @Override
    public int getItemCount() {

        return mData.size();
    }

//    public LightColorButtonItem getLightColorItem(int position){
//        return mData.get(position)!=null?mData.get(position):null;
//    }

    public static class VH extends RecyclerView.ViewHolder{

        CircleColorButton  circleColorButton;
        public VH(View v) {

            super(v);
            circleColorButton=itemView.findViewById(R.id.button_color_light);

        }
    }
}
