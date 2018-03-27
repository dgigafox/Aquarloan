package com.aquarloan.aquarloan;

import android.app.Application;

import com.github.omadahealth.lollipin.lib.managers.LockManager;

/**
 * Created by Darren Gegantino on 3/27/2018.
 */

public class CustomApplication extends Application {
    @SuppressWarnings("unchecked")
    @Override
    public void onCreate() {
        super.onCreate();

        LockManager<CustomPinActivity> lockManager = LockManager.getInstance();
        lockManager.enableAppLock(this, CustomPinActivity.class);
        lockManager.getAppLock().setLogoId(R.drawable.ic_pin_lock);
        lockManager.getAppLock().setShouldShowForgot(false);
    }
}
