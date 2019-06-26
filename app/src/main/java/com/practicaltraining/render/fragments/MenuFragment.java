package com.practicaltraining.render.fragments;

/**
 * created By LQY
 * 2019.6.22
 * 弹出菜单fragment 用于挂载各种功能实现
 */
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.practicaltraining.render.R;
import com.practicaltraining.render.core.FragmentSwitchManager;

public class MenuFragment extends FatherFragment {
    private Button testButton;
    private Button tangButton;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.meun_fragment, container, false);
        testButton = rootView.findViewById(R.id.testButton);
        tangButton = rootView.findViewById(R.id.tang);
        testButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //SocketIOManager.getInstance().getNewScence();
                //SocketIOManager.getInstance().finishcallback.getDataCompleted("111");
                FragmentSwitchManager.getInstance().switchToNextFragmentByTag(getActivity().getSupportFragmentManager(),
                        MenuFragment.class.getName(),SettingFragment.class.getName());
                changeCurrentFragment.changeCurrentFragment(SettingFragment.class.getName());
            }
        });

        tangButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentSwitchManager.getInstance().switchToNextFragmentByTag(getActivity().getSupportFragmentManager(),
                        MenuFragment.class.getName(),ModelsFragment.class.getName());
                changeCurrentFragment.changeCurrentFragment(ModelsFragment.class.getName());
            }
        });

        return rootView;
    }
}
