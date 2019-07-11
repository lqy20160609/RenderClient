package com.practicaltraining.render.fragments;

import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;


import com.alibaba.fastjson.JSONObject;
import com.practicaltraining.render.R;
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
    private String[] meshNames;
    private int[] modelImgsId;
    private int positionClick = -1;
    public static int meshCount = 0;
    public static String meshName = "";
    Button modelSubmitButton;

    @Override
    public Animation onCreateAnimation(int transit, boolean enter, int nextAnim) {
        return super.onCreateAnimation(transit, enter, nextAnim);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.resources_fragment, container, false);
        //列表添加数据
        modelSubmitButton = rootView.findViewById(R.id.modelSubmitButton);

        modelNames = new String[]{"0", "1", "2","3","4","5"};//物体的编号
        meshNames = new String[]{"牛", "大脑", "兔子","狗头人","头","lucy"};
        modelImgsId = new int[]{R.drawable.cow,R.drawable.brain,R.drawable.bunny,R.drawable.goutouren,R.drawable.head,R.drawable.lucy};//物体缩略图
        for (int i =0;i<6;i++){
            modelRes.add(new ModelItem(modelNames[i], modelImgsId[i],meshNames[i]));
        }
        //设置recyclerlist参数
        modelsResources = rootView.findViewById(R.id.resources);
        modelsResources.setHasFixedSize(true);
        LinearLayoutManager reslayout = new LinearLayoutManager(rootView.getContext(),
                OrientationHelper.VERTICAL, false);
        reslayout.canScrollVertically();
        modelsResources.setLayoutManager(reslayout);
        ModelRecyclerViewAdapter resadap = new ModelRecyclerViewAdapter(modelRes);
        modelsResources.setAdapter(resadap);
        resadap.setOnSettingItemClickListener(position -> {
            positionClick = position;
        });
        modelSubmitButton.setOnClickListener(view -> {
            if (positionClick == -1) {
                Toast.makeText(getContext(), "请先选择一个模型", Toast.LENGTH_SHORT).show();
            } else {
                meshCount++;
                meshName = resadap.getModelItem(positionClick).getName();
                Toast.makeText(getActivity(),meshName,Toast.LENGTH_LONG).show();
                changeCurrentFragment.changeCurrentFragment(TreeFragment.class.getName());
                FragmentSwitchManager.getInstance().switchToPreFragmentByTag(getActivity().getSupportFragmentManager(),
                        ModelsFragment.class.getName(), TreeFragment.class.getName());
            }
        });
        return rootView;
    }
}
