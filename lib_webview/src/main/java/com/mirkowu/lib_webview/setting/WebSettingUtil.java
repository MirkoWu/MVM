package com.mirkowu.lib_webview.setting;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.text.TextUtils;

import com.mirkowu.lib_util.LogUtil;
import com.mirkowu.lib_webview.BuildConfig;
import com.tencent.smtt.sdk.WebSettings;
import com.tencent.smtt.sdk.WebView;


/**
 * webview相关设置功能开关 在这里统一配置。
 */
public class WebSettingUtil {
    private static final int TEXT_ROOM = 100;
    private static final int DEFAULT_FONT_SIZE = 16;
    private static final int MINI_MUM_FONT_SIZE = 10;
    private static final int APP_CACHE = 1024 * 1024 * 100;
    private static final String TAG = WebSettingUtil.class.getSimpleName();


    @SuppressLint("SetJavaScriptEnabled")
    public static void toSetting(WebView webView, String userAgent) {
        WebSettings mWebSettings = webView.getSettings();
        mWebSettings.setJavaScriptEnabled(true); //告诉WebView启用JavaScript执行。
        mWebSettings.setSupportZoom(true); //设置WebView是否应该使用其屏幕上的缩放控件和手势支持缩放。
        mWebSettings.setBuiltInZoomControls(false); //设置WebView是否应该使用其内置的缩放机制
        mWebSettings.setTextZoom(TEXT_ROOM);  // textZoom:100表示正常，120表示文字放大1.2倍 文字大小

        if (isNetworkConnected(webView.getContext())) {
            mWebSettings.setCacheMode(WebSettings.LOAD_DEFAULT); //覆盖缓存的使用方式。默认缓存模式
        } else {
            mWebSettings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);  //覆盖缓存的使用方式。网络缓存模式
        }

        userAgent = TextUtils.isEmpty(userAgent) ? mWebSettings.getUserAgentString() : userAgent + mWebSettings.getUserAgentString();
        LogUtil.e(TAG, "userAgent------------------=" + userAgent);
        mWebSettings.setUserAgentString(userAgent); //设置当前ua

        mWebSettings.setLoadsImagesAutomatically(true);  //有图：正常加载显示所有图片
        mWebSettings.setBlockNetworkImage(false);  //是否阻塞加载网络图片  协议http or https
        mWebSettings.setSupportMultipleWindows(false); //WebView是否支持多个窗口

        mWebSettings.setLoadWithOverviewMode(true); //设置WebView是否以概览模式加载页面，也就是说，缩放内容以适应屏幕的宽度
        mWebSettings.setUseWideViewPort(true); //设置WebView是否应该启用支持“viewport”HTML元标签或应该使用一个宽的viewport

        mWebSettings.setDatabaseEnabled(true); //设置是否启用数据库存储API。
        mWebSettings.setAppCacheEnabled(true); //设置是否应该启用应用程序缓存API。
        mWebSettings.setAllowFileAccess(true); //允许加载本地文件html  file协议
        mWebSettings.setAllowFileAccessFromFileURLs(false); //通过 file url 加载的 Javascript 读取其他的本地文件 .建议关闭
        mWebSettings.setSavePassword(false); //关闭密码保存
        mWebSettings.setSaveFormData(false); //设置WebView是否应该保存表单数据
        mWebSettings.setDomStorageEnabled(true); //设置是否启用DOM存储API
        mWebSettings.setGeolocationEnabled(true); //设置是否启用地理位置。内核默认是开启的

        mWebSettings.setAllowUniversalAccessFromFileURLs(false); //允许混合加载 允许通过 file url 加载的 Javascript 可以访问其他的源，包括其他的文件和 http，https 等其他的源
        mWebSettings.setJavaScriptCanOpenWindowsAutomatically(true); //JS自动弹窗提示 在非用户操作情况下利用window.open打开窗口被称为自动弹窗，该功能默认关闭。
        mWebSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);

        mWebSettings.setNeedInitialFocus(true); //当WebView#requestFocus(int, android.graphics.Rect)被调用时，告诉WebView是否需要设置一个节点来具有焦点
        mWebSettings.setDefaultTextEncodingName("utf-8"); //设置解码html页面时使用的默认文本编码名称
        mWebSettings.setDefaultFontSize(DEFAULT_FONT_SIZE); //设置 WebView 支持的默认字体大小
        mWebSettings.setMinimumFontSize(MINI_MUM_FONT_SIZE); //设置 WebView 支持的最小字体大小，默认为 8

        //5.0以上允许加载http和https混合的页面(5.0以下默认允许，5.0+默认禁止)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mWebSettings.setMixedContentMode(android.webkit.WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }
        String cacheDir = webView.getContext().getDir("cache", Context.MODE_PRIVATE).getPath();
        LogUtil.e(TAG, "WebView cache dir =" + cacheDir);
        //设置应用程序缓存文件的路径。
        mWebSettings.setDatabasePath(cacheDir);
        mWebSettings.setAppCachePath(cacheDir);
        mWebSettings.setAppCacheMaxSize(APP_CACHE);
        //是否开发调试模式
        WebView.setWebContentsDebuggingEnabled(BuildConfig.DEBUG);
    }

    private static boolean isNetworkConnected(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = cm.getActiveNetworkInfo();
        if (activeNetworkInfo != null) {
            boolean connected = activeNetworkInfo.isConnected();
            return connected;
        } else {
            return false;
        }

    }
}
