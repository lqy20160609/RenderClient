package com.practicaltraining.render;
/**
 * created By LQY
 * 2019.6.22
 * 主界面 使用drawerLayout
 */

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.TextView;
import android.widget.Toast;


import com.alibaba.fastjson.JSONObject;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.practicaltraining.render.activities.HelpActivity;

import com.practicaltraining.render.adapters.ModelChangeFuncAdapter;
import com.practicaltraining.render.callbacks.ChangeCurrentFragment;
import com.practicaltraining.render.callbacks.CloseDrawer;

import com.practicaltraining.render.core.FragmentSwitchManager;


import com.practicaltraining.render.core.SocketIOManager;

import com.practicaltraining.render.fragments.ModelChangeFuncFragment;
import com.practicaltraining.render.fragments.ModelsFragment;
import com.practicaltraining.render.fragments.RoamingFragment;
import com.practicaltraining.render.fragments.SettingFragment;

import com.practicaltraining.render.fragments.TreeFragment;
import com.practicaltraining.render.utils.StaticVar;
import com.practicaltraining.render.views.NoScrollViewPager;

import java.lang.reflect.Field;

import java.util.Timer;
import java.util.TimerTask;
import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity {
    private String TAG = "MainActivity";
    private DrawerLayout popMeunView;
    private CardView cardView;
    private List<Fragment> list;
    private Toolbar toolbar;
    private boolean canOpenDrawer = true;
    private ProgressDialog progressDialog;
    private TextView fpsText;

    private FloatingActionButton backButton;
    private SettingFragment settingFragment;
    public Fragment currentFragment = null;
    private TreeFragment treeFragment;
    //for viewpager
    private ModelChangeFuncAdapter modelChangeFuncAdapter;
    private NoScrollViewPager viewPager;
    private TabLayout tabs;
    private NoScrollViewPager vp;
    private ChangeCurrentFragment changeCurrentFragment = newTag -> {
        Log.d(TAG + "lqy", newTag);
        currentFragment = getSupportFragmentManager().findFragmentByTag(newTag);
    };
    private CloseDrawer closeDrawer = () -> popMeunView.closeDrawers();
    private ModelsFragment modelsFragment;
    private boolean flagVulkan=true;


    private void initView() {
        popMeunView = findViewById(R.id.drawer_layout);
        popMeunView.setScrimColor(Color.TRANSPARENT);
        viewPager = findViewById(R.id.viewpager);
        tabs = findViewById(R.id.tabs);

        list = new ArrayList<>();
        list.add(new ModelChangeFuncFragment());
        list.add(new RoamingFragment());
        modelChangeFuncAdapter = new ModelChangeFuncAdapter(getSupportFragmentManager(), list);
        viewPager.setAdapter(modelChangeFuncAdapter);

        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabs));
        tabs.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab.getText().toString().equals("模型视图")) {
                    JSONObject json = new JSONObject();
                    json.put("operation_type", 25);
                    SocketIOManager.getInstance().sendParam(json);
                    viewPager.setCurrentItem(0);
                    canOpenDrawer = true;
                } else {
                    JSONObject json = new JSONObject();
                    json.put("operation_type", 25);
                    SocketIOManager.getInstance().sendParam(json);
                    viewPager.setCurrentItem(1);
                    canOpenDrawer = false;
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        backButton = findViewById(R.id.nav_back_button);
        //toolbar
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("RenderClient");
        toolbar.setNavigationIcon(R.drawable.ic_menu_black_24dp);
        makeActionOverflowMenuShown();

        fpsText = findViewById(R.id.main_fps);
        cardView = findViewById(R.id.card_view);
    }

    @SuppressLint("ClickableViewAccessibility")
    private void initListener() {

        // 设置toolBar监听
        toolbar.setNavigationOnClickListener(v -> {
            if (canOpenDrawer) {
                popMeunView.openDrawer(GravityCompat.START);
            } else {
                Toast.makeText(MainActivity.this, "请专心漫游哦", Toast.LENGTH_SHORT).show();
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
            public void onDrawerSlide(@NonNull View drawerView, float slideOffset) {
                View mContent = popMeunView.getChildAt(0);
                float scale = 1 - slideOffset;
                float rightScale = 0.8f + scale * 0.2f;
                float leftScale = 0.5f + slideOffset * 0.5f;
                drawerView.setAlpha(leftScale);
                drawerView.setScaleX(leftScale);
                drawerView.setScaleY(leftScale);
                drawerView.setPadding(0, getStatusBarHeight(MainActivity.this), 0, 0);
                mContent.setPivotX(0);
                mContent.setPivotY(mContent.getHeight() * 1 / 2);
                mContent.setScaleX(rightScale);
                mContent.setScaleY(rightScale);
                mContent.setTranslationX(drawerView.getWidth() * slideOffset);
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                if (StaticVar.shouldClear) {
                    progressDialog = new ProgressDialog(MainActivity.this,
                            R.style.AppTheme_Dark_Dialog);
                    progressDialog.setCanceledOnTouchOutside(false);
                    progressDialog.setCancelable(false);
                    progressDialog.setMessage("初始化中...");
                    progressDialog.show();
                    treeFragment.reset(progressDialog);
                    modelsFragment.changeLists(StaticVar.currentEngine,progressDialog);
                }

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
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString("currentFragment", currentFragment.getTag());
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        StaticVar.currentEngine = "Optix";
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
        } else {
            currentFragment = getSupportFragmentManager().findFragmentByTag(
                    (String) savedInstanceState.get("currentFragment"));
            FragmentSwitchManager.getInstance().switchToNextFragment(getSupportFragmentManager(), currentFragment,
                    currentFragment, R.id.nav_view);
        }
        initListener();
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(()->{
                    if (viewPager.getCurrentItem()==1) {
                        fpsText.setText("fps:" + (StaticVar.currentSecondRoamingFrames));
                    }else{
                        fpsText.setText("fps:" + (StaticVar.currentSecondModelFrames));
                    }
                });
                StaticVar.currentSecondRoamingFrames = 0;
                StaticVar.currentSecondModelFrames = 0;

            }
        }, 0, 1000);
    }

    //menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int menuItemId = item.getItemId();
        switch (menuItemId) {
            case R.id.switchTo_Optix:
                if (StaticVar.currentEngine.equals("Optix")){
                    Toast.makeText(MainActivity.this,"当前已经是Optix引擎了",Toast.LENGTH_SHORT).show();
                }else{

                    SocketIOManager.getInstance().resetSocket(0);
                    StaticVar.currentEngine = "Optix";
                    progressDialog = new ProgressDialog(MainActivity.this,
                            R.style.AppTheme_Dark_Dialog);
                    progressDialog.setCanceledOnTouchOutside(false);
                    progressDialog.setCancelable(false);
                    progressDialog.setMessage("切换中...");
                    progressDialog.show();
                    new Thread(() -> {
                        try {
                            SocketIOManager.getInstance();
                            Thread.sleep(1000);
                            JSONObject json = new JSONObject();
                            json.put("operation_type", 14);
                            json.put("viewWidth", StaticVar.imgWidth);
                            json.put("viewHeight", StaticVar.imgHeight);
                            SocketIOManager.getInstance().sendParam(json);
                            StaticVar.meshNum = 0;
                            StaticVar.node = null;
                            StaticVar.shouldClear = true;
                            //modelsFragment.changeLists(StaticVar.currentEngine);
                            Thread.sleep(1000);
                            progressDialog.dismiss();
                        }catch (Exception e){
                            e.printStackTrace();
                            progressDialog.dismiss();
                        }
                    }).start();
                }
                return true;
            case R.id.switchTo_vulkan:
                if (StaticVar.currentEngine.equals("Vulkan")){
                    Toast.makeText(MainActivity.this,"当前已经是Vulkan引擎了",Toast.LENGTH_SHORT).show();
                }else{
                    SocketIOManager.getInstance().resetSocket(1);
                    StaticVar.currentEngine = "Vulkan";
                    progressDialog = new ProgressDialog(MainActivity.this,
                            R.style.AppTheme_Dark_Dialog);
                    progressDialog.setCanceledOnTouchOutside(false);
                    progressDialog.setCancelable(false);
                    progressDialog.setMessage("切换中...");
                    progressDialog.show();
                    new Thread(() -> {
                        try {
                            SocketIOManager.getInstance();
                            Thread.sleep(1000);
                            JSONObject json = new JSONObject();
                            json.put("operation_type", 14);
                            json.put("viewWidth", StaticVar.imgWidth);
                            json.put("viewHeight", StaticVar.imgHeight);
                            SocketIOManager.getInstance().sendParam(json);
                            StaticVar.meshNum = 0;
                            StaticVar.node = null;
                            StaticVar.shouldClear = true;
                            //modelsFragment.changeLists(StaticVar.currentEngine);
                            Thread.sleep(1000);
                            progressDialog.dismiss();
                        }catch (Exception e){
                            e.printStackTrace();
                            progressDialog.dismiss();
                        }
                    }).start();
                }
                return true;
            case R.id.setting:
                return true;
            case R.id.help_button:
                //因内存分配问题请在加载模型之前启动
                ComponentName helpActivity=new ComponentName(MainActivity.this,HelpActivity.class);
                Intent intent=new Intent();
                intent.setComponent(helpActivity);
                startActivity(intent);
                return true;
            case R.id.exit:
                System.exit(0);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void makeActionOverflowMenuShown() {
        try {
            ViewConfiguration config = ViewConfiguration.get(this);
            Field menuKeyField = ViewConfiguration.class.getDeclaredField("sHasPermanentMenuKey");
            if (menuKeyField != null) {
                menuKeyField.setAccessible(true);
                menuKeyField.setBoolean(config, false);
            }
        } catch (Exception e) {
        }
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

}
