package com.practicaltraining.render.activities;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Space;
import android.widget.TextView;

import com.practicaltraining.render.R;

import java.util.ArrayList;
import java.util.List;

public class HelpActivity extends Activity {
    ViewPager helpPage;
    TextView helpText;//引导文字
    ImageView helpImage;//引导图片
    TextView helpPageNum;//标识页码
    SparseArray<View> PageCache=new SparseArray<View>();
    private List<Fragment> helpList;
    private Button helpSkip;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.help_frag);
        //resources
        helpPageNum=findViewById(R.id.help_page_num);
        helpSkip=findViewById(R.id.help_skip);
        //ComponentName helpActivity=getIntent().getComponent();
        //helpList=new ArrayList<>();
        //for viewPager
        helpPage=findViewById(R.id.help_page);
        helpPage.setAdapter(new helpPageAdapter(this));
        helpPage.addOnPageChangeListener(new OnViewPageChangeListener());

        helpSkip.setOnClickListener(view -> {
            finish();
        });
    }

    private class helpPageAdapter extends PagerAdapter{
        private LayoutInflater helpInflater;

        private helpPageAdapter(Context context){
            helpInflater=LayoutInflater.from(context);
        }
        @Override
        public int getCount() {
            return 3;
        }

        @Override
        public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
            return view==object;
        }

        @NonNull
        @Override
        public Object instantiateItem(@NonNull ViewGroup container, int position) {
            View page=PageCache.get(position);
            if(page==null){
                page=helpInflater.inflate(R.layout.help_frag_page,container,false);
                helpText=page.findViewById(R.id.help_text);
                helpImage=page.findViewById(R.id.help_image);
                switch (position){
                    case 0:
                        helpText.setText("点击左上角弹出Drawer，右上角弹出菜单\n"
                                +"点击Tab切换操作模式\n"+"滑动视图进行模型操作");
                        break;
                    case 1:
                        helpImage.setImageResource(R.drawable.renderhelp2);
                        helpText.setText("长按项目进行设置，点击圆圈进行模型选择\n，+添加模型，×删除模型");
                        break;
                    case 2:
                        helpImage.setImageResource(R.drawable.renderhelp3);
                        helpText.setText("选择一个模型，点击确认按钮完成添加");
                        break;
                    default:
                        break;
                }
                PageCache.append(position,page);
            }
            container.addView(page);
            return page;
        }

        @Override
        public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
            container.removeView((View)object);
        }
    }
    private class OnViewPageChangeListener implements ViewPager.OnPageChangeListener{

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            switch (position){
                case 0:
                    helpPageNum.setText("1/3");
                    break;
                case 1:
                    helpPageNum.setText("2/3");
                    break;
                case 2:
                    helpPageNum.setText("3/3");
                    break;
            }

        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    }

}
