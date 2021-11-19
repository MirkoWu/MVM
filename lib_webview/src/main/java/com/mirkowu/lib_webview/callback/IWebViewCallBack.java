package com.mirkowu.lib_webview.callback;

import com.mirkowu.lib_webview.CommonWebView;

/**
 * WebView回调统一处理
 * 所有涉及到WebView交互的都必须实现这个callback
 */
public interface IWebViewCallBack {

    void pageStarted(CommonWebView webView, String url);

    void pageFinished(CommonWebView webView, String url);

    void onProgressChanged(CommonWebView webView, int newProgress);

    void onReceivedTitle(CommonWebView webView, String title);

    void onReceivedError(CommonWebView webView, int errorCode, String description, String failingUrl);

    boolean shouldOverrideUrlLoading(CommonWebView webView, String url);
}
