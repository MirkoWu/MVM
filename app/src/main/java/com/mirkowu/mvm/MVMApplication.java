package com.mirkowu.mvm;

import android.app.Application;

import com.mirkowu.lib_util.LogUtil;
import com.mirkowu.lib_webview.util.WebViewUtil;
import com.mirkowu.mvm.manager.autosize.AutoSizeManager;

public class MVMApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        // LeakCanary.INSTANCE(this);

        WebViewUtil.init(this);
        LogUtil.init(BuildConfig.DEBUG);
        AutoSizeManager.getInstance().setConfig();

    }
}
