package com.mirkowu.lib_webview.jsbridge;

import android.webkit.JavascriptInterface;

import androidx.annotation.NonNull;

import com.mirkowu.lib_util.LogUtil;
import com.mirkowu.lib_webview.callback.IWebViewCallBack;


/**
 * js调安卓的逻辑，统一在这个处理。
 */
public class JSCallNativeBridge {
    private static final String TAG = JSCallNativeBridge.class.getSimpleName();
    private IWebViewCallBack mIWebViewCallBack;

    public JSCallNativeBridge(@NonNull IWebViewCallBack callBack) {
        mIWebViewCallBack = callBack;
    }

    /**
     * js调用安卓代码
     *
     * @param action  具体js指令.也就是需要触发什么命令，需要移动端做什么。
     * @param content js 给移动端的具体请求参数。
     */
    @JavascriptInterface
    public void jsCallNative(final String action, final String content) {
        LogUtil.e(TAG, String.format("jsCallNative：action = %s ，content = %s", action, content));
        if (mIWebViewCallBack != null) {
            mIWebViewCallBack.jsCallNative(action, content, null);
        }
    }

}
