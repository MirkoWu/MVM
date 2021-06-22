package com.mirkowu.mvm;

import android.app.Application;

import com.mirkowu.lib_upgrade.BuglyManager;
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

        //防止初始化多次，视项目情况设置
        if (!ProcessUtils.isMainProcess()) {
            return;
        }

        ////换成你自己的bugly账号
        BuglyManager.init(this, "3e2cd9bf87", BuildConfig.DEBUG);

        //屏幕适配
        AutoSizeManager.getInstance().setConfig(this);

        //RxJava2 取消订阅后，抛出的异常无法捕获，导致程序崩溃
        RxJavaPlugins.setErrorHandler(new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) throws Throwable {
                LogUtil.e(throwable, "RxJavaPlugins");
            }
        });
    }

}
