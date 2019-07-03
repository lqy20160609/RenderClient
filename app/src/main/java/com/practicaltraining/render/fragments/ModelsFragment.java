package com.practicaltraining.render.fragments;

import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;


import com.alibaba.fastjson.JSONObject;
import com.practicaltraining.render.R;
import com.practicaltraining.render.TreeView.holder.IconTreeItemHolder;
import com.practicaltraining.render.TreeView.model.TreeNode;
import com.practicaltraining.render.adapters.ModelRecyclerViewAdapter;
import com.practicaltraining.render.core.FragmentSwitchManager;
import com.practicaltraining.render.core.SocketIOManager;
import com.practicaltraining.render.objects.ModelItem;
import com.practicaltraining.render.utils.StaticVar;


import java.util.ArrayList;
import java.util.List;

public class ModelsFragment extends FatherFragment {
    private static String TAG = "ModelsFragment";
    private RecyclerView modelsResources;
    private List<ModelItem> modelRes = new ArrayList<>();
    private String[] modelNames;
    private int[] modelImgsId;
    public static int meshCount = 0;
    public static String meshName = "";

    @Override
    public Animation onCreateAnimation(int transit, boolean enter, int nextAnim) {
        return super.onCreateAnimation(transit, enter, nextAnim);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.resources_fragment, container, false);
        //列表添加数据
        modelNames = new String[]{"0", "1", "2"};//物体的名字
        modelImgsId = new int[]{R.drawable.ic_launcher_background};//物体缩略图
        for (String modelName : modelNames) {
            modelRes.add(new ModelItem(modelName, modelImgsId[0]));
        }
        //设置recyclerlist参数
        modelsResources = rootView.findViewById(R.id.resources);
        modelsResources.setHasFixedSize(true);
        GridLayoutManager reslayout = new GridLayoutManager(rootView.getContext(), 3,
                OrientationHelper.VERTICAL, false);
        reslayout.canScrollVertically();
        modelsResources.setLayoutManager(reslayout);
        ModelRecyclerViewAdapter resadap = new ModelRecyclerViewAdapter(modelRes);
        modelsResources.setAdapter(resadap);
        resadap.setOnSettingItemClickListener(position -> {
            meshCount++;
            meshName = resadap.getModelItem(position).getName();
            changeCurrentFragment.changeCurrentFragment(TreeFragment.class.getName());
            FragmentSwitchManager.getInstance().switchToPreFragmentByTag(getActivity().getSupportFragmentManager(),
                    ModelsFragment.class.getName(),TreeFragment.class.getName());
        });


        return rootView;
    }
}
