package com.practicaltraining.render.core;
/**
 * created By LQY
 * 对fragment切换进行封装，使用回退栈
 * 使用hide和show防止fragment实例多次创建
 */

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

public class FragmentSwitchManager {
    private static FragmentSwitchManager fragmentSwitchManager;


    public static FragmentSwitchManager getInstance() {
        if (fragmentSwitchManager == null) {
            synchronized (FragmentSwitchManager.class) {
                if (fragmentSwitchManager == null) {
                    fragmentSwitchManager = new FragmentSwitchManager();
                    return fragmentSwitchManager;
                }
            }
        }
        return fragmentSwitchManager;
    }

    public void addNewFragmentWithHide(FragmentManager fragmentManager,Fragment fragment,int container){
        fragmentManager.beginTransaction()
                .add(container, fragment, fragment.getClass().getName())
                .hide(fragment)
                .commit();
    }

    public void addNewFragmentWithOutHide(FragmentManager fragmentManager,Fragment fragment,int container){
        fragmentManager.beginTransaction()
                .add(container, fragment, fragment.getClass().getName())
                .commit();
    }

    public void switchToNextFragment(FragmentManager fragmentManager,Fragment currentFragment,
                                     Fragment nextFragment, int container) {
        if (currentFragment == null) {
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            if (!nextFragment.isAdded()) {
                addNewFragmentWithOutHide(fragmentManager, nextFragment, container);
            } else {
                transaction.show(nextFragment).commit();
            }
        }else if (currentFragment != nextFragment) {
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction.hide(currentFragment);
            if (!nextFragment.isAdded()) {
                addNewFragmentWithOutHide(fragmentManager, nextFragment, container);
            } else {
                transaction.show(nextFragment).commit();
            }
        }else{
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            if (!currentFragment.isAdded()) {
                addNewFragmentWithOutHide(fragmentManager, currentFragment, container);
            } else {
                transaction.show(currentFragment).commit();
            }
        }
    }

    public void switchToNextFragmentByTag(FragmentManager fragmentManager,String currentTag,String targetTag){
        Fragment currentFragment = fragmentManager.findFragmentByTag(currentTag);
        Fragment targetFragment = fragmentManager.findFragmentByTag(targetTag);
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.hide(currentFragment);
        transaction.show(targetFragment).commit();
    }

}