package com.baijia.playbackui.activity;

import android.os.Build;
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

    protected void removeFragment(Fragment fragment) {
        if (fragment == null) return;
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.remove(fragment);
        if (Build.VERSION.SDK_INT >= 24) {
            transaction.commitNowAllowingStateLoss();
        } else {
            transaction.commitAllowingStateLoss();
        }

    }
}
