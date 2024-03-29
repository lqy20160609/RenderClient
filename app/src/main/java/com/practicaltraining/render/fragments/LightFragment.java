package com.practicaltraining.render.fragments;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.practicaltraining.render.R;
import com.practicaltraining.render.adapters.LightColorButtonRecyclerViewAdapter;
import com.practicaltraining.render.adapters.LightColorRGBRecyclerViewAdapter;
import com.practicaltraining.render.objects.LightColorButtonItem;
import com.practicaltraining.render.objects.LightColorRGBItem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class LightFragment extends FatherFragment {
    private List<LightColorButtonItem> mData = new ArrayList<>();
    private List<LightColorRGBItem> mData_rgb = new ArrayList<>();
    private TextView getLight_intensity_show;
    private ImageView imageView;
    private int light_color;
    private int light_intensity;
    private LightColorButtonRecyclerViewAdapter lCBAdapter;
    private LightColorRGBRecyclerViewAdapter lightColorRGBRecyclerViewAdapter;
    //获取当前光照颜色和亮度
    public int getLight_color() {
        return light_color;
    }

    public int getLight_intensity() {
        return light_intensity;
    }

    //设置亮度和光照颜色

    public void setLight_intensity(int light_intensity) {
        this.light_intensity = light_intensity;
    }

    public void setLight_color(int light_color) {
        this.light_color = light_color;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.setting_light, container, false);
        lCBAdapter = new LightColorButtonRecyclerViewAdapter(mData);
        lightColorRGBRecyclerViewAdapter = new LightColorRGBRecyclerViewAdapter(mData_rgb);
        RecyclerView recyclerView;
        RecyclerView recyclerView_rgb;
        SeekBar seekBar_intensity;

        //绑定
        getLight_intensity_show = rootView.findViewById(R.id.light_intensity_show);
        imageView = rootView.findViewById(R.id.light_color_show);
        seekBar_intensity = rootView.findViewById(R.id.light_intensity_bar);
        recyclerView = rootView.findViewById(R.id.container_button_color);
        recyclerView_rgb = rootView.findViewById(R.id.light_color_rgb);

        //Item初始化 mData&mData_rgb
        init();
        //添加manager&adapter
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 5);
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.setAdapter(lCBAdapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView_rgb.setLayoutManager(linearLayoutManager);
        recyclerView_rgb.setAdapter(lightColorRGBRecyclerViewAdapter);

        //设置两个recyclerview的Item间距
        setItemSpace(recyclerView, 30, 30, 30, 0);
        setItemSpace(recyclerView_rgb, 30, 0, 0, 0);

        //颜色随button或者rgb动态变化，并保存color数据

        LightColorRGBRecyclerViewAdapter.onChangeLightColor((int progress) -> {
            //更新rgb bar，在rgb中完成对imageView的更新
            imageView.setImageTintList(ColorStateList.valueOf(Color.rgb(mData_rgb.get(0).getProgress(), mData_rgb.get(1).getProgress(), mData_rgb.get(2).getProgress())));
            this.setLight_color(Color.rgb(mData_rgb.get(0).getProgress(), mData_rgb.get(1).getProgress(), mData_rgb.get(2).getProgress()));

        });
        //button与rgb同步
        LightColorButtonRecyclerViewAdapter.onChangeLightColor((int color) -> {
            //点击button后， 传递button color 更新rgb bar，然后在rgb中完成对imageView的更新
            setRGB(color);
            lightColorRGBRecyclerViewAdapter.notifyDataSetChanged();
        });
        //亮度变化
        seekBar_intensity.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                getLight_intensity_show.setText(String.valueOf(progress));
                setLight_intensity(progress);
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

    //设置Item间距
    public class RecyclerViewSpacesItemDecoration extends RecyclerView.ItemDecoration {

        private HashMap<String, Integer> mSpaceValueMap;

        private static final String TOP_DECORATION = "top_decoration";
        private static final String BOTTOM_DECORATION = "bottom_decoration";
        private static final String LEFT_DECORATION = "left_decoration";
        private static final String RIGHT_DECORATION = "right_decoration";

        RecyclerViewSpacesItemDecoration(HashMap<String, Integer> mSpaceValueMap) {
            this.mSpaceValueMap = mSpaceValueMap;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view,
                                   RecyclerView parent, RecyclerView.State state) {
            if (mSpaceValueMap.get(TOP_DECORATION) != null)
                outRect.top = mSpaceValueMap.get(TOP_DECORATION);
            if (mSpaceValueMap.get(LEFT_DECORATION) != null)

                outRect.left = mSpaceValueMap.get(LEFT_DECORATION);
            if (mSpaceValueMap.get(RIGHT_DECORATION) != null)
                outRect.right = mSpaceValueMap.get(RIGHT_DECORATION);
            if (mSpaceValueMap.get(BOTTOM_DECORATION) != null)

                outRect.bottom = mSpaceValueMap.get(BOTTOM_DECORATION);

        }

    }

    //设置两个recyclerview的Item间距
    public void setItemSpace(RecyclerView recyclerView, int top, int bottom, int left, int right) {

        HashMap<String, Integer> stringIntegerHashMap = new HashMap<>();
        stringIntegerHashMap.put(RecyclerViewSpacesItemDecoration.TOP_DECORATION, top);
        stringIntegerHashMap.put(RecyclerViewSpacesItemDecoration.BOTTOM_DECORATION, bottom);
        stringIntegerHashMap.put(RecyclerViewSpacesItemDecoration.LEFT_DECORATION, left);
        stringIntegerHashMap.put(RecyclerViewSpacesItemDecoration.RIGHT_DECORATION, right);
        recyclerView.addItemDecoration(new RecyclerViewSpacesItemDecoration(stringIntegerHashMap));

    }


    public void setRGB(int color) {

        mData_rgb.get(0).setProgress(Color.red(color));
        mData_rgb.get(1).setProgress(Color.green(color));
        mData_rgb.get(2).setProgress(Color.blue(color));
    }

    //button和rgb_edit数据初始化
    public void init() {
        mData.add(new LightColorButtonItem(Color.parseColor("#FFFF0000"), false));
        mData.add(new LightColorButtonItem(Color.parseColor("#FFFF6600"), false));
        mData.add(new LightColorButtonItem(Color.parseColor("#FFFFFF00"), false));
        mData.add(new LightColorButtonItem(Color.parseColor("#FF00CC00"), false));
        mData.add(new LightColorButtonItem(Color.parseColor("#FF669999"), false));
        mData.add(new LightColorButtonItem(Color.parseColor("#FF0066CC"), false));
        mData.add(new LightColorButtonItem(Color.parseColor("#FF990099"), false));
        mData.add(new LightColorButtonItem(Color.parseColor("#FF7C7C7C"), false));
        if (mData_rgb.size() >= 3) {
            imageView.setImageTintList(ColorStateList.valueOf(Color.rgb(mData_rgb.get(0).getProgress(), mData_rgb.get(1).getProgress(), mData_rgb.get(2).getProgress())));

        } else {
            mData_rgb.add(0, new LightColorRGBItem("R", 255));
            mData_rgb.add(1, new LightColorRGBItem("G", 255));
            mData_rgb.add(2, new LightColorRGBItem("B", 255));
            imageView.setImageTintList(ColorStateList.valueOf(Color.rgb(mData_rgb.get(0).getProgress(), mData_rgb.get(1).getProgress(), mData_rgb.get(2).getProgress())));

        }
    }
}
