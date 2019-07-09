package com.practicaltraining.render.fragments;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.alibaba.fastjson.JSONObject;
import com.practicaltraining.render.R;
import com.practicaltraining.render.core.SocketIOManager;
import com.practicaltraining.render.utils.BitmapUtils;
import com.practicaltraining.render.utils.StaticVar;
import com.practicaltraining.render.views.LongClickButton;
import com.practicaltraining.render.views.MySurfaceView;

import java.io.IOException;

public class RoamingFragment extends Fragment {
    private static MySurfaceView img;
    private static Bitmap bitmap;
    private int imgWidth, imgHeight;
    private float preX = -1, preY = -1;
    private long startTime, endTime;
    private LongClickButton forward, backward, left, right;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.user_walking, container, false);
        initView(rootView);
        // 设置获取漫游图片回调
        SocketIOManager.getInstance().setRoamingPhotoCompleted(data -> {
            RoamingFragment.GetBitmapTask mTask = new RoamingFragment.GetBitmapTask(StaticVar.picServerAddress + data + ".jpg");
            mTask.execute();
        });
        // 向服务器发送屏幕宽高
        img.post(() -> {
            imgWidth = img.getWidth();
            imgHeight = img.getHeight();
            JSONObject json = new JSONObject();
            json.put("operation_type", 12);
            json.put("viewWidth", imgWidth);
            json.put("viewHeight", imgHeight);
            SocketIOManager.getInstance().sendParam(json);
        });
        initListenr();
        return rootView;
    }

    public void initView(View rootView) {
        img = rootView.findViewById(R.id.testImage);
        forward = rootView.findViewById(R.id.Roaming_up);
        backward = rootView.findViewById(R.id.Roaming_down);
        left = rootView.findViewById(R.id.Roaming_left);
        right = rootView.findViewById(R.id.Roaming_right);
    }


    @SuppressLint("ClickableViewAccessibility")
    public void initListenr() {
        //接收触控信息
        img.setOnTouchListener(new View.OnTouchListener() {
            int mode = 0;

            @Override
            public boolean onTouch(View view, MotionEvent event) {

                switch (event.getAction() & MotionEvent.ACTION_MASK) {
                    case MotionEvent.ACTION_DOWN:
                        mode = 1;
                        preX = event.getX();
                        preY = event.getY();
                        startTime = System.currentTimeMillis();
                        break;
                    case MotionEvent.ACTION_UP:
                        mode = 0;
                    case MotionEvent.ACTION_MOVE:
                        if (mode == 1) {
                            endTime = System.currentTimeMillis();
                            if (endTime - startTime >= 50) {
                                float currentX = event.getX();
                                float currentY = event.getY();
                                startTime = System.currentTimeMillis();
                                JSONObject jsonObject = new JSONObject();
                                jsonObject.put("operation_type", 24);
                                jsonObject.put("preX", preX);
                                jsonObject.put("preY", preY);
                                jsonObject.put("currentX", currentX);
                                jsonObject.put("currentY", currentY);
                                SocketIOManager.getInstance().getNewModelScence(jsonObject);
                                preX = currentX;
                                preY = currentY;
                            }
                        }
                        break;
                    default:
                        break;
                }
                return true;
            }
        });
        forward.setOnTouchListener((view, motionEvent) -> false);
        backward.setOnTouchListener((view, motionEvent) -> false);
        left.setOnTouchListener((view, motionEvent) -> false);
        right.setOnTouchListener((view, motionEvent) -> false);
        forward.setLongClickCallBack(() -> {
            JSONObject json = new JSONObject();
            json.put("operation_type",20);
            SocketIOManager.getInstance().getNewRoamingScence(json);
        });
        backward.setLongClickCallBack(() -> {
            JSONObject json = new JSONObject();
            json.put("operation_type",21);
            SocketIOManager.getInstance().getNewRoamingScence(json);
        });
        left.setLongClickCallBack(() -> {
            JSONObject json = new JSONObject();
            json.put("operation_type",23);
            SocketIOManager.getInstance().getNewRoamingScence(json);
        });
        right.setLongClickCallBack(() -> {
            JSONObject json = new JSONObject();
            json.put("operation_type",22);
            SocketIOManager.getInstance().getNewRoamingScence(json);
        });

    }

    private static class GetBitmapTask extends AsyncTask {
        private String imageAddress;

        public GetBitmapTask(String imageAddress) {
            this.imageAddress = imageAddress;
        }

        @Override
        protected Object doInBackground(Object[] params) {
            try {
                bitmap = BitmapUtils.getBitmapFromInternet("http://" + imageAddress);
                return 1;
            } catch (IOException e) {
                e.printStackTrace();
                return 0;
            }
        }

        @Override
        protected void onPostExecute(Object o) {
            if (o.toString().equals("1")) {
                img.setBitmap(bitmap);
            }
        }
    }
}

