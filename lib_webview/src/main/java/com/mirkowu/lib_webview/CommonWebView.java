package com.mirkowu.lib_webview;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.KeyEvent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleOwner;

import com.mirkowu.lib_util.LogUtil;
import com.mirkowu.lib_webview.callback.IWebViewCallBack;
import com.mirkowu.lib_webview.jsbridge.BridgeHandler;
import com.mirkowu.lib_webview.jsbridge.BridgeWebViewDelegate;
import com.mirkowu.lib_webview.jsbridge.CallBackFunction;
import com.mirkowu.lib_webview.jsbridge.JSCallNativeBridge;
import com.mirkowu.lib_webview.jsbridge.WebViewJavascriptBridge;
import com.mirkowu.lib_webview.util.WebViewUtil;
import com.tencent.smtt.sdk.ValueCallback;
import com.tencent.smtt.sdk.WebView;

import java.util.Map;


/**
 * 1.支持腾讯X5浏览器
 * 2.支持jsBridge
 */
public class CommonWebView extends WebView implements IWebViewDelegate, WebViewJavascriptBridge {
    private static final String TAG = CommonWebView.class.getSimpleName();
    private Map<String, String> mHeaders;
    private String[] mJsObjectNameArrays;
    //BridgeWebView代理
    private BridgeWebViewDelegate mBridgeWebViewDelegate;

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
        mBridgeWebViewDelegate = new BridgeWebViewDelegate(this);
    }

    /**
     * 调用js代码
     *
     * @param methodName    js方法名
     * @param params        方法参数
     * @param valueCallback js回调
     */
    public void loadJs(@NonNull String methodName, @Nullable String params, @Nullable ValueCallback<String> valueCallback) {
        String jsCode;
        if (params == null) {
            jsCode = String.format("javascript:%s()", methodName);
        } else {
            jsCode = String.format("javascript:%s(%s)", methodName, params);
        }
        LogUtil.e(TAG, jsCode);
        evaluateJavascript(jsCode, valueCallback);
    }

    /**
     * 注入js对象
     *
     * @param callBack
     * @param jsObjectNameArrays
     */
    public void addJavascriptInterface(@NonNull IWebViewCallBack callBack, @NonNull String... jsObjectNameArrays) {
        if (callBack == null || jsObjectNameArrays == null || jsObjectNameArrays.length == 0) {
            return;
        }
        JSCallNativeBridge jsCallNativeBridge = new JSCallNativeBridge(callBack);
        addJavascriptInterface(jsCallNativeBridge, jsObjectNameArrays);
    }

    public void addJavascriptInterface(@NonNull JSCallNativeBridge jsCallNativeBridge, @NonNull String... jsObjectNameArrays) {
        if (jsCallNativeBridge == null || jsObjectNameArrays == null || jsObjectNameArrays.length == 0) {
            return;
        }
        mJsObjectNameArrays = jsObjectNameArrays;
        for (String jsObjectName : jsObjectNameArrays) {
            addJavascriptInterface(jsCallNativeBridge, jsObjectName);
        }
    }

    /**
     * 移除所有的Js注入
     */
    public void removeAllJavascriptInterface() {
        if (mJsObjectNameArrays == null || mJsObjectNameArrays.length == 0) {
            return;
        }
        for (String jsObjectName : mJsObjectNameArrays) {
            removeJavascriptInterface(jsObjectName);
        }
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
    }

    /*** >>>>>>>>>>>>>>>>>>> jsBridge 相关方法 >>>>>>>>>>>>>>>>>>> */
    @Override
    public void send(String data) {
        if (mBridgeWebViewDelegate != null) {
            mBridgeWebViewDelegate.send(data);
        }
    }

    @Override
    public void send(String data, CallBackFunction responseCallback) {
        if (mBridgeWebViewDelegate != null) {
            mBridgeWebViewDelegate.send(data, responseCallback);
        }
    }

    @Override
    public void registerHandler(String handlerName, BridgeHandler handler) {
        if (mBridgeWebViewDelegate != null) {
            mBridgeWebViewDelegate.registerHandler(handlerName, handler);
        }
    }

    @Override
    public void callHandler(String handlerName, String data, CallBackFunction callBack) {
        if (mBridgeWebViewDelegate != null) {
            mBridgeWebViewDelegate.callHandler(handlerName, data, callBack);
        }
    }

    @Override
    public void handlerReturnData(String url) {
        if (mBridgeWebViewDelegate != null) {
            mBridgeWebViewDelegate.handlerReturnData(url);
        }
    }

    @Override
    public void flushMessageQueue() {
        if (mBridgeWebViewDelegate != null) {
            mBridgeWebViewDelegate.flushMessageQueue();
        }
    }

    @Override
    public void loadLocalJS() {
        if (mBridgeWebViewDelegate != null) {
            mBridgeWebViewDelegate.loadLocalJS();
        }
    }

    /*** <<<<<<<<<<<<<<<<<<<<<<< jsBridge 相关方法 <<<<<<<<<<<<<<<<<<<<<<< */
}
