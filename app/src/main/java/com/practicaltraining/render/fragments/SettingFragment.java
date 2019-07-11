package com.practicaltraining.render.fragments;

import android.app.AlertDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.practicaltraining.render.R;
import com.practicaltraining.render.adapters.SettingRecyclerViewAdapter;
import com.practicaltraining.render.core.SocketIOManager;
import com.practicaltraining.render.objects.SettingItem;
import com.practicaltraining.render.utils.StaticVar;

import java.util.ArrayList;
import java.util.List;

public class SettingFragment extends FatherFragment {
    private RecyclerView recyclerView;
    private SettingRecyclerViewAdapter mAdapter;
    private List<SettingItem> data = new ArrayList<>();
    @Override
    public Animation onCreateAnimation(int transit, boolean enter, int nextAnim) {
        return super.onCreateAnimation(transit, enter, nextAnim);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.setting_fragment, container, false);
        recyclerView=rootView.findViewById(R.id.setting_recycler_view);
        initData();
        mAdapter = new SettingRecyclerViewAdapter();
        mAdapter.initData(data);
        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getContext(), 3, OrientationHelper.VERTICAL, false);
        mLayoutManager.canScrollVertically();
        recyclerView.setLayoutManager(mLayoutManager);

        mAdapter.setOnSettingItemClickListener(position -> {

            Toast.makeText(getContext(),"点击了"+mAdapter.getItem(position).getDescription(),
                    Toast.LENGTH_LONG).show();
            switch (position){
                case 0:
//                    FragmentSwitchManager.getInstance().switchToNextFragmentByTag(getActivity().getSupportFragmentManager(),
//                            SettingFragment.class.getName(),LightFragment.class.getName());
//                    changeCurrentFragment.changeCurrentFragment(LightFragment.class.getName());
                    AlertDialog.Builder lightBuilder=new AlertDialog.Builder(getContext());
                    LightFragment lightColorItem=new LightFragment();
                    lightBuilder.setView(lightColorItem.onCreateView(inflater,container,savedInstanceState));
                    lightBuilder.setPositiveButton("确定",((dialogInterface, i) -> {
                        // 发送网络请求

                    }));
                    lightBuilder.setNegativeButton("取消",((dialogInterface, i) -> {

                    }));
                    lightBuilder.create().show();
                    break;
                case 1:
                    //FragmentSwitchManager.getInstance().switchToNextFragmentByTag(getActivity().getSupportFragmentManager(),
                    //SettingFragment.class.getName(),MaterialFragment.class.getName());
                    //changeCurrentFragment.changeCurrentFragment(MaterialFragment.class.getName());
                    AlertDialog.Builder matBuilder=new AlertDialog.Builder(getContext());
                    matBuilder.setTitle("选择材质");
                    MaterialFragment materialFragment=new MaterialFragment();
                    matBuilder.setView(materialFragment.onCreateView(inflater,container,savedInstanceState));
                    matBuilder.setPositiveButton("确定",((dialogInterface, i) -> {
                        // 发送网络请求
                    }));
                    matBuilder.setNegativeButton("取消",((dialogInterface, i) -> {}));
                    matBuilder.create().show();
                    break;
                case 2:
                    AlertDialog.Builder colorBuilder=new AlertDialog.Builder(getContext());
                    colorBuilder.setTitle("更改颜色");
                    ColorFragment colorFragment=new ColorFragment();
                    colorBuilder.setView(colorFragment.onCreateView(inflater,container,savedInstanceState));
                    colorBuilder.setPositiveButton("确定",((dialogInterface, i) -> {
                        // 发送网络请求
                        JSONObject json = new JSONObject();
                        json.put("operation_type",15);
                        json.put("R",StaticVar.colorR);
                        json.put("G",StaticVar.colorG);
                        json.put("B",StaticVar.colorB);
                        json.put("groupId",StaticVar.currentItemId);
                        SocketIOManager.getInstance().getNewModelScence(json);
                    }));
                    colorBuilder.setNegativeButton("取消",((dialogInterface, i) -> {

                    }));
                    colorBuilder.create().show();
                    break;
                /*case 3:
                    break;*/
                    default:
                        break;

            }
        });

        recyclerView.setAdapter(mAdapter);

        return rootView;
    }

    private void initData(){
        data.add(new SettingItem(R.drawable.light,"更改光照"));
        data.add(new SettingItem(R.drawable.texture,"更改纹理"));
        data.add(new SettingItem(R.drawable.color,"更改颜色"));
        //data.add(new SettingItem(R.drawable.addmodel,"添加模型"));

    }
}
