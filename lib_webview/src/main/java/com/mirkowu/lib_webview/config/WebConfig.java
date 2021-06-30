package com.mirkowu.lib_webview.config;


import com.mirkowu.lib_webview.callback.IWebViewCallBack;

import java.util.Map;

/**
 * 处理可配置的业务逻辑。但这些逻辑webView有可能用不到。所以是可用可不用的。
 */
public class WebConfig {
    //    private String mTitleText; //标题文案
    private boolean mShowToolbar; //设置是否需要标题栏，true表示需要，false表示不需要
    private boolean mShowBack; //设置是否需要返回键，true表示需要，false表示不需要
    private boolean mShowClose; //设置是否需要关闭键，true表示需要，false表示不需要
    private boolean mShowProgress; //设置是否需要进度条，true表示需要，false表示不需要
    private IWebViewCallBack mWebViewCallBack; //WebView回调统一处理
    private String mUserAgent;
    private String[] mJsInjectionObjectNameArrays; //js注入对象
    private Map<String, String> mHeaders; // webView 添加header

//    /**
//     * @param text 设置是否展示 title
//     * @return
//     */
//    public WebConfig setTitleText(String text) {
//        mTitleText = text;
//        return this;
//    }

    /**
     * @param showToolbar 左上角是否需要标题栏，true表示需要，false表示不需要
     * @return
     */
    public WebConfig setShowToolbar(boolean showToolbar) {
        mShowToolbar = showToolbar;
        return this;
    }

    /**
     * @param showBack 左上角是否需要返回键，true表示需要，false表示不需要
     * @return
     */
    public WebConfig setShowBack(boolean showBack) {
        mShowBack = showBack;
        return this;
    }

    /**
     * @param showClose 左上角是否需要关闭键，true表示需要，false表示不需要
     * @return
     */
    public WebConfig setShowClose(boolean showClose) {
        mShowClose = showClose;
        return this;
    }

    /**
     * @param showProgress 是否展示进度条 true表示展示，false表示不展示
     * @return
     */
    public WebConfig setShowProgress(boolean showProgress) {
        mShowProgress = showProgress;
        return this;
    }

    /**
     * @param callBack WebView回调统一处理
     * @return
     */
    public WebConfig setCallBack(IWebViewCallBack callBack) {
        mWebViewCallBack = callBack;
        return this;
    }

    /**
     * @param userAgent 设置需要添加的UA
     * @return
     */
    public WebConfig setUserAgent(String userAgent) {
        this.mUserAgent = userAgent;
        return this;
    }

    /**
     * @param headers 添加header
     * @return
     */
    public WebConfig setHeaders(Map<String, String> headers) {
        this.mHeaders = headers;
        return this;
    }

    /**
     * @param jsInjectionArrays 设置js注入对象
     * @return
     */
    public WebConfig setJsInjectionArrays(String[] jsInjectionArrays) {
        this.mJsInjectionObjectNameArrays = jsInjectionArrays;
        return this;
    }

    public Map<String, String> getHeaders() {
        return mHeaders;
    }

    public String[] getJsInjectionArrays() {
        return mJsInjectionObjectNameArrays;
    }

//    public String getTitleText() {
//        return mTitleText;
//    }

    public boolean isShowToolbar() {
        return mShowToolbar;
    }

    public boolean isShowBack() {
        return mShowBack;
    }
    public boolean isShowClose() {
        return mShowClose;
    }

    public boolean isShowProgress() {
        return mShowProgress;
    }

    public String getUserAgent() {
        return mUserAgent;
    }

    public IWebViewCallBack getWebViewCallBack() {
        return mWebViewCallBack;
    }
}
