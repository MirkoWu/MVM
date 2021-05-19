package com.mirkowu.mvm;

import android.app.Application;
import android.content.pm.ApplicationInfo;

import com.mirkowu.lib_util.LogUtil;
import com.orhanobut.logger.AndroidLogAdapter;
import com.orhanobut.logger.Logger;

import leakcanary.LeakCanary;

public class MVMApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
       // LeakCanary.INSTANCE(this);


        LogUtil.init(BuildConfig.DEBUG);
    }
}
