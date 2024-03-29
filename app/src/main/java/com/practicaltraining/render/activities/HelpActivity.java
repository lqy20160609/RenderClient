package com.practicaltraining.render.activities;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
    private BitmapFactory.Options opts;
    SparseArray<View> PageCache=new SparseArray<View>();
    private List<Fragment> helpList;
    private Button helpSkip;
    private Bitmap bitmap;
    int pagenum=3;//总页数

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
            return pagenum;
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
                        opts = new BitmapFactory.Options();
                        opts.inSampleSize= 2;
                        bitmap =BitmapFactory.decodeResource(getResources(),R.drawable.renderhelp, opts);
                        helpImage.setImageBitmap(bitmap);
                        helpText.setText("点击左上角弹出Drawer，右上角弹出菜单\n"
                                +"点击Tab切换操作模式\n"+"滑动视图进行模型操作");
                        break;
                    case 1:
                        opts = new BitmapFactory.Options();
                        opts.inSampleSize= 2;
                        bitmap =BitmapFactory.decodeResource(getResources(),R.drawable.renderhelp2, opts);
                        helpImage.setImageBitmap(bitmap);
                        helpText.setText("长按项目进行设置，点击圆圈进行模型选择\n，+添加模型，×删除模型");
                        break;
                    case 2:
                        opts = new BitmapFactory.Options();
                        opts.inSampleSize= 2;
                        bitmap =BitmapFactory.decodeResource(getResources(),R.drawable.renderhelp3, opts);
                        helpImage.setImageBitmap(bitmap);
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
            int positionplusone=position+1;
            helpPageNum.setText(positionplusone+"/"+pagenum);
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    }

}
