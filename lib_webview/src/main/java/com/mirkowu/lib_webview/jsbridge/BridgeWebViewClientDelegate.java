package com.mirkowu.lib_webview.jsbridge;

import android.graphics.Bitmap;

import androidx.annotation.NonNull;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

/**
 * Created by bruce on 10/28/15.
 */
public class BridgeWebViewClientDelegate {
    private WebViewJavascriptBridge webView;

    public BridgeWebViewClientDelegate(@NonNull WebViewJavascriptBridge webView) {
        this.webView = webView;
    }


    public boolean shouldOverrideUrlLoading(String url) {
        try {
            url = URLDecoder.decode(url, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        if (url.startsWith(BridgeUtil.YY_RETURN_DATA)) { // 如果是返回数据
            if (webView != null) {
                webView.handlerReturnData(url);
            }
            return true;
        } else if (url.startsWith(BridgeUtil.YY_OVERRIDE_SCHEMA)) { //
            if (webView != null) {
                webView.flushMessageQueue();
            }
            return true;
        } else {
            return false;
        }
    }


    public void onPageStarted(  String url, Bitmap favicon) {

    }


    public void onPageFinished(  String url) {
        if (webView != null) {
            webView.loadLocalJS();
        }
    }


    public void onReceivedError(  int errorCode, String description, String failingUrl) {

    }
}