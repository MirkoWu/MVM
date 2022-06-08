package com.mirkowu.lib_webview;

import android.content.Context;
import android.os.Build;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.KeyEvent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleOwner;

import com.mirkowu.lib_webview.dsbridge.DSBridgeImp;
import com.mirkowu.lib_webview.dsbridge.IDSBridge;
import com.mirkowu.lib_webview.dsbridge.JavascriptCloseWindowListener;
import com.mirkowu.lib_webview.dsbridge.OnReturnValue;
import com.mirkowu.lib_webview.settings.WebSettingsUtil;
import com.mirkowu.lib_webview.util.WebViewUtil;
import com.tencent.smtt.sdk.ValueCallback;
import com.tencent.smtt.sdk.WebView;

import java.util.Map;


/**
 * 1.支持腾讯X5浏览器
 * 2.支持jsBridge
 */
public class CommonWebView extends WebView implements IWebViewLifecycle, IDSBridge {
    private static final String TAG = CommonWebView.class.getSimpleName();
    private Map<String, String> mHeaders;
    private DSBridgeImp mBridgeDelegate; //BridgeWebView代理

    public CommonWebView(@NonNull Context context) {
        this(context, null);
    }

    public CommonWebView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CommonWebView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        mBridgeDelegate = new DSBridgeImp(this);
        WebSettingsUtil.configDefaultSettings(this);
    }

    /**
     * 设置请求头
     *
     * @param mHeaders
     */
    public void setHeaders(Map<String, String> mHeaders) {
        this.mHeaders = mHeaders;
    }

    @Override
    public void loadUrl(String url) {
        if (TextUtils.isEmpty(url)) {
            return;
        }
        if (mHeaders == null) {
            super.loadUrl(url);
        } else {
            super.loadUrl(url, mHeaders);
        }
    }

    /**
     * 单步后退
     */
    public void goBacks() {
        if (canGoBack()) {
            goBack();
        }
    }

    /**
     * 单步前进
     */
    public void goForwards() {
        if (canGoForward()) {
            goForward();
        }
    }


    /**
     * 前进后退统一接口及设置跨度
     *
     * @param index -1表示后退，1表示前进
     */
    @Override
    public void goBackOrForward(int index) {
        super.goBackOrForward(index);
    }

    /**
     * 前进后退统一接口及设置跨度
     *
     * @param index -1表示后退，1表示前进
     */
    public void goBackOrForwards(int index) {
        goBackOrForward(index);
    }

    /**
     * 保存网页
     *
     * @param filename
     */
    @Override
    public void saveWebArchive(String filename) {
        super.saveWebArchive(filename);
    }

    /**
     * 保存网页
     *
     * @param basename
     * @param autoName
     * @param valueCallback
     */
    @Override
    public void saveWebArchive(String basename, boolean autoName, ValueCallback<String> valueCallback) {
        super.saveWebArchive(basename, autoName, valueCallback);
    }

    public boolean onBackHandle() {
        if (canGoBack()) {
            goBack();
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            return onBackHandle();
        }
        return false;
    }

    @Override
    public void onAnyEvent(LifecycleOwner owner, Lifecycle.Event event) {

    }

    @Override
    public void onCreateEvent() {
    }

    @Override
    public void onStartEvent() {
    }

    @Override
    public void onStopEvent() {
    }

    @Override
    public void onResumeEvent() {
        WebViewUtil.onResume(this);
    }

    @Override
    public void onPauseEvent() {
        WebViewUtil.onPause(this);
    }

    @Override
    public void onDestroyEvent() {
        WebViewUtil.clearWebView(this);
        if (mBridgeDelegate != null) {
            mBridgeDelegate.onDestroy();
            mBridgeDelegate = null;
        }
    }


    /*** >>>>>>>>>>>>>>>>>>> jsBridge 相关方法 >>>>>>>>>>>>>>>>>>> */


    public void addJavascriptObject(Object object) {
        addJavascriptObject(object, null);
    }

    @Override
    public void addJavascriptObject(Object object, String namespace) {
        if (mBridgeDelegate != null) {
            mBridgeDelegate.addJavascriptObject(object, namespace);
        }
    }

    @Override
    public void removeJavascriptObject(String namespace) {
        if (mBridgeDelegate != null) {
            mBridgeDelegate.removeJavascriptObject(namespace);
        }
    }

    @Override
    public void removeAllJavascriptObject() {
        if (mBridgeDelegate != null) {
            mBridgeDelegate.removeAllJavascriptObject();
        }
    }

    @Override
    public <T> void callHandler(String method, Object[] args, OnReturnValue<T> handler) {
        if (mBridgeDelegate != null) {
            mBridgeDelegate.callHandler(method, args, handler);
        }
    }

    @Override
    public void callHandler(String method, Object[] args) {
        if (mBridgeDelegate != null) {
            mBridgeDelegate.callHandler(method, args);
        }
    }

    @Override
    public <T> void callHandler(String method, OnReturnValue<T> handler) {
        if (mBridgeDelegate != null) {
            mBridgeDelegate.callHandler(method, handler);
        }
    }

    @Override
    public void hasJavascriptMethod(String handlerName, OnReturnValue<Boolean> existCallback) {
        if (mBridgeDelegate != null) {
            mBridgeDelegate.hasJavascriptMethod(handlerName, existCallback);
        }
    }

    @Override
    public void clearCache(boolean includeDiskFiles) {
        super.clearCache(includeDiskFiles);
        WebSettingsUtil.clearCache(includeDiskFiles);
    }

    @Override
    public void setJavascriptCloseWindowListener(JavascriptCloseWindowListener listener) {
        if (mBridgeDelegate != null) {
            mBridgeDelegate.setJavascriptCloseWindowListener(listener);
        }
    }

    @Override
    public void evaluateJavascript(String script) {
        if (mBridgeDelegate != null) {
            mBridgeDelegate.evaluateJavascript(script);
        }
    }

    @Override
    public void disableJavascriptDialogBlock(boolean disable) {
        if (mBridgeDelegate != null) {
            mBridgeDelegate.disableJavascriptDialogBlock(disable);
        }
    }

    public static void setWebContentsDebuggingEnabled(boolean enabled) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            DSBridgeImp.setWebContentsDebuggingEnabled(enabled);
        }
    }
    /*** <<<<<<<<<<<<<<<<<<<<<<< jsBridge 相关方法 <<<<<<<<<<<<<<<<<<<<<<< */
}
