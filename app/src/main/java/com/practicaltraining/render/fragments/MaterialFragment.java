package com.practicaltraining.render.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.practicaltraining.render.R;
import com.practicaltraining.render.adapters.matViewAdapter;
import com.practicaltraining.render.objects.MatItem;
import com.practicaltraining.render.objects.ModelItem;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class MaterialFragment extends FatherFragment {
    private String[] matNames;
    private int[] matImgsId;
    private List<MatItem>matList=new ArrayList<>();
    private RecyclerView matView;

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView=inflater.inflate(R.layout.material_frag,container,false);
        //传递数据列表
        matNames = new String[]{"a", "b", "c","d","e","f","g"};
        matImgsId = new int[]{R.drawable.mat1};
        for (int i = 0;i<matNames.length; i++) {
            matList.add(new MatItem(matNames[i],matImgsId[0]));
        }
        //设置recyclerview
        matView = rootView.findViewById(R.id.matRecyclerView);
        matView.setHasFixedSize(true);

         GridLayoutManager matLayout = new GridLayoutManager(rootView.getContext(),3,
                OrientationHelper.VERTICAL, false);
        matLayout.canScrollVertically();
        matView.setLayoutManager(matLayout);
        matViewAdapter matAdapter=new matViewAdapter(matList);
        matView.setAdapter(matAdapter);
        /*matAdapter.setOnSettingItemClickListener(position -> {
            Toast.makeText(getContext(),"hello!",Toast.LENGTH_SHORT).show();
        });*/

        return rootView;
    }
}
