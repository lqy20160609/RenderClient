package com.practicaltraining.render.fragments;

import android.app.AlertDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.Toast;

import com.practicaltraining.render.R;
import com.practicaltraining.render.adapters.SettingRecyclerViewAdapter;
import com.practicaltraining.render.callbacks.OnSettingItemClickListener;
import com.practicaltraining.render.core.FragmentSwitchManager;
import com.practicaltraining.render.objects.SettingItem;

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
        //RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getContext(), 3, OrientationHelper.VERTICAL, false);
        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getContext(), 1, OrientationHelper.VERTICAL, false);
        mLayoutManager.canScrollVertically();
        recyclerView.setLayoutManager(mLayoutManager);

        mAdapter.setOnSettingItemClickListener(new OnSettingItemClickListener() {
            @Override
            public void onSettingItemClick(int position) {
                Toast.makeText(getContext(),"点击了"+mAdapter.getItem(position).getDescription(),
                        Toast.LENGTH_LONG).show();
                if(position==2){
                    //FragmentSwitchManager.getInstance().switchToNextFragmentByTag(getActivity().getSupportFragmentManager(),
                            //SettingFragment.class.getName(),ColorFragment.class.getName());
                    //changeCurrentFragment.changeCurrentFragment(ColorFragment.class.getName());
                    AlertDialog.Builder colorBuilder=new AlertDialog.Builder(getContext());
                    ColorFragment colorFragment=new ColorFragment();
                    colorBuilder.setView(colorFragment.onCreateView(inflater,container,savedInstanceState));
                    colorBuilder.setPositiveButton("OK",((dialogInterface, i) -> {}));
                    colorBuilder.setNegativeButton("Cancel",((dialogInterface, i) -> {}));
                    colorBuilder.create().show();
                }
                if(position==3){
                    FragmentSwitchManager.getInstance().switchToNextFragmentByTag(getActivity().getSupportFragmentManager(),
                            SettingFragment.class.getName(),ModelStackFrag.class.getName());
                    changeCurrentFragment.changeCurrentFragment(ModelStackFrag.class.getName());
                }
            }

        });

        recyclerView.setAdapter(mAdapter);

        return rootView;
    }

    private void initData(){
        data.add(new SettingItem(R.drawable.light,"更改光照"));
        data.add(new SettingItem(R.drawable.texture,"更改纹理"));
        data.add(new SettingItem(R.drawable.color,"更改颜色"));
        data.add(new SettingItem(R.drawable.addmodel,"添加模型"));

    }
}
