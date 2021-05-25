package com.mirkowu.lib_webview.util;

import android.content.Context;
import android.os.Looper;
import android.view.ViewGroup;

import com.mirkowu.lib_webview.CommonWebView;
import com.tencent.smtt.sdk.QbSdk;

/**
 * 这个处理webview初始化、cookie同步、删除缓存清除等。
 */
public class WebViewUtil {
    private static final String DOMAIN_URL = "xxx.xxx.com";

    public static void init(Context context) {
        QbSdk.initX5Environment(context.getApplicationContext(), null);
    }

//    /**
//     * 清除缓存
//     *
//     * @param context 上下文
//     */
//    public static void clearData(Context context) {
//        if (context == null) {
//            return;
//        }
//        Context applicationContext = context.getApplicationContext();
//        //清除cookie
//        CookieManager.getInstance().removeAllCookies(null);
//        //清除storage相关缓存
//        WebStorage.getInstance().deleteAllData();
//        //清除用户密码信息
//        WebViewDatabase.getInstance(applicationContext).clearUsernamePassword();
//        //清除httpauth信息
//        WebViewDatabase.getInstance(applicationContext).clearHttpAuthUsernamePassword();
//        //清除表单数据
//        WebViewDatabase.getInstance(applicationContext).clearFormData();
//        //清除页面icon图标信息
//        WebIconDatabase.getInstance().removeAllIcons();
//        //删除地理位置授权，也可以删除某个域名的授权（参考接口类）
//        GeolocationPermissions.getInstance().clearAll();
//
//        //一次性删除所有缓存
////        QbSdk.clearAllWebViewCache(applicationContext, true);
//    }
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
    }
}
