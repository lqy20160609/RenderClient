package com.practicaltraining.render;
/**
 * created By LQY
 * 2019.6.22
 * 主界面 使用drawerLayout
 */
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
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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

import java.io.IOException;

public class MainActivity extends AppCompatActivity {
    private DrawerLayout popMeunView;
    private CardView cardView;
    private ImageView img;
    private TextView postest;
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

    // test
    //add sth due to Recyclerview
    private ModelsFragment modelsFragment;


    private void initView(){
        popMeunView = findViewById(R.id.drawer_layout);
        popMeunView.setScrimColor(Color.TRANSPARENT);
        //contentView = findViewById(R.id.content_view);
        img = findViewById(R.id.testImage);
        backButton = findViewById(R.id.nav_back_button);
        Toolbar toolbar =  findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        postest=findViewById(R.id.postest);
        getSupportActionBar().setTitle("");
        toolbar.setNavigationIcon(R.drawable.ic_menu_black_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popMeunView.openDrawer(GravityCompat.START);
            }
        });
        SocketIOManager.getInstance().setFinishcallback(new GetPhotoCompleted() {
            @Override
            public void getDataCompleted(String data) {
                GetBitmapTask mTask = new GetBitmapTask(data);
                mTask.execute();

            }
        });
        cardView = findViewById(R.id.card_view);
        //接收触控信息
        img.setOnTouchListener(new View.OnTouchListener() {
            int mode=0;
            @Override
            public boolean onTouch(View view, MotionEvent event) {

                switch (event.getAction()&MotionEvent.ACTION_MASK){
                    //action_mask实现多点触控（只能最多识别两个触控点）
                    case MotionEvent.ACTION_DOWN:
                        mode=1;
                        break;
                    case MotionEvent.ACTION_UP:
                        mode=0;
                        break;
                    case MotionEvent.ACTION_POINTER_DOWN:
                        mode+=1;
                        break;
                    case MotionEvent.ACTION_POINTER_UP:
                        mode-=1;
                        break;
                    case MotionEvent.ACTION_MOVE:
                        if(mode==1){
                            postest.setText("You position:("+event.getX()+","+event.getY()+")");
                        }
                        if(mode>=2){
                            postest.setText("You position:("+event.getX(0)+","+event.getY(0)
                                    +")&("+event.getX(1)+","+event.getY(1)+")");
                            //methtest.setText("You are scaling:"+spacing(event));
                        }
                        break;
                    default:
                        postest.setText("Hello!!!");
                        break;
                }
                return true;
            }

            //其他的功能
            private float spacing(MotionEvent event){
                float x=event.getX(0)-event.getX(1);
                float y=event.getY(0)-event.getY(1);
                return x*x+y*y;
            }
        });
        menuFragment = new MenuFragment();
        settingFragment = new SettingFragment();
        modelsFragment=new ModelsFragment();
        treeFragment = new TreeFragment();

        menuFragment.setChangeCurrentFragment(changeCurrentFragment);
        settingFragment.setChangeCurrentFragment(changeCurrentFragment);
        modelsFragment.setChangeCurrentFragment(changeCurrentFragment);
        treeFragment.setChangeCurrentFragment(changeCurrentFragment);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        FragmentSwitchManager.getInstance().addNewFragmentWithHide(getSupportFragmentManager(),
                menuFragment,R.id.nav_view);
        FragmentSwitchManager.getInstance().addNewFragmentWithHide(getSupportFragmentManager(),
                settingFragment,R.id.nav_view);
        FragmentSwitchManager.getInstance().addNewFragmentWithHide(getSupportFragmentManager(),
                modelsFragment,R.id.nav_view);//my adding
        FragmentSwitchManager.getInstance().addNewFragmentWithHide(getSupportFragmentManager(),
                treeFragment,R.id.nav_view);
        currentFragment = menuFragment;
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String currentTag = currentFragment.getTag();
                if (currentTag.equals(SettingFragment.class.getName())){
                    FragmentSwitchManager.getInstance().switchToNextFragmentByTag(
                            getSupportFragmentManager(),
                            currentTag,ModelsFragment.class.getName());
                    currentFragment = modelsFragment;
                }else{
                    popMeunView.closeDrawers();
                }
            }
        });

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
                bitmap = BitmapUtils.getBitmapFromInternet(imageAddress);
                return 1;
            } catch (IOException e) {
                e.printStackTrace();
                return 0;
            }
        }

        @Override
        protected void onPostExecute(Object o) {
            if (o.toString().equals("1")) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        img.setImageBitmap(bitmap);
                    }
                });
            }
        }
    }
}
