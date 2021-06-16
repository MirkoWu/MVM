package com.mirkowu.mvm;

import android.app.Application;

import com.mirkowu.lib_util.LogUtil;
import com.mirkowu.lib_util.utilcode.util.ProcessUtils;
import com.mirkowu.lib_webview.util.WebViewUtil;
import com.mirkowu.mvm.manager.autosize.AutoSizeManager;

import io.reactivex.rxjava3.functions.Consumer;
import io.reactivex.rxjava3.plugins.RxJavaPlugins;

public class MVMApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        // LeakCanary.INSTANCE(this);
        LogUtil.init(BuildConfig.DEBUG);
        WebViewUtil.initMultiProcess(this);
        if (!ProcessUtils.isMainProcess()) {
            return;
        }

        AutoSizeManager.getInstance().setConfig(this);


//        RxJava2 取消订阅后，抛出的异常无法捕获，导致程序崩溃
        RxJavaPlugins.setErrorHandler(new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) throws Throwable {
                LogUtil.e(throwable, "RxJavaPlugins");
            }
        });
    }

}
