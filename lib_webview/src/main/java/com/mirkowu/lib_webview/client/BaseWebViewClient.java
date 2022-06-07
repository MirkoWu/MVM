package com.mirkowu.lib_webview.client;

import android.annotation.TargetApi;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Message;
import android.webkit.URLUtil;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import com.mirkowu.lib_util.IntentUtil;
import com.mirkowu.lib_util.LogUtil;
import com.mirkowu.lib_webview.CommonWebView;
import com.mirkowu.lib_webview.callback.IWebViewCallBack;
import com.tencent.smtt.export.external.interfaces.HttpAuthHandler;
import com.tencent.smtt.export.external.interfaces.SslErrorHandler;
import com.tencent.smtt.export.external.interfaces.WebResourceError;
import com.tencent.smtt.export.external.interfaces.WebResourceRequest;
import com.tencent.smtt.export.external.interfaces.WebResourceResponse;
import com.tencent.smtt.sdk.WebView;
import com.tencent.smtt.sdk.WebViewClient;

public class BaseWebViewClient extends WebViewClient {

    private static final String TAG = "XXWebviewCallBack";
    private IWebViewCallBack mWebViewCallBack;
    private CommonWebView mWebView;

    public BaseWebViewClient(@NonNull CommonWebView webView, IWebViewCallBack webViewCallBack) {
        this.mWebView = webView;
        this.mWebViewCallBack = webViewCallBack;
    }


    /**
     * url重定向会执行此方法以及点击页面某些链接也会执行此方法
     *
     * @return true:表示当前url已经加载完成，即使url还会重定向都不会再进行加载 false 表示此url默认由系统处理，该重定向还是重定向，直到加载完成
     */
    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) {
        LogUtil.e(TAG, "shouldOverrideUrlLoading url: " + url);
        return handleLinked(url);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
        LogUtil.e(TAG, "shouldOverrideUrlLoading url: " + request.getUrl());
        // 控制页面中点开新的链接在当前webView中打开
        return handleLinked(request.getUrl().toString());
    }

    /**
     * 支持电话、短信、邮件、地图跳转，跳转的都是手机系统自带的应用
     */
    private boolean handleLinked(String url) {
        //自定义拦截
        if (mWebViewCallBack != null && mWebViewCallBack.shouldOverrideUrlLoading(mWebView, url)) {
            return true;
        }

        //无效Url 则通过Intent打开
        if (!URLUtil.isValidUrl(url)) {
            return IntentUtil.openScheme(mWebView.getContext(), url);
        }

        return false;
    }

    @Override
    public void onPageFinished(WebView view, String url) {
        LogUtil.e(TAG, "onPageFinished url:" + url);
        if (mWebViewCallBack != null) {
            mWebViewCallBack.pageFinished(mWebView, url);
        }
    }

    @Override
    public void onPageStarted(WebView view, String url, Bitmap favicon) {
        LogUtil.e(TAG, "onPageStarted url: " + url);
        if (mWebViewCallBack != null) {
            mWebViewCallBack.pageStarted(mWebView, url);
        }
    }

    @Override
    public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
        super.onReceivedError(view, errorCode, description, failingUrl);
        LogUtil.e(TAG, "webview" + " error" + errorCode + " + " + description);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return;
        }
        if (mWebViewCallBack != null) {
            mWebViewCallBack.onReceivedError(mWebView, errorCode, description, failingUrl);
        }
    }

    // 新版本，只会在Android6及以上调用
    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public void onReceivedError(WebView webView, WebResourceRequest webResourceRequest, WebResourceError webResourceError) {
        super.onReceivedError(webView, webResourceRequest, webResourceError);
//        LogUtil.e(TAG, "webview" + " error" + errorCode + " + " + description);
        if (webResourceRequest.isForMainFrame()) {
            if (mWebViewCallBack != null) {
                mWebViewCallBack.onReceivedError(mWebView, webResourceError.getErrorCode(), (String) webResourceError.getDescription(), webResourceRequest.getUrl().toString());
            }
        }
    }

    @Override
    public void onReceivedSslError(WebView webView, final SslErrorHandler handler, com.tencent.smtt.export.external.interfaces.SslError error) {
        handler.proceed();
    }

    /**
     * HttpAuth密码记录及使用 当访问页面需要输入用户名和密码时会回调对应client接口.
     *
     * @param webView
     * @param handler
     * @param host
     * @param realm
     */
    @Override
    public void onReceivedHttpAuthRequest(WebView webView,
                                          HttpAuthHandler handler,
                                          String host,
                                          String realm) {
        super.onReceivedHttpAuthRequest(webView, handler, host, realm);
        //首先判断是否可以重复使用对应用户名和密码如果可以则获取已保存的密码（获取成功后直接使用）
        //如果不允许重复使用用户名和密码或者未保存用户名和密码则需要提示用户输入
        //用户输入用户名和密码后可以将对应数据保存
        //获取密码接口
//        mWebView.getHttpAuthUsernamePassword(host, realm);
//保存密码接口
//        mWebView.setHttpAuthUsernamePassword( host,  realm, String username, String password)
    }

    @Override
    public void onFormResubmission(WebView view, Message dontResend, Message resend) {
        //解决有表单form提交的时候，webview在调用goBack()方法时，不起作用
        resend.sendToTarget();
    }
}