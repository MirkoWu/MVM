package com.mirkowu.lib_webview.util;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Looper;
import android.text.TextUtils;
import android.view.ViewGroup;

import com.mirkowu.lib_util.LogUtil;
import com.mirkowu.lib_util.utilcode.util.ProcessUtils;
import com.mirkowu.lib_webview.CommonWebView;
import com.mirkowu.lib_webview.R;
import com.mirkowu.lib_webview.service.EmptyService;
import com.tencent.smtt.export.external.TbsCoreSettings;
import com.tencent.smtt.sdk.CookieManager;
import com.tencent.smtt.sdk.GeolocationPermissions;
import com.tencent.smtt.sdk.QbSdk;
import com.tencent.smtt.sdk.WebIconDatabase;
import com.tencent.smtt.sdk.WebStorage;
import com.tencent.smtt.sdk.WebView;
import com.tencent.smtt.sdk.WebViewDatabase;

import java.util.HashMap;

/**
 * 这个处理WebView初始化、cookie同步、删除缓存清除等。
 */
public class WebViewUtil {
    private static boolean sUseMultiProcess = false; //该静态参数，可在多进程模式下使用，因为会赋值

    /**
     * 初始化WebView 保证只初始化一次
     * 建议在onCreate 中调用
     *
     * @param application
     */
    public static void init(Application application) {
        init(application, false);
    }

    /**
     * 初始化WebView 保证只初始化一次
     * 建议在onCreate 中调用
     * <p>
     * 如果要使用多进程 则需要调用此方法初始化进程，否则第一次打开会白屏一会
     *
     * @param application
     * @param useMultiProcess 是否使用多进程
     */
    public static void init(Application application, boolean useMultiProcess) {
        String mainProcessName = application.getPackageName();
        init(application, useMultiProcess, mainProcessName + application.getString(R.string.webview_process));
    }

    /**
     * 可自定义进程名称
     *
     * @param application
     * @param useMultiProcess 是否使用多进程
     * @param processName     指定进程名  默认: 包名:webview ,如需自定义，记得在AndroidManifest.xlm中注册process
     */
    public static void init(Application application, boolean useMultiProcess, String processName) {
        sUseMultiProcess = useMultiProcess;
        configWebViewCacheDirWithAndroidP(application);
        String curProcessName = ProcessUtils.getCurrentProcessName();
        String mainProcessName = application.getPackageName();
        boolean isMainProcess = TextUtils.equals(curProcessName, mainProcessName);
        if (useMultiProcess && TextUtils.equals(curProcessName, processName)) { //web进程初始化
            sUseMultiProcess = true;
            initX5Web(application);
        }
        if (isMainProcess) { //主进程初始化
            initX5Web(application);
        }
        if (isMainProcess && useMultiProcess) {
            startMultiProcess(application);
        }
    }

    /**
     * 获取是否使用了多进程
     *
     * @return
     */
    public static boolean getUseMultiProcess() {
        return sUseMultiProcess;
    }

    /**
     * 初始化X5浏览器
     *
     * @param application
     */
    private static void initX5Web(Application application) {
        //首次初始化冷启动优化  在调用TBS初始化、创建WebView之前进行如下配置
        HashMap map = new HashMap();
        map.put(TbsCoreSettings.TBS_SETTINGS_USE_SPEEDY_CLASSLOADER, true);
        map.put(TbsCoreSettings.TBS_SETTINGS_USE_DEXLOADER_SERVICE, true);
        QbSdk.initTbsSettings(map);

        //屏蔽X5频繁收集手机敏感信息数据 imsi 和 imei
        QbSdk.disableSensitiveApi();
        //初始化环境
        QbSdk.initX5Environment(application.getApplicationContext(), null);
    }

