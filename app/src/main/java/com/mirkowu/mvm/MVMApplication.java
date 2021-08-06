package com.mirkowu.mvm;

import android.app.Application;

import com.mirkowu.lib_bugly.UpgradeManager;
import com.mirkowu.lib_screen.AutoSizeManager;
import com.mirkowu.lib_stat.UmengManager;
import com.mirkowu.lib_util.LogUtil;
import com.mirkowu.lib_util.utilcode.util.ProcessUtils;
import com.mirkowu.lib_webview.util.WebViewUtil;

import io.reactivex.rxjava3.functions.Consumer;
import io.reactivex.rxjava3.plugins.RxJavaPlugins;

public class MVMApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        // LeakCanary.INSTANCE(this);
        LogUtil.init(BuildConfig.DEBUG);

        //WebView初始化多进程
        WebViewUtil.initMultiProcess(this);

        //换成你自己的bugly账号
        UpgradeManager.init(this, "3e2cd9bf87", BuildConfig.DEBUG);

        /**
         * 防止初始化多次，视项目情况设置
         * 需要多进程初始化的方法此方法前面 不需要多进程处理的放在后面
         */
        if (!ProcessUtils.isMainProcess()) {
            return;
        }

        //umeng
        UmengManager.preInit(this, "60d310388a102159db787693", "umeng", BuildConfig.DEBUG);
        UmengManager.init(this, null);


        //屏幕适配
        AutoSizeManager.init(this);

        //RxJava2 取消订阅后，抛出的异常无法捕获，导致程序崩溃
        RxJavaPlugins.setErrorHandler(new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) throws Throwable {
                LogUtil.e(throwable, "RxJavaPlugins");
            }
        });
    }

}
