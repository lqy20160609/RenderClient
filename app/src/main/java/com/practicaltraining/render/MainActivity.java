package com.practicaltraining.render;
/**
 * created By LQY
 * 2019.6.22
 * 主界面 使用drawerLayout
 */
import android.content.Context;
import android.graphics.Color;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    private DrawerLayout popMeunView;
    private FrameLayout contentView;
    private CardView cardView;

    private String[] datas = {"test1","test2","test3","test4","test5"};
    private ArrayAdapter<String> adapter;
    private ListView listView;
    // test

    private void initView(){
        popMeunView = (DrawerLayout)findViewById(R.id.drawer_layout);
        popMeunView.setScrimColor(Color.TRANSPARENT);
        contentView = (FrameLayout)findViewById(R.id.content_view);//当前面板
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        toolbar.setNavigationIcon(R.drawable.ic_menu_black_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popMeunView.openDrawer(GravityCompat.START);
            }
        });
        cardView = (CardView)findViewById(R.id.card_view);

        //接收触控信息
        contentView.setOnTouchListener(new View.OnTouchListener() {
            TextView postest=(TextView)findViewById(R.id.postest);
            TextView methtest=(TextView)findViewById(R.id.methtest);
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
    }
    private void drawMenuList(){
        //绘制左侧选项
        listView = (ListView) findViewById(R.id.menulist);
        adapter = new ArrayAdapter<>(this,android.R.layout.simple_expandable_list_item_1,datas);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Toast.makeText(getApplicationContext(),"您打开了："+adapter.getItem(i),Toast.LENGTH_SHORT).show();
            }
        });
    }

            @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        drawMenuList();//绘制菜单表项

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

}
