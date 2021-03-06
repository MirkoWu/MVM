package com.mirkowu.lib_webview.client;

import android.annotation.TargetApi;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Message;

import androidx.annotation.RequiresApi;

import com.mirkowu.lib_util.LogUtil;
import com.mirkowu.lib_webview.CommonWebView;
import com.mirkowu.lib_webview.callback.IWebViewCallBack;
import com.mirkowu.lib_webview.jsbridge.BridgeWebViewClientDelegate;
import com.tencent.smtt.export.external.interfaces.HttpAuthHandler;
import com.tencent.smtt.export.external.interfaces.SslErrorHandler;
import com.tencent.smtt.export.external.interfaces.WebResourceError;
import com.tencent.smtt.export.external.interfaces.WebResourceRequest;
import com.tencent.smtt.export.external.interfaces.WebResourceResponse;
import com.tencent.smtt.sdk.WebView;
import com.tencent.smtt.sdk.WebViewClient;

public class BaseWebViewClient extends WebViewClient {

    private static final String TAG = "XXWebviewCallBack";
    public static final String SCHEME_SMS = "sms:";
    public static final String SCHEME_WEIXIN = "weixin://";
    private IWebViewCallBack mWebViewCallBack;
    private CommonWebView mWebView;
    private BridgeWebViewClientDelegate mBridgeWebViewClientDelegate;

    public BaseWebViewClient(CommonWebView webView, IWebViewCallBack webViewCallBack) {
        this.mWebView = webView;
        this.mWebViewCallBack = webViewCallBack;
        this.mBridgeWebViewClientDelegate = new BridgeWebViewClientDelegate(mWebView);
    }


    /**
     * url重定向会执行此方法以及点击页面某些链接也会执行此方法
     *
     * @return true:表示当前url已经加载完成，即使url还会重定向都不会再进行加载 false 表示此url默认由系统处理，该重定向还是重定向，直到加载完成
     */
    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) {
        if (mBridgeWebViewClientDelegate.shouldOverrideUrlLoading(url)) {
            return true;
        }

        LogUtil.e(TAG, "shouldOverrideUrlLoading url: " + url);
        return handleLinked(url);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
        if (mBridgeWebViewClientDelegate.shouldOverrideUrlLoading(request.getUrl().toString())) {
            return true;
        }

        LogUtil.e(TAG, "shouldOverrideUrlLoading url: " + request.getUrl());
        // 控制页面中点开新的链接在当前webView中打开
        return handleLinked(request.getUrl().toString());
    }

    /**
     * 支持电话、短信、邮件、地图跳转，跳转的都是手机系统自带的应用
     */
    private boolean handleLinked(String url) {
        if (url.startsWith(WebView.SCHEME_TEL)
                || url.startsWith(SCHEME_SMS)
                || url.startsWith(WebView.SCHEME_MAILTO)
                || url.startsWith(WebView.SCHEME_GEO)
                || url.startsWith(SCHEME_WEIXIN)) {
            try {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(url));
                mWebView.getContext().startActivity(intent);
            } catch (ActivityNotFoundException ignored) {
                LogUtil.e(ignored.toString());
                return false;
            }
            return true;
        }

        if (mWebViewCallBack != null) {
            LogUtil.e("tag", "shouldOverrideUrlLoading------url=" + url);
            return mWebViewCallBack.shouldOverrideUrlLoading(mWebView, url);
        }
        return false;
    }

    @Override
    public void onPageFinished(WebView view, String url) {
        LogUtil.e(TAG, "onPageFinished url:" + url);

        mBridgeWebViewClientDelegate.onPageFinished(url);

        if (mWebViewCallBack != null) {
            mWebViewCallBack.pageFinished(mWebView, url);
        }
    }

    @Override
    public void onPageStarted(WebView view, String url, Bitmap favicon) {
        LogUtil.e(TAG, "onPageStarted url: " + url);

        mBridgeWebViewClientDelegate.onPageStarted(url, favicon);

        if (mWebViewCallBack != null) {
            mWebViewCallBack.pageStarted(mWebView, url);
        }
    }

    @Override
    public void onScaleChanged(WebView view, float oldScale, float newScale) {
        super.onScaleChanged(view, oldScale, newScale);
    }

    @Override
    public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
        return shouldInterceptRequest(view, request.getUrl().toString());
    }

    @Override
    public WebResourceResponse shouldInterceptRequest(WebView view, String url) {
        return null;
    }

    @Override
    public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
        super.onReceivedError(view, errorCode, description, failingUrl);
        LogUtil.e(TAG, "webview" + " error" + errorCode + " + " + description);

        mBridgeWebViewClientDelegate.onReceivedError(errorCode, description, failingUrl);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return;
        }
        if (mWebViewCallBack != null) {
            mWebViewCallBack.onReceivedError(this, mWebView, errorCode, description, failingUrl);
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
                mWebViewCallBack.onReceivedError(this, mWebView, webResourceError.getErrorCode(), (String) webResourceError.getDescription(), webResourceRequest.getUrl().toString());
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