    /**
     * 提前创建新的进程，防止WebView第一次启动时白屏
     *
     * @param context
     */
    private static void startMultiProcess(Context context) {
        try {
            Intent intent = new Intent(context, EmptyService.class);
            context.startService(intent);
        } catch (Throwable e) {
            LogUtil.e("startMultiProcess  try", e);
            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    context.startForegroundService(new Intent(context, EmptyService.class));
                }
            } catch (Throwable e1) {
                LogUtil.e("startMultiProcess failed", e1);
            }
        }
    }

    private static void configWebViewCacheDirWithAndroidP(Application application) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            String processName = ProcessUtils.getCurrentProcessName();
            String appPackageName = application.getPackageName();
            if (!TextUtils.equals(appPackageName, processName)) {
                WebView.setDataDirectorySuffix(processName);
                LogUtil.d(String.format("WebView 主进程=%s 当前多进程=%s", appPackageName, processName));
            }
        }
    }

    /**
     * 清除缓存
     *
     * @param context 上下文
     */
    public static void clearAllCache(Context context) {
        if (context == null) {
            return;
        }
        Context applicationContext = context.getApplicationContext();
        //清除cookie
        CookieManager.getInstance().removeAllCookies(null);
        //清除storage相关缓存
        WebStorage.getInstance().deleteAllData();
        //清除用户密码信息
        WebViewDatabase.getInstance(applicationContext).clearUsernamePassword();
        //清除httpauth信息
        WebViewDatabase.getInstance(applicationContext).clearHttpAuthUsernamePassword();
        //清除表单数据
        WebViewDatabase.getInstance(applicationContext).clearFormData();
        //清除页面icon图标信息
        WebIconDatabase.getInstance().removeAllIcons();
        //删除地理位置授权，也可以删除某个域名的授权（参考接口类）
        GeolocationPermissions.getInstance().clearAll();

        //X5浏览器 一次性删除所有缓存
        QbSdk.clearAllWebViewCache(applicationContext, true);
    }
//
//    /**
//     * 将cookie同步到WebView
//     *
//     * @param url WebView要加载的url
//     * @param map 具体cookie参数集合
//     * @return true 同步cookie成功，false同步cookie失败
//     */
//    public static void syncCookie(String url, final Map<String, String> map) {
//        for (String key : map.keySet()) {
//            setCookie(url, key, map.get(key));
//        }
//    }
//
//    public static void setCookie(String url, String key, final String value) {
//        if (TextUtils.isEmpty(url)) {
//            url = DOMAIN_URL;
//        }
//        String finalUrl = url;
//        ThreadUtils.executeByIo(new ThreadUtils.SimpleTask<Object>() {
//            @Override
//            public Object doInBackground() throws Throwable {
//                CookieManager cookieManager = CookieManager.getInstance();
//                cookieManager.setCookie(finalUrl, key + "=" + value);
//                cookieManager.setAcceptCookie(true);
//                cookieManager.flush();
//                return null;
//            }
//
//            @Override
//            public void onSuccess(Object result) {
//
//            }
//        });
//
//    }

    public static void onResume(CommonWebView webView) {
        if (webView == null) {
            return;
        }
        webView.onResume();
    }

    public static void onPause(CommonWebView webView) {
        if (webView == null) {
            return;
        }
        webView.onPause();
    }

    public static void clearWebView(CommonWebView webView) {
        if (webView == null) {
            return;
        }
        if (Looper.myLooper() != Looper.getMainLooper()) {
            return;
        }
//        clearData(webView.getContext());
        webView.stopLoading();
        if (webView.getHandler() != null) {
            webView.getHandler().removeCallbacksAndMessages(null);
        }
        webView.removeAllViews();
        ViewGroup mViewGroup = null;
        if ((mViewGroup = ((ViewGroup) webView.getParent())) != null) {
            mViewGroup.removeView(webView);
        }
        webView.setWebChromeClient(null);
        webView.setWebViewClient(null);
        webView.setTag(null);
        webView.clearHistory();
        webView.removeAllJavascriptInterface();
        webView.destroy();
        webView = null;

        //请求回收
        Runtime.getRuntime().gc();
    }
}
