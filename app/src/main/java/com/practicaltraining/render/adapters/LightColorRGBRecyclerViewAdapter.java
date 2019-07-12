package com.practicaltraining.render.adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.TextView;

import com.practicaltraining.render.R;
import com.practicaltraining.render.callbacks.ChangeLightColor;
import com.practicaltraining.render.objects.LightColorRGBItem;

import java.util.List;

public class LightColorRGBRecyclerViewAdapter extends RecyclerView.Adapter<LightColorRGBRecyclerViewAdapter.VH> {
    private static ChangeLightColor changeLightColor;
    private List<LightColorRGBItem> mData;//Adapter接收list作为参数

    public LightColorRGBRecyclerViewAdapter(List<LightColorRGBItem> mData){
        this.mData =mData;
    }
    public static void onChangeLightColor(ChangeLightColor LightColor){
        changeLightColor =LightColor;
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull final ViewGroup parent, int viewType) {
        //创建组件
        final View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.light_color_rgb, parent, false);

        return new VH(v);
    }
    @Override
    public void onBindViewHolder(@NonNull VH holder, final int position) {

        holder.textView.setText(mData.get(position).getRGB());
        holder.seekBar.setProgress(mData.get(position).getProgress());
        holder.showView.setText(String.valueOf(mData.get(position).getProgress()));
        holder.seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                holder.showView.setText(String.valueOf(progress));
                mData.get(position).setProgress(progress);
                changeLightColor.setChangeLightColor(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }


    public static class VH extends RecyclerView.ViewHolder{

        TextView textView;
        TextView showView;
        SeekBar seekBar;

        public VH(View v) {

            super(v);
            textView=itemView.findViewById(R.id.light_color_rgb_title);
            showView = itemView.findViewById(R.id.light_color_rgb_show);
            seekBar = itemView.findViewById(R.id.light_color_rgb_bar);

        }
    }
}
