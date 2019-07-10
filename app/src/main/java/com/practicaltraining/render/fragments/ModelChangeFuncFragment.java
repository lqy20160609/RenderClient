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
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.practicaltraining.render.R;
import com.practicaltraining.render.core.SocketIOManager;
import com.practicaltraining.render.utils.BitmapUtils;
import com.practicaltraining.render.utils.StaticVar;
import com.practicaltraining.render.views.MySurfaceView;

import java.io.IOException;

public class ModelChangeFuncFragment extends Fragment {
    private static final String ARG_SECTION_NUMBER="section_number";
    private RadioGroup rgTrans;
    private RadioGroup rgAxis;
    private RadioButton wholeScale;
    private static MySurfaceView img;
    private static Bitmap bitmap;
    private int currentOpType = -1;
    private int finalType = -1;
    private float preX = -1, preY = -1;
    private long startTime, endTime;
    private int imgWidth,imgHeight;
    private int index1=-1,index2=-1;

    public static ModelChangeFuncFragment newInstance(int sectionNum){
        ModelChangeFuncFragment fragment=new ModelChangeFuncFragment();
        Bundle args=new Bundle();
        args.putInt(ARG_SECTION_NUMBER,sectionNum);
        fragment.setArguments(args);
        return fragment;
    }
    @SuppressLint("ClickableViewAccessibility")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.model_transform, container, false);

        img = rootView.findViewById(R.id.Model_image);//testImage

        rgTrans = rootView.findViewById(R.id.rg_trans);
        rgAxis = rootView.findViewById(R.id.rg_axis);
        wholeScale = rootView.findViewById(R.id.rb_wholeScale);
        // 设置获取图片回调
        SocketIOManager.getInstance().setModelFinishCallBack(data -> {
            ModelChangeFuncFragment.GetBitmapTask mTask = new ModelChangeFuncFragment.GetBitmapTask(StaticVar.picServerAddress + data + ".jpg");
            mTask.execute();
        });
        //接收触控信息
        img.setOnTouchListener(new View.OnTouchListener() {
            int mode = 0;
            @Override
            public boolean onTouch(View view, MotionEvent event) {

                switch (event.getAction() & MotionEvent.ACTION_MASK) {
                    case MotionEvent.ACTION_DOWN:
                        if (finalType==-1){
                            Toast.makeText(rootView.getContext(),
                                    "请选择完整操作模式",Toast.LENGTH_SHORT).show();
                            return true;
                        }
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
                                jsonObject.put("operation_type", finalType);
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
        // 设置选择操作单选按钮监听
        rgTrans.setOnCheckedChangeListener((radioGroup, i) -> {
            switch (i) {
                case R.id.rb_translate:
                    index1 = 0;
                    finalType = getCurrentOpType();
                    rgAxis.setVisibility(View.VISIBLE);
                    wholeScale.setVisibility(View.INVISIBLE);
                    break;
                case R.id.rb_scale:
                    index1 = 1;
                    finalType = getCurrentOpType();
                    rgAxis.setVisibility(View.VISIBLE);
                    wholeScale.setVisibility(View.VISIBLE);
                    break;
                case R.id.rb_rotate:
                    index1 = 2;
                    finalType = getCurrentOpType();
                    rgAxis.setVisibility(View.VISIBLE);
                    wholeScale.setVisibility(View.INVISIBLE);
                    break;
                case R.id.rb_rotateCamera:
                    index1 = 3;
                    finalType = getCurrentOpType();
                    rgAxis.setVisibility(View.INVISIBLE);
                    wholeScale.setVisibility(View.INVISIBLE);
                    break;
                case R.id.rb_scaleCamera:
                    index1 = 4;
                    finalType = getCurrentOpType();
                    rgAxis.setVisibility(View.INVISIBLE);
                    wholeScale.setVisibility(View.INVISIBLE);
                    break;
            }
        });
        // 设置选择坐标轴单选按钮监听
        rgAxis.setOnCheckedChangeListener((radioGroup, i) -> {
            switch (i) {
                case R.id.rb_axisX:
                    index2=0;
                    finalType = getCurrentOpType();
                    break;
                case R.id.rb_axisY:
                    index2=1;
                    finalType = getCurrentOpType();
                    break;
                case R.id.rb_axisZ:
                    index2=2;
                    finalType = getCurrentOpType();
                    break;
                case R.id.rb_wholeScale:
                    index2=3;
                    finalType = getCurrentOpType();
                    break;
            }
        });
        img.post(() -> {
            imgWidth = img.getWidth();
            imgHeight = img.getHeight();
            JSONObject json = new JSONObject();
            json.put("operation_type", 12);
            json.put("viewWidth", imgWidth);
            json.put("viewHeight", imgHeight);
            SocketIOManager.getInstance().sendParam(json);
        });
        // 向服务器发送屏幕宽高
        return rootView;
    }
    private int getCurrentOpType(){
        switch (index1){
            case 0:
                switch (index2){
                    case 0:
                        return 2;
                    case 1:
                        return 3;
                    case 2:
                        return 4;
                }
                break;
            case 1:
                switch (index2){
                    case 0:
                        return 8;
                    case 1:
                        return 9;
                    case 2:
                        return 10;
                    case 3:
                        return 19;
                }
                break;
            case 2:
                switch (index2){
                    case 0:
                        return 5;
                    case 1:
                        return 6;
                    case 2:
                        return 7;
                }
                break;
            case 3:
                return 16;
            case 4:
                return 17;
        }
        return -1;
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
                StaticVar.currentSecondModelFrames++;
            }
        }
    }
    @Override
    public void onStart() {
        super.onStart();
        img.init();
    }

}
