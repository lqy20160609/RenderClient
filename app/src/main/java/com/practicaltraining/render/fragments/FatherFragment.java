package com.practicaltraining.render.fragments;
/**
 * created By LQY
 * 2019.6.25
 * fragment基类，设置入场出场动画
 * 注册改变当前fragment变量回调
 */

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;

import com.practicaltraining.render.callbacks.ChangeCurrentFragment;
import com.practicaltraining.render.callbacks.CloseDrawer;

public class FatherFragment extends Fragment {
    ChangeCurrentFragment changeCurrentFragment;
    CloseDrawer closeDrawer;

    public void setCloseDrawer(CloseDrawer closeDrawer) {
        this.closeDrawer = closeDrawer;
    }

    public void setChangeCurrentFragment(ChangeCurrentFragment changeCurrentFragment) {
        this.changeCurrentFragment = changeCurrentFragment;
    }

    @Override
    public Animation onCreateAnimation(int transit, boolean enter, int nextAnim) {
        TranslateAnimation animation = null;
        if (transit == FragmentTransaction.TRANSIT_FRAGMENT_OPEN) {
            if (enter) {
                animation = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 1, Animation.RELATIVE_TO_SELF, 0,
                        Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 0);
            } else {
                animation = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, -1,
                        Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 0);
            }
        } else if (FragmentTransaction.TRANSIT_FRAGMENT_CLOSE == transit) {
            if (enter) {
                animation = new TranslateAnimation(Animation.RELATIVE_TO_SELF, -1, Animation.RELATIVE_TO_SELF, 0,
                        Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 0);
            } else {
                animation = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 1,
                        Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 0);
            }
        }
        if (animation == null) {
            animation = new TranslateAnimation(0, 0, 0, 0);
        }
        animation.setDuration(300);
        return animation;
    }
}
