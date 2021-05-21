package com.mirkowu.lib_webview;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.mirkowu.lib_base.mediator.BaseMediator;


public class WebViewActivity extends BaseWebViewActivity {


    private static final String KEY_TITLE = "title";
    private static final String KEY_URL = "url";

    public static void start(Context context, String title, String url) {
        if (TextUtils.isEmpty(url)) return;
        Intent starter = new Intent(context, WebViewActivity.class);
        starter.putExtra(KEY_TITLE, title);
        starter.putExtra(KEY_URL, url);
        context.startActivity(starter);
    }

    @Override
    protected BaseMediator initMediator() {
        return null;
    }

    @Override
    protected void initialize() {
        super.initialize();
        String title = getIntent().getStringExtra(KEY_TITLE);
        String url = getIntent().getStringExtra(KEY_URL);
        loadUrl(url);
    }
}