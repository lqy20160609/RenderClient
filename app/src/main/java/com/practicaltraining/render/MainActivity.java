package com.practicaltraining.render;
/**
 * created By LQY
 * 2019.6.22
 * 主界面 使用drawerLayout
 */

import android.annotation.SuppressLint;
import android.content.Context;
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
import android.view.View;
import android.widget.TextView;


import com.getbase.floatingactionbutton.FloatingActionButton;
import com.practicaltraining.render.adapters.ModelChangeFuncAdapter;
import com.practicaltraining.render.callbacks.ChangeCurrentFragment;
import com.practicaltraining.render.callbacks.CloseDrawer;

import com.practicaltraining.render.core.FragmentSwitchManager;


import com.practicaltraining.render.fragments.ModelChangeFuncFragment;
import com.practicaltraining.render.fragments.ModelsFragment;
import com.practicaltraining.render.fragments.RoamingFragment;
import com.practicaltraining.render.fragments.SettingFragment;

import com.practicaltraining.render.fragments.TreeFragment;
import com.practicaltraining.render.utils.StaticVar;
import com.practicaltraining.render.views.NoScrollViewPager;

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
    private int imgWidth, imgHeight;
    //test fragment


    private void initView() {
        popMeunView = findViewById(R.id.drawer_layout);
        popMeunView.setScrimColor(Color.TRANSPARENT);
        viewPager=findViewById(R.id.viewpager);
        tabs=findViewById(R.id.tabs);

        list=new ArrayList<>();
        list.add(new ModelChangeFuncFragment());
        list.add(new RoamingFragment());
        modelChangeFuncAdapter=new ModelChangeFuncAdapter(getSupportFragmentManager(),list);
        viewPager.setAdapter(modelChangeFuncAdapter);

        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabs));
        tabs.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(viewPager));

        backButton = findViewById(R.id.nav_back_button);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        toolbar.setNavigationIcon(R.drawable.ic_menu_black_24dp);
        fpsText = findViewById(R.id.main_fps);
        cardView = findViewById(R.id.card_view);
    }

    @SuppressLint("ClickableViewAccessibility")
    private void initListener() {
        // 设置toolBar监听
        toolbar.setNavigationOnClickListener(v -> popMeunView.openDrawer(GravityCompat.START));


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
                    fpsText.setText("fps:"+(StaticVar.currentSecondFrames*2));
                });
                StaticVar.currentSecondFrames = 0;
            }
        }, 0, 500);
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
