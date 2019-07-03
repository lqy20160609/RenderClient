package com.practicaltraining.render;
/**
 * created By LQY
 * 2019.6.22
 * 主界面 使用drawerLayout
 */

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;


import com.alibaba.fastjson.JSONObject;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.practicaltraining.render.callbacks.ChangeCurrentFragment;
import com.practicaltraining.render.callbacks.CloseDrawer;

import com.practicaltraining.render.core.FragmentSwitchManager;

import com.practicaltraining.render.fragments.ColorFragment;


import com.practicaltraining.render.fragments.LightFragment;

import com.practicaltraining.render.fragments.ModelsFragment;
import com.practicaltraining.render.fragments.SettingFragment;

import com.practicaltraining.render.core.SocketIOManager;
import com.practicaltraining.render.fragments.TreeFragment;
import com.practicaltraining.render.utils.BitmapUtils;
import com.practicaltraining.render.utils.StaticVar;
import com.practicaltraining.render.views.MySurfaceView;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {
    private String TAG = "MainActivity";
    private DrawerLayout popMeunView;
    private CardView cardView;
    private MySurfaceView img;
    private Toolbar toolbar;
    private RadioGroup rgTrans;
    private RadioGroup rgAxis;
    private int currentOpType = -1;
    private int finalType = -1;
    private float preX = -1, preY = -1;
    private long startTime, endTime;
    private Bitmap bitmap;
    private FloatingActionButton backButton;
    private SettingFragment settingFragment;
    public Fragment currentFragment = null;
    private TreeFragment treeFragment;
    private ChangeCurrentFragment changeCurrentFragment = newTag -> {
        Log.d(TAG + "lqy", newTag);
        currentFragment = getSupportFragmentManager().findFragmentByTag(newTag);
    };
    private CloseDrawer closeDrawer = () -> popMeunView.closeDrawers();
    private ModelsFragment modelsFragment;
    private int imgWidth,imgHeight;
    //test fragment


    private void initView() {
        popMeunView = findViewById(R.id.drawer_layout);
        popMeunView.setScrimColor(Color.TRANSPARENT);
        img = findViewById(R.id.testImage);
        rgTrans = findViewById(R.id.rg_trans);
        rgAxis = findViewById(R.id.rg_axis);
        backButton = findViewById(R.id.nav_back_button);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        toolbar.setNavigationIcon(R.drawable.ic_menu_black_24dp);

        cardView = findViewById(R.id.card_view);
    }

    @SuppressLint("ClickableViewAccessibility")
    private void initListener() {
        // 设置toolBar监听
        toolbar.setNavigationOnClickListener(v -> popMeunView.openDrawer(GravityCompat.START));
        // 设置获取图片回调
        SocketIOManager.getInstance().setFinishcallback(data -> {
            GetBitmapTask mTask = new GetBitmapTask(StaticVar.picServerAddress + data + ".jpg");
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
                            Toast.makeText(MainActivity.this,
                                    "请选择操作模式",Toast.LENGTH_SHORT).show();
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
                                SocketIOManager.getInstance().getNewScence(jsonObject);
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

        // 设置返回键监听
        backButton.setOnClickListener(view -> {
            String currentTag = currentFragment.getTag();
            if (currentTag.equals(SettingFragment.class.getName())) {
                StaticVar.node = null;
                FragmentSwitchManager.getInstance().switchToPreFragmentByTag(getSupportFragmentManager(),
                        currentTag, TreeFragment.class.getName());
                currentFragment = treeFragment;
            } else if (currentTag.equals(TreeFragment.class.getName())) {
                popMeunView.closeDrawers();
            } else if (currentTag.equals(ModelsFragment.class.getName())) {
                StaticVar.node = null;
                FragmentSwitchManager.getInstance().switchToPreFragmentByTag(getSupportFragmentManager(),
                        currentTag, TreeFragment.class.getName());
                currentFragment = treeFragment;
            }
        });
        // 设置抽屉组件监听以及动画计算
        popMeunView.addDrawerListener(new DrawerLayout.DrawerListener() {

            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                View mContent = popMeunView.getChildAt(0);
                View mMenu = drawerView;
                float scale = 1 - slideOffset;
                float rightScale = 0.8f + scale * 0.2f;
                float leftScale = 0.5f + slideOffset * 0.5f;
                mMenu.setAlpha(leftScale);
                mMenu.setScaleX(leftScale);
                mMenu.setScaleY(leftScale);
                mMenu.setPadding(0, getStatusBarHeight(MainActivity.this), 0, 0);
                mContent.setPivotX(0);
                mContent.setPivotY(mContent.getHeight() * 1 / 2);
                mContent.setScaleX(rightScale);
                mContent.setScaleY(rightScale);
                mContent.setTranslationX(mMenu.getWidth() * slideOffset);
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                cardView.setRadius(20);
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                cardView.setRadius(0);
            }

            @Override
            public void onDrawerStateChanged(int newState) {


            }
        });

        // 设置选择操作单选按钮监听
        rgTrans.setOnCheckedChangeListener((radioGroup, i) -> {
            switch (i) {
                case R.id.rb_translate:
                    currentOpType = 0;
                    rgAxis.setVisibility(View.VISIBLE);
                    break;
                case R.id.rb_scale:
                    currentOpType = 1;
                    rgAxis.setVisibility(View.VISIBLE);
                    break;
                case R.id.rb_rotate:
                    currentOpType = 2;
                    rgAxis.setVisibility(View.VISIBLE);
                    break;
                case R.id.rb_rotateCamera:
                    finalType = 16;
                    rgAxis.setVisibility(View.INVISIBLE);
                    break;
                case R.id.rb_scaleCamera:
                    finalType = 17;
                    rgAxis.setVisibility(View.INVISIBLE);
                    break;
            }
        });
        // 设置选择坐标轴单选按钮监听
        rgAxis.setOnCheckedChangeListener((radioGroup, i) -> {
            switch (i) {
                case R.id.rb_axisX:
                    if (currentOpType == 0) {
                        finalType = 2;
                    } else if (currentOpType == 1) {
                        finalType = 5;
                    } else if (currentOpType == 2) {
                        finalType = 8;
                    }
                    break;
                case R.id.rb_axisY:
                    if (currentOpType == 0) {
                        finalType = 3;
                    } else if (currentOpType == 1) {
                        finalType = 6;
                    } else if (currentOpType == 2) {
                        finalType = 8;
                    }
                    break;
                case R.id.rb_axisZ:
                    if (currentOpType == 0) {
                        finalType = 4;
                    } else if (currentOpType == 1) {
                        finalType = 7;
                    } else if (currentOpType == 2) {
                        finalType = 8;
                    }
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
            //SocketIOManager.getInstance().sendParam(json);
        });
        // 向服务器发送屏幕宽高

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        if (savedInstanceState == null) {
            settingFragment = new SettingFragment();
            modelsFragment = new ModelsFragment();
            treeFragment = new TreeFragment();

            settingFragment.setChangeCurrentFragment(changeCurrentFragment);
            settingFragment.setCloseDrawer(closeDrawer);
            modelsFragment.setChangeCurrentFragment(changeCurrentFragment);
            modelsFragment.setCloseDrawer(closeDrawer);
            treeFragment.setChangeCurrentFragment(changeCurrentFragment);
            treeFragment.setCloseDrawer(closeDrawer);

            FragmentSwitchManager.getInstance().addNewFragmentWithHide(getSupportFragmentManager(),
                    settingFragment, R.id.nav_view);
            FragmentSwitchManager.getInstance().addNewFragmentWithHide(getSupportFragmentManager(),
                    modelsFragment, R.id.nav_view);
            FragmentSwitchManager.getInstance().addNewFragmentWithOutHide(getSupportFragmentManager(),
                    treeFragment, R.id.nav_view);
            currentFragment = treeFragment;
        }
        initListener();
    }

    public int getStatusBarHeight(Context context) {
        int result = 0;
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen",
                "android");
        if (resourceId > 0) {
            result = context.getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    private class GetBitmapTask extends AsyncTask {
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


    public double calLength(int opType, float x1, float y1, float x2, float y2) {
        float x0 = 0, y0 = 0;
        boolean flag = false;
        switch (opType) {
            case 2:
                x0 = 1;
                y0 = 0;
                flag = true;
                break;
            case 3:
                x0 = 0;
                y0 = -1;
                flag = true;
                break;
            case 4:
                x0 = -1;
                y0 = 1;
                flag = true;
                break;
            case 5:
                x0 = 1;
                y0 = 0;
                flag = true;
                break;
            case 6:
                x0 = 0;
                y0 = -1;
                flag = true;
                break;
            case 7:
                x0 = -1;
                y0 = 1;
                flag = true;
                break;
        }
        if (!flag) {
            return y2 - y1;
        } else {
            double l1 = Math.sqrt(x0 * x0 + y0 * y0);
            double l2 = Math.sqrt((x2 - x1) * (x2 - x1) + (y2 - y1) * (y2 - y1));
            double cos = (x0 * (x2 - x1) + y0 * (y2 - y1)) / (l1 * l2);
            Log.d("lqyDebug COS", cos + "");
            return cos * l2;
        }
    }
}
