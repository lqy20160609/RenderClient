package com.practicaltraining.render.fragments;

/**
 * created By LQY
 * 2019.6.22
 * 弹出菜单fragment 用于挂载各种功能实现
 */
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.practicaltraining.render.R;
import com.practicaltraining.render.callbacks.GetPhotoCompleted;
import com.practicaltraining.render.socketio.SocketIOManager;

public class MenuFragment extends Fragment {
    private Button testButton;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.meun_fragment,container,false);
        testButton = rootView.findViewById(R.id.testButton);
        SocketIOManager.getInstance().setFinishcallback(new GetPhotoCompleted() {
            @Override
            public void getDataCompleted(String data) {
                Log.d("lqyDeBug callback",data);
                Toast.makeText(getActivity(),data,Toast.LENGTH_LONG).show();
            }
        });
        testButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SocketIOManager.getInstance().getNewScence();
            }
        });
        return rootView;
    }
}
