package com.mirkowu.lib_webview.callback;

import android.content.Intent;
import android.net.Uri;

import com.mirkowu.lib_webview.CommonWebView;
import com.mirkowu.lib_webview.client.BaseWebViewClient;
import com.tencent.smtt.sdk.ValueCallback;
import com.tencent.smtt.sdk.WebChromeClient;

/**
 * WebView回调统一处理
 * 所有涉及到WebView交互的都必须实现这个callback
 */
public interface IWebViewCallBack {
    boolean onShowFileChooser(CommonWebView webView, ValueCallback<Uri[]> filePathCallback, WebChromeClient.FileChooserParams fileChooserParams);

    void pageStarted(CommonWebView webView, String url);

    void pageFinished(CommonWebView webView, String url);

    void onReceivedError(BaseWebViewClient client, CommonWebView view, int errorCode, String description, String failingUrl);

    boolean shouldOverrideUrlLoading(CommonWebView webView, String url);

    <T> T jsCallAndroid(String action, Object content, Class<T> t);

    void onActivityResult(CommonWebView webView, int requestCode, int resultCode, Intent data);
}
