package com.mirkowu.mvm;

import android.app.Application;

import com.mirkowu.lib_util.LogUtil;
import com.mirkowu.lib_webview.util.WebViewUtil;

public class MVMApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
       // LeakCanary.INSTANCE(this);

        WebViewUtil.init(this);
        LogUtil.init(BuildConfig.DEBUG);
    }
}
