package com.practicaltraining.render.fragments;

import android.graphics.Color;
import android.graphics.ColorFilter;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.practicaltraining.render.R;
import com.practicaltraining.render.utils.StaticVar;

public class ColorFragment extends FatherFragment{
    ImageView currentImg;
    SeekBar color_r;
    SeekBar color_g;
    SeekBar color_b;
    TextView text_r;
    TextView text_g;
    TextView text_b;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView=inflater.inflate(R.layout.pattle_frag,container,false);

        currentImg=rootView.findViewById(R.id.colorView);

        color_r=rootView.findViewById(R.id.seekBar_r);
        color_g=rootView.findViewById(R.id.seekBar_g);
        color_b=rootView.findViewById(R.id.seekBar_b);

        text_r=rootView.findViewById(R.id.Text_r);
        text_g=rootView.findViewById(R.id.Text_g);
        text_b=rootView.findViewById(R.id.Text_b);
        //获取模型的颜色（from远程
        //把模型颜色传递给progress（推荐rgb分开，取值0-255
        //把progress值传递给imgview↓
        currentImg.setColorFilter(Color.rgb(color_r.getProgress(),color_g.getProgress(),color_b.getProgress()));//0x000000,int类型
        //color_r.setProgress(Integer.parseInt(text_r.getText().toString()));//文本到进度条
        color_r.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                //b=true,表示用户使seekbar数值变化,否则是seekbar自己在变化
                currentImg.setColorFilter(Color.rgb(i,color_g.getProgress(),color_b.getProgress()));
                text_r.setText(String.valueOf(i));
                StaticVar.colorR = i;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        color_g.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                currentImg.setColorFilter(Color.rgb(color_r.getProgress(),i,color_b.getProgress()));
                text_g.setText(String.valueOf(i));
                StaticVar.colorG = i;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        color_b.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                currentImg.setColorFilter(Color.rgb(color_r.getProgress(),color_g.getProgress(),i));
                text_b.setText(String.valueOf(i));
                StaticVar.colorB = i;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        return rootView;
    }

}
