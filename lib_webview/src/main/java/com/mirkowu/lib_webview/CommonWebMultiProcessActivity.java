package com.mirkowu.lib_webview;


import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

/**
 * 通用的WebView 支持多进程
 * 1.支持X5
 * 2.支持JsBridge
 */
public class CommonWebMultiProcessActivity extends CommonWebActivity {
    public static void start(Context context, String title, String url) {
        if (TextUtils.isEmpty(url)) {
            return;
        }
        Intent starter = new Intent(context, CommonWebMultiProcessActivity.class);
        starter.putExtra(KEY_TITLE, title);
        starter.putExtra(KEY_URL, url);
        starter.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(starter);
    }
}