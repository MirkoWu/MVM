package com.mirkowu.lib_webview.client;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Message;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;

import com.mirkowu.lib_util.LogUtil;
import com.mirkowu.lib_webview.CommonWebView;
import com.mirkowu.lib_webview.callback.IWebViewCallBack;
import com.mirkowu.lib_webview.callback.IWebViewFileChooser;
import com.tencent.smtt.export.external.interfaces.IX5WebChromeClient;
import com.tencent.smtt.export.external.interfaces.JsPromptResult;
import com.tencent.smtt.export.external.interfaces.JsResult;
import com.tencent.smtt.export.external.interfaces.WebResourceRequest;
import com.tencent.smtt.sdk.ValueCallback;
import com.tencent.smtt.sdk.WebChromeClient;
import com.tencent.smtt.sdk.WebView;

public class BaseWebChromeClient extends WebChromeClient {
    private static final int PROGRESS_LENGTH = 100;
    private static final String TAG = BaseWebChromeClient.class.getSimpleName();
    private Context mContext;
    private IWebViewFileChooser mWebViewFileChooser; //WebView回调统一处理
    private IWebViewCallBack mWebViewCallBack; //WebView回调统一处理
    private boolean mAlertBoxBlock;

    public BaseWebChromeClient(@NonNull Context context, IWebViewFileChooser fileChooser, IWebViewCallBack webViewCallBack) {
        mContext = context;
        mWebViewFileChooser = fileChooser;
        mWebViewCallBack = webViewCallBack;
    }

    public boolean isAlertBoxBlock() {
        return mAlertBoxBlock;
    }

    public void setAlertBoxBlock(boolean alertBoxBlock) {
        this.mAlertBoxBlock = alertBoxBlock;
    }

    @Override
    public boolean onJsAlert(WebView view, String url, final String message, final JsResult result) {
        if (!mAlertBoxBlock) {
            result.confirm();
        }

        Dialog alertDialog = new AlertDialog.Builder(mContext).
                setMessage(message).
                setCancelable(false).
                setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        if (mAlertBoxBlock) {
                            result.confirm();
                        }
                    }
                })
                .create();
        alertDialog.show();
        return true;
    }

    @Override
    public boolean onJsConfirm(WebView view, String url, String message,
                               final JsResult result) {
        if (!mAlertBoxBlock) {
            result.confirm();
        }

        DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (mAlertBoxBlock) {
                    if (which == Dialog.BUTTON_POSITIVE) {
                        result.confirm();
                    } else {
                        result.cancel();
                    }
                }
            }
        };
        new AlertDialog.Builder(mContext)
                .setMessage(message)
                .setCancelable(false)
                .setPositiveButton(android.R.string.ok, listener)
                .setNegativeButton(android.R.string.cancel, listener).show();
        return true;
    }

    @Override
    public boolean onJsPrompt(WebView view, String url, final String message,
                              String defaultValue, final JsPromptResult result) {
        if (!mAlertBoxBlock) {
            result.confirm();
        }

        final EditText editText = new EditText(mContext);
        editText.setText(defaultValue);
        if (defaultValue != null) {
            editText.setSelection(defaultValue.length());
        }
        float dpi = mContext.getResources().getDisplayMetrics().density;
        DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (mAlertBoxBlock) {
                    if (which == Dialog.BUTTON_POSITIVE) {
                        result.confirm(editText.getText().toString());
                    } else {
                        result.cancel();
                    }
                }
            }
        };
        new AlertDialog.Builder(mContext)
                .setTitle(message)
                .setView(editText)
                .setCancelable(false)
                .setPositiveButton(android.R.string.ok, listener)
                .setNegativeButton(android.R.string.cancel, listener)
                .show();
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        int t = (int) (dpi * 16);
        layoutParams.setMargins(t, 0, t, 0);
        layoutParams.gravity = Gravity.CENTER_HORIZONTAL;
        editText.setLayoutParams(layoutParams);
        int padding = (int) (15 * dpi);
        editText.setPadding(padding - (int) (5 * dpi), padding, padding, padding);
        return true;
    }
//
//    @Override
//    public boolean onJsAlert(WebView webView, String s, String s1, JsResult jsResult) {
//        //可以弹框或进行其它处理，但一定要回调result.confirm或者cancel
//        //这里要返回true否则内核会进行提示
//        return super.onJsAlert(webView, s, s1, jsResult);
//    }
//
//    @Override
//    public boolean onJsConfirm(WebView webView, String s, String s1, JsResult jsResult) {
//        //可以弹框或进行其它处理，但一定要回调result.confirm或者cancel
////        jsResult.confirm();
//        return super.onJsConfirm(webView, s, s1, jsResult);
//    }
//
//    @Override
//    public boolean onJsPrompt(WebView webView, String s, String s1, String s2, JsPromptResult jsPromptResult) {
//        //可以弹框或进行其它处理，但一定要回调result.confirm或者cancel，confirm可以将用户输入作为参数
////        当页面有对应的js提示时会回调对应的方法；如果没有设置这些内核将会使用默认弹出样式提示用户
//        return super.onJsPrompt(webView, s, s1, s2, jsPromptResult);
//    }

    @Override
    public boolean onJsBeforeUnload(WebView webView, String s, String s1, JsResult jsResult) {
        //可以弹框或进行其它处理，但一定要回调result.confirm或者cancel
        return super.onJsBeforeUnload(webView, s, s1, jsResult);
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
        if (mWebViewFileChooser != null &&
                mWebViewFileChooser.onShowFileChooser((CommonWebView) webView, filePathCallback, fileChooserParams)) {
            return true;
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

    @Override
    public void onReceivedTitle(WebView webView, String s) {
        super.onReceivedTitle(webView, s);
        if (mWebViewCallBack != null) {
            mWebViewCallBack.onReceivedTitle((CommonWebView) webView, s);
        }
    }

    @Override
    public boolean onCreateWindow(WebView webView, boolean b, boolean b1, Message message) {
        //setSupportMultipleWindows(true)时必须重写
        CommonWebView newWebView = new CommonWebView(mContext);
        newWebView.setWebViewClient(new BaseWebViewClient(newWebView, mWebViewCallBack) {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                if (!super.shouldOverrideUrlLoading(view, request)) {
                    //当前WebView重新加载
                    webView.loadUrl(request.getUrl().toString());
                }
                return true;
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (!super.shouldOverrideUrlLoading(view, url)) {
                    //当前WebView重新加载
                    webView.loadUrl(url);
                }
                return true;
            }
        });
        newWebView.setWebChromeClient(this);

        WebView.WebViewTransport transport = (WebView.WebViewTransport) message.obj;
        transport.setWebView(newWebView);
        message.sendToTarget();
        return true;
    }

    @Override
    public void onCloseWindow(WebView webView) {
        super.onCloseWindow(webView);
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
        if (mWebViewCallBack != null) {
            mWebViewCallBack.onProgressChanged((CommonWebView) webView, newProgress);
        }
        //  updateProgress(mProgressBar,newProgress);
    }

    public static void updateProgress(ProgressBar mProgressBar, int newProgress) {
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
