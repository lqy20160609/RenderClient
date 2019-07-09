package com.practicaltraining.render.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;


import java.util.List;

public class ModelChangeFuncAdapter extends FragmentPagerAdapter {
    private FragmentManager fragmentManager;
    private List<Fragment> list;
    public ModelChangeFuncAdapter(FragmentManager fm,List<Fragment> list){
        super(fm);
        this.list=list;
    }
    @Override
    public Fragment getItem(int position) {
        //return ModelChangeFuncFragment.newInstance(position+1);
        return list.get(position);
    }

    @Override
    public int getCount() {
        return list.size();
    }
}
