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
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .commit();
    }

    public void switchToNextFragment(FragmentManager fragmentManager,Fragment currentFragment,
                                     Fragment nextFragment, int container) {
        if (currentFragment == null) {
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            if (!nextFragment.isAdded()) {
                addNewFragmentWithOutHide(fragmentManager, nextFragment, container);
            } else {
                transaction.show(nextFragment).setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN).commit();
            }
        }else if (currentFragment != nextFragment) {
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction.hide(currentFragment);
            if (!nextFragment.isAdded()) {
                addNewFragmentWithOutHide(fragmentManager, nextFragment, container);
            } else {
                transaction.show(nextFragment).setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN).commit();
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

    public void switchToPreFragment(FragmentManager fragmentManager,Fragment currentFragment,
                                     Fragment preFragment, int container) {
        if (currentFragment==null){
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            if (!preFragment.isAdded()) {
                addNewFragmentWithOutHide(fragmentManager, preFragment, container);
            } else {
                transaction.show(preFragment).setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE).commit();
            }
        }else if (currentFragment!=preFragment){
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction.hide(currentFragment);
            if (!preFragment.isAdded()) {
                addNewFragmentWithOutHide(fragmentManager, preFragment, container);
            } else {
                transaction.show(preFragment).setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE).commit();
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

    public void switchToPreFragmentByTag(FragmentManager fragmentManager,String currentTag,String preTag){
        Fragment currentFragment = fragmentManager.findFragmentByTag(currentTag);
        Fragment preFragment = fragmentManager.findFragmentByTag(preTag);
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.hide(currentFragment);
        transaction.show(preFragment).setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE).commit();
    }

    public void hideFragmentByTag(FragmentManager fragmentManager,String tag){
        Fragment currentFragment = fragmentManager.findFragmentByTag(tag);
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.hide(currentFragment).commit();
    }

    public void switchToNextFragmentByTag(FragmentManager fragmentManager,String currentTag,String targetTag){
        Fragment currentFragment = fragmentManager.findFragmentByTag(currentTag);
        Fragment targetFragment = fragmentManager.findFragmentByTag(targetTag);
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.hide(currentFragment);
        transaction.show(targetFragment).setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN).commit();
    }

}
