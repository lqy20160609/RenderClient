package com.practicaltraining.render.fragments;

import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.practicaltraining.render.R;
import com.practicaltraining.render.adapters.ModelRecyclerViewAdapter;
import com.practicaltraining.render.objects.ModelItem;

import java.util.ArrayList;
import java.util.List;

public class ModelsFragment extends FatherFragment {
    private RecyclerView modelsResources;
    private List<ModelItem> modelRes=new ArrayList<>();
    private String[] modelNames;
    private int[] modelImgsId;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View rootView=inflater.inflate(R.layout.resources_fragment,container,false);
        //列表添加数据
        modelNames=new String[]{"0","1","2"};//物体的名字
        modelImgsId=new int[]{R.drawable.ic_launcher_background};//物体缩略图
        for(int i=0;i<modelNames.length;i++){
            modelRes.add(new ModelItem(modelNames[i],modelImgsId[0]));
        }
        //设置recyclerlist参数
        modelsResources=(RecyclerView)rootView.findViewById(R.id.resources);
        modelsResources.setHasFixedSize(true);
        GridLayoutManager reslayout=new GridLayoutManager(rootView.getContext(),3,OrientationHelper.VERTICAL,false);
        modelsResources.setLayoutManager(reslayout);
        ModelRecyclerViewAdapter resadap=new ModelRecyclerViewAdapter(modelRes);
        modelsResources.setAdapter(resadap);

        return rootView;
    }
}
