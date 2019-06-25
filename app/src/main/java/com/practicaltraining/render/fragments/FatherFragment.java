package com.practicaltraining.render.fragments;

import android.support.v4.app.Fragment;

import com.practicaltraining.render.callbacks.ChangeCurrentFragment;

public class FatherFragment extends Fragment {
    ChangeCurrentFragment changeCurrentFragment;

    public void setChangeCurrentFragment(ChangeCurrentFragment changeCurrentFragment) {
        this.changeCurrentFragment = changeCurrentFragment;
    }

}
