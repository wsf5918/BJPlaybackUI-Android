package com.baijia.playbackui.activity;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by wangkangfei on 17/8/17.
 */

public class PBBaseActivity extends AppCompatActivity {

    protected void addFragment(int layoutId, Fragment fragment) {
        addFragment(layoutId, fragment, false, null);
    }

    protected void addFragment(int layoutId, Fragment fragment, boolean addToBackStack, String fragmentTag) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        if (fragmentTag == null) {
            transaction.add(layoutId, fragment);
        } else {
            transaction.add(layoutId, fragment, fragmentTag);
        }
        transaction.commitAllowingStateLoss();
    }
}
