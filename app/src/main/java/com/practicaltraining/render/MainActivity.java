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
import android.support.constraint.ConstraintLayout;
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
import android.widget.RadioGroup;
import android.widget.TextView;


import com.alibaba.fastjson.JSONObject;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.practicaltraining.render.callbacks.ChangeCurrentFragment;
import com.practicaltraining.render.callbacks.GetPhotoCompleted;

import com.practicaltraining.render.core.FragmentSwitchManager;
import com.practicaltraining.render.fragments.MenuFragment;
import com.practicaltraining.render.fragments.ModelsFragment;
import com.practicaltraining.render.fragments.SettingFragment;

import com.practicaltraining.render.core.SocketIOManager;
import com.practicaltraining.render.fragments.TreeFragment;
import com.practicaltraining.render.utils.BitmapUtils;
import com.practicaltraining.render.utils.StaticVar;
import com.practicaltraining.render.views.MySurfaceView;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {
    private DrawerLayout popMeunView;
    private CardView cardView;
    private MySurfaceView img;
    private TextView postest;
    private Toolbar toolbar;
    private RadioGroup rgTrans;
    private RadioGroup rgAxis;
    private int currentOpType=-1;
    private int finalType=-1;
    //private RadioButton rbTranslate,rbScale,rbRotate,rbAxisX,rbAxisY,rbAxisZ;
    private float preX=-1,preY=-1;
    private long startTime,endTime;
    private Bitmap bitmap;
    private FloatingActionButton backButton;
    private MenuFragment menuFragment;
    private SettingFragment settingFragment;
    public Fragment currentFragment=null;
    private TreeFragment treeFragment;
    private ChangeCurrentFragment changeCurrentFragment=new ChangeCurrentFragment() {
        @Override
        public void changeCurrentFragment(String newTag) {
            currentFragment = getSupportFragmentManager().findFragmentByTag(newTag);
        }
    };
    private ModelsFragment modelsFragment;


    private void initView(){
        popMeunView = findViewById(R.id.drawer_layout);
        popMeunView.setScrimColor(Color.TRANSPARENT);
        img = findViewById(R.id.testImage);
        rgTrans = findViewById(R.id.rg_trans);
        rgAxis = findViewById(R.id.rg_axis);
        backButton = findViewById(R.id.nav_back_button);
        toolbar =  findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        postest=findViewById(R.id.postest);
        getSupportActionBar().setTitle("");
        toolbar.setNavigationIcon(R.drawable.ic_menu_black_24dp);
        cardView = findViewById(R.id.card_view);


    }

    @SuppressLint("ClickableViewAccessibility")
    private void initListener(){
        // 设置toolBar监听
        toolbar.setNavigationOnClickListener(v -> popMeunView.openDrawer(GravityCompat.START));
        // 设置获取图片回调
        SocketIOManager.getInstance().setFinishcallback(data -> {
            GetBitmapTask mTask = new GetBitmapTask(StaticVar.picServerAddress+data+".jpg");
            mTask.execute();

        });
        //接收触控信息
        img.setOnTouchListener(new View.OnTouchListener() {
            int mode=0;
            @Override
            public boolean onTouch(View view, MotionEvent event) {

                switch (event.getAction()&MotionEvent.ACTION_MASK){
                    case MotionEvent.ACTION_DOWN:
                        mode=1;
                        preX=event.getX();
                        preY=event.getY();
                        startTime = System.currentTimeMillis();
                        break;
                    case MotionEvent.ACTION_UP:
                        mode=0;
                    case MotionEvent.ACTION_MOVE:
                        if(mode==1){
                            endTime = System.currentTimeMillis();
                            if (endTime-startTime>=60){
                                float currentX=event.getX();
                                float currentY=event.getY();
                                startTime = System.currentTimeMillis();
                                JSONObject jsonObject = new JSONObject();
                                jsonObject.put("operation_type",finalType);
                                jsonObject.put("para",calLength(finalType,preX,preY,
                                        currentX,currentY));
                                SocketIOManager.getInstance().getNewScence(jsonObject);
                                Log.d("jsonToSend",jsonObject.toJSONString());
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
            if (currentTag.equals(SettingFragment.class.getName())){
                FragmentSwitchManager.getInstance().switchToNextFragmentByTag(
                        getSupportFragmentManager(),
                        currentTag,ModelsFragment.class.getName());
                currentFragment = modelsFragment;
            }else{
                popMeunView.closeDrawers();
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
                mMenu.setPadding(0,getStatusBarHeight(MainActivity.this),0,0);
                mContent.setPivotX(0);
                mContent.setPivotY(mContent.getHeight() * 1/2);
                mContent.setScaleX(rightScale);
                mContent.setScaleY(rightScale);
                mContent.setTranslationX(mMenu.getWidth() * slideOffset);
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                FragmentSwitchManager.getInstance().switchToNextFragment(getSupportFragmentManager(),
                        currentFragment,currentFragment,R.id.nav_view);
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
            switch (i){
                case R.id.rb_translate:
                    currentOpType = 0;
                    rgAxis.setVisibility(View.VISIBLE);
                    break;
                case R.id.rb_scale:
                    currentOpType = 1;
                    rgAxis.setVisibility(View.INVISIBLE);
                    break;
                case R.id.rb_rotate:
                    currentOpType = 2;
                    rgAxis.setVisibility(View.VISIBLE);
                    break;
            }
        });
        // 设置选择坐标轴单选按钮监听
        rgAxis.setOnCheckedChangeListener((radioGroup, i) -> {
            switch (i){
                case R.id.rb_axisX:
                    if (currentOpType==0){
                        finalType = 2;
                    }else if (currentOpType==1){
                        finalType = 5;
                    }else if(currentOpType==2){
                        finalType = 8;
                    }
                    break;
                case R.id.rb_axisY:
                    if (currentOpType==0){
                        finalType = 3;
                    }else if (currentOpType==1){
                        finalType = 6;
                    }else if(currentOpType==2){
                        finalType = 8;
                    }
                    break;
                case R.id.rb_axisZ:
                    if (currentOpType==0){
                        finalType = 4;
                    }else if (currentOpType==1){
                        finalType = 7;
                    }else if(currentOpType==2){
                        finalType = 8;
                    }
                    break;
            }
        });
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        if (savedInstanceState==null){
            menuFragment = new MenuFragment();
            settingFragment = new SettingFragment();
            modelsFragment=new ModelsFragment();
            treeFragment = new TreeFragment();

            menuFragment.setChangeCurrentFragment(changeCurrentFragment);
            settingFragment.setChangeCurrentFragment(changeCurrentFragment);
            modelsFragment.setChangeCurrentFragment(changeCurrentFragment);
            treeFragment.setChangeCurrentFragment(changeCurrentFragment);
            FragmentSwitchManager.getInstance().addNewFragmentWithHide(getSupportFragmentManager(),
                    menuFragment,R.id.nav_view);
            FragmentSwitchManager.getInstance().addNewFragmentWithHide(getSupportFragmentManager(),
                    settingFragment,R.id.nav_view);
            FragmentSwitchManager.getInstance().addNewFragmentWithHide(getSupportFragmentManager(),
                    modelsFragment,R.id.nav_view);//my adding
            FragmentSwitchManager.getInstance().addNewFragmentWithHide(getSupportFragmentManager(),
                    treeFragment,R.id.nav_view);
            currentFragment = menuFragment;
        }else{
            FragmentSwitchManager.getInstance().switchToNextFragment(getSupportFragmentManager(),
                    currentFragment,currentFragment,R.id.container);
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
                bitmap = BitmapUtils.getBitmapFromInternet("http://"+imageAddress);
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
    public double calLength(int opType,float x1,float y1,float x2,float y2){
        float x0=0,y0=0;
        boolean flag = false;
        switch (opType){
            case 2:
                x0=1;y0=0;
                flag = true;
                break;
            case 3:
                x0=0;y0=-1;
                flag = true;
                break;
            case 4:
                x0=-1;y0=1;
                flag = true;
                break;
            case 5:
                x0=1;y0=0;
                flag = true;
                break;
            case 6:
                x0=0;y0=-1;
                flag = true;
                break;
            case 7:
                x0=-1;y0=1;
                flag = true;
                break;
        }
        if (!flag){
            return y2-y1;
        }else{

            double l1 = Math.sqrt(x0*x0+y0*y0);
            double l2 = Math.sqrt((x2-x1)*(x2-x1)+(y2-y1)*(y2-y1));
            double cos = (x0*(x2-x1)+y0*(y2-y1))/(l1*l2);
            Log.d("lqyDebug COS",cos+"");
            return cos*l2;
        }
    }
}
