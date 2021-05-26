package com.mirkowu.lib_webview.callback;

import android.content.Intent;
import android.net.Uri;

import com.mirkowu.lib_webview.CommonWebView;
import com.tencent.smtt.sdk.ValueCallback;
import com.tencent.smtt.sdk.WebChromeClient;

/**
 * WebView回调统一处理
 * 所有涉及到WebView交互的都必须实现这个callback
 */
public interface IWebViewFileChooser {
    boolean onShowFileChooser(CommonWebView webView, ValueCallback<Uri[]> filePathCallback, WebChromeClient.FileChooserParams fileChooserParams);
    void onActivityResult(CommonWebView webView, int requestCode, int resultCode, Intent data);
}
