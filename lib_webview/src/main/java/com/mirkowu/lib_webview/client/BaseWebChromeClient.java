package com.mirkowu.lib_webview.client;

import android.graphics.Bitmap;
import android.net.Uri;
import android.view.View;
import android.widget.ProgressBar;

import com.mirkowu.lib_util.LogUtil;
import com.mirkowu.lib_webview.CommonWebView;
import com.mirkowu.lib_webview.callback.IWebViewFileChooser;
import com.tencent.smtt.export.external.interfaces.IX5WebChromeClient;
import com.tencent.smtt.export.external.interfaces.JsPromptResult;
import com.tencent.smtt.export.external.interfaces.JsResult;
import com.tencent.smtt.sdk.ValueCallback;
import com.tencent.smtt.sdk.WebChromeClient;
import com.tencent.smtt.sdk.WebView;

public class BaseWebChromeClient extends WebChromeClient {
    private static final int PROGRESS_LENGTH = 100;
    private static final String TAG = BaseWebChromeClient.class.getSimpleName();
    private ProgressBar mProgressBar;
    private IWebViewFileChooser mWebViewCallBack; //WebView回调统一处理

    public BaseWebChromeClient(IWebViewFileChooser webViewCallBack, ProgressBar progressBar) {
        mWebViewCallBack = webViewCallBack;
        mProgressBar = progressBar;
    }

    @Override
    public boolean onJsAlert(WebView webView, String s, String s1, JsResult jsResult) {
        //可以弹框或进行其它处理，但一定要回调result.confirm或者cancel
        //这里要返回true否则内核会进行提示
        return super.onJsAlert(webView, s, s1, jsResult);
    }

    @Override
    public boolean onJsConfirm(WebView webView, String s, String s1, JsResult jsResult) {
        //可以弹框或进行其它处理，但一定要回调result.confirm或者cancel
//        jsResult.confirm();
        return super.onJsConfirm(webView, s, s1, jsResult);
    }

    @Override
    public boolean onJsBeforeUnload(WebView webView, String s, String s1, JsResult jsResult) {
        //可以弹框或进行其它处理，但一定要回调result.confirm或者cancel
        return super.onJsBeforeUnload(webView, s, s1, jsResult);
    }

    @Override
    public boolean onJsPrompt(WebView webView, String s, String s1, String s2, JsPromptResult jsPromptResult) {
        //可以弹框或进行其它处理，但一定要回调result.confirm或者cancel，confirm可以将用户输入作为参数
//        当页面有对应的js提示时会回调对应的方法；如果没有设置这些内核将会使用默认弹出样式提示用户
        return super.onJsPrompt(webView, s, s1, s2, jsPromptResult);
    }

//    @Override
//    public void onGeolocationPermissionsShowPrompt(String origin, GeolocationPermissionsCallback callback) {
//        super.onGeolocationPermissionsShowPrompt(origin, callback);
//        //在此可以弹窗提示用户
//        //处理后需要回调
//        //参数的意义见上面的接口说明
//        /**
//         * origin：要保存的授权域
//         * allow：是否允许定位
//         * remember：该记录是否持久化保存，保存后可以调用相应的缓存清除接口进行清除
//         */
//        callback.invoke(origin, true, false);
//    }

    /**
     * 文件单选：设置client回调
     *
     * @param uploadFile
     * @param acceptType
     * @param captureType
     */
//    @Override
//    public void openFileChooser(ValueCallback<Uri> uploadFile,
//                                String acceptType,
//                                String captureType) {
//        //保存对应的valuecallback供选择后使用
//        //通过startActivityForResult启动文件选择窗口或自定义文件选择
//        super.openFileChooser(uploadFile, acceptType, captureType);
//    }

    /**
     * 设置client回调（单选多选均会回调该接口）
     *
     * @param webView
     * @param fileChooserParams
     * @return
     */
    @Override
    public boolean onShowFileChooser(WebView webView,
                                     ValueCallback<Uri[]> filePathCallback,
                                     FileChooserParams fileChooserParams) {
        if (mWebViewCallBack != null) {
            return mWebViewCallBack.onShowFileChooser((CommonWebView) webView, filePathCallback, fileChooserParams);
        }
        return super.onShowFileChooser(webView, filePathCallback, fileChooserParams);
    }

    /**
     * H5全屏
     * 当页面通过js请求全屏、退出全屏时内核会回调onShowCustomView、onHideCustomView接口，外部按照如下适配即可
     *
     * @param view
     * @param customViewCallback
     */
    @Override
    public void onShowCustomView(View view, IX5WebChromeClient.CustomViewCallback customViewCallback) {
        super.onShowCustomView(view, customViewCallback);
    }

    @Override
    public void onHideCustomView() {
        super.onHideCustomView();
    }

    /**
     * 获取页面Favicon
     * 设置client监听页面Favicon回调
     *
     * @param webView
     * @param bitmap
     */
    @Override
    public void onReceivedIcon(WebView webView, Bitmap bitmap) {
        //这里对favicon进行操作
        super.onReceivedIcon(webView, bitmap);
    }

    /**
     * 页面加载进度
     *
     * @param webView
     * @param newProgress 进度值
     */
    @Override
    public void onProgressChanged(WebView webView, int newProgress) {
        LogUtil.e(TAG, "newProgress---------=" + newProgress + ",thread=" + Thread.currentThread().getName());
        if (mProgressBar != null) {
            if (newProgress < PROGRESS_LENGTH) {
                if (mProgressBar.getVisibility() == View.VISIBLE) {
                    mProgressBar.setProgress(newProgress);
                }
            } else {
                if (mProgressBar.getVisibility() == View.VISIBLE) {
                    mProgressBar.setVisibility(View.GONE);
                }
            }
        }
    }

}
