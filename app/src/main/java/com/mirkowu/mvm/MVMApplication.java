package com.mirkowu.mvm;

import android.app.Application;
import android.os.StrictMode;

import com.mirkowu.lib_bugly.BuglyManager;
import com.mirkowu.lib_crash.CrashManager;
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
        LogUtil.d("点击 跳过 Application onCreate");

        //WebView初始化多进程 耗时200ms左右,建议延迟初始化
        WebViewUtil.init(this, true);
        LogUtil.d("点击 跳过 WebViewUtil");
        //换成你自己的bugly账号
        BuglyManager.init(this, "3e2cd9bf87", BuildConfig.DEBUG);
        LogUtil.d("点击 跳过 BuglyManager");
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

        LogUtil.d("点击 跳过UmengManager");
        //屏幕适配
        AutoSizeManager.init(this, BuildConfig.DEBUG);
        LogUtil.d("点击 跳过 AutoSizeManager");
        //RxJava2 取消订阅后，抛出的异常无法捕获，导致程序崩溃
        RxJavaPlugins.setErrorHandler(new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) throws Throwable {
                LogUtil.e("RxJavaPlugins", throwable);
                CrashManager.reportError(throwable);
                UmengManager.reportError(throwable);
            }
        });

        LogUtil.d("点击 跳过 Application onCreate  end---");
    }

    /**
     * 开启严苛模式，当代码有违规操作时，可以通过Logcat或崩溃的方式提醒我们
     */
    private void startStrictMode() {
        if (BuildConfig.DEBUG) { //一定要在Debug模式下使用，避免在生产环境中发生不必要的崩溃和日志输出

            //线程检测策略
            StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                    .detectDiskReads()  //检测主线程磁盘读取操作
                    .detectDiskWrites() //检测主线程磁盘写入操作
                    .detectNetwork() //检测主线程网络请求操作
                    .penaltyLog() //违规操作以log形式输出
                    .penaltyFlashScreen()
                    .build());

            //虚拟机检测策略
            StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
                    .detectLeakedSqlLiteObjects() //检测SqlLite泄漏
                    .detectLeakedClosableObjects() //检测未关闭的closable对象泄漏
                    .penaltyDeath() //发生违规操作时，直接崩溃
                    .build());
        }
    }
}
