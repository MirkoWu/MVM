package com.mirkowu.lib_webview.settings;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.text.TextUtils;

import androidx.annotation.NonNull;

import com.mirkowu.lib_util.LogUtil;
import com.mirkowu.lib_util.utilcode.util.Utils;
import com.tencent.smtt.sdk.CookieManager;
import com.tencent.smtt.sdk.WebSettings;
import com.tencent.smtt.sdk.WebView;

import java.io.File;


/**
 * webview相关设置功能开关 在这里统一配置。
 */
public class WebSettingsUtil {
    private static final int TEXT_ROOM = 100;
    private static final int DEFAULT_FONT_SIZE = 16;
    private static final int MINI_MUM_FONT_SIZE = 10;
    private static final int WEB_CACHE_SIZE = 1024 * 1024 * 100;
    private static final String TAG = WebSettingsUtil.class.getSimpleName();

    public static void setUserAgent(WebView webView, String userAgent) {
        WebSettings webSettings = webView.getSettings();
        webSettings.setUserAgentString(userAgent); //设置当前ua
        LogUtil.e(TAG, "userAgent ------------------=" + userAgent);
    }

    public static void appendUserAgent(WebView webView, String userAgent) {
        WebSettings webSettings = webView.getSettings();
        userAgent = TextUtils.isEmpty(userAgent) ? webSettings.getUserAgentString() : (userAgent + webSettings.getUserAgentString());
        LogUtil.e(TAG, "userAgent ------------------=" + userAgent);
        webSettings.setUserAgentString(userAgent); //设置当前ua
    }

    @SuppressLint("SetJavaScriptEnabled")
    public static void configDefaultSettings(WebView webView) {
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true); //告诉WebView启用JavaScript执行。
        webSettings.setSupportZoom(true); //设置WebView是否应该使用其屏幕上的缩放控件和手势支持缩放。
        webSettings.setBuiltInZoomControls(false); //设置WebView是否应该使用其内置的缩放机制
        webSettings.setTextZoom(TEXT_ROOM);  // textZoom:100表示正常，120表示文字放大1.2倍 文字大小

        if (isNetworkConnected(webView.getContext())) {
            webSettings.setCacheMode(WebSettings.LOAD_DEFAULT); //覆盖缓存的使用方式。默认缓存模式
        } else {
            webSettings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);  //覆盖缓存的使用方式。网络缓存模式
        }

        webSettings.setLoadsImagesAutomatically(true);  //有图：正常加载显示所有图片
        webSettings.setBlockNetworkImage(false);  //是否阻塞加载网络图片  协议http or https
        webSettings.setSupportMultipleWindows(true); //WebView是否支持多个窗口 true时必须重写WebChrome.onCreateWindow

        webSettings.setLoadWithOverviewMode(true); //设置WebView是否以概览模式加载页面，也就是说，缩放内容以适应屏幕的宽度
        webSettings.setUseWideViewPort(true); //设置WebView是否应该启用支持“viewport”HTML元标签或应该使用一个宽的viewport

        webSettings.setDatabaseEnabled(true); //设置是否启用数据库存储API。
        webSettings.setAppCacheEnabled(true); //设置是否应该启用应用程序缓存API。
        webSettings.setAllowFileAccess(true); //允许加载本地文件html  file协议
        webSettings.setAllowFileAccessFromFileURLs(false); //通过 file url 加载的 Javascript 读取其他的本地文件 .建议关闭
        webSettings.setSavePassword(false); //关闭密码保存
        webSettings.setSaveFormData(false); //设置WebView是否应该保存表单数据
        webSettings.setDomStorageEnabled(true); //设置是否启用DOM存储API
        webSettings.setGeolocationEnabled(true); //设置是否启用地理位置。内核默认是开启的

        webSettings.setAllowUniversalAccessFromFileURLs(false); //允许混合加载 允许通过 file url 加载的 Javascript 可以访问其他的源，包括其他的文件和 http，https 等其他的源
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true); //JS自动弹窗提示 在非用户操作情况下利用window.open打开窗口被称为自动弹窗，该功能默认关闭。
        webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        webSettings.setMediaPlaybackRequiresUserGesture(false); //支持网页自动播放功能

        webSettings.setNeedInitialFocus(true); //当WebView#requestFocus(int, android.graphics.Rect)被调用时，告诉WebView是否需要设置一个节点来具有焦点
        webSettings.setDefaultTextEncodingName("utf-8"); //设置解码html页面时使用的默认文本编码名称
        webSettings.setDefaultFontSize(DEFAULT_FONT_SIZE); //设置 WebView 支持的默认字体大小
        webSettings.setMinimumFontSize(MINI_MUM_FONT_SIZE); //设置 WebView 支持的最小字体大小，默认为 8

        //5.0以上允许加载http和https混合的页面(5.0以下默认允许，5.0+默认禁止)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            CookieManager.getInstance().setAcceptThirdPartyCookies(webView, true);
            webSettings.setMixedContentMode(android.webkit.WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }
        String cacheDir = getCachePath();
        LogUtil.e(TAG, "WebView cache dir =" + cacheDir);
        //设置应用程序缓存文件的路径。
        webSettings.setDatabasePath(cacheDir);
        webSettings.setAppCachePath(cacheDir);
        webSettings.setAppCacheMaxSize(WEB_CACHE_SIZE);
    }


    private static boolean isNetworkConnected(@NonNull Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = cm.getActiveNetworkInfo();
        if (activeNetworkInfo != null) {
            boolean connected = activeNetworkInfo.isConnected();
            return connected;
        } else {
            return false;
        }

    }

    public static String getCachePath() {
        return Utils.getApp().getDir("cache", Context.MODE_PRIVATE).getPath();
    }

    public static void clearCache(boolean includeDiskFiles) {
        CookieManager.getInstance().removeAllCookie();
        Context context = Utils.getApp();
        try {
            context.deleteDatabase("webview.db");
            context.deleteDatabase("webviewCache.db");
        } catch (Exception e) {
            e.printStackTrace();
        }
        String cacheDir = getCachePath();
        File appCacheDir = new File(cacheDir);
        File webviewCacheDir = new File(cacheDir);

        if (webviewCacheDir.exists()) {
            deleteFile(webviewCacheDir);
        }

        if (appCacheDir.exists()) {
            deleteFile(appCacheDir);
        }
    }

    public static void deleteFile(File file) {
        try {
            if (file.exists()) {
                if (file.isFile()) {
                    file.delete();
                } else if (file.isDirectory()) {
                    File files[] = file.listFiles();
                    for (int i = 0; i < files.length; i++) {
                        deleteFile(files[i]);
                    }
                }
                file.delete();
            }
        } catch (Throwable e) {
        }
    }
}
