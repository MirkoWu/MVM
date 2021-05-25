package com.mirkowu.lib_webview;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ProgressBar;

import androidx.annotation.Nullable;

import com.mirkowu.lib_base.activity.BaseMVMActivity;
import com.mirkowu.lib_base.mediator.BaseMediator;
import com.mirkowu.lib_webview.callback.IWebViewCallBack;
import com.mirkowu.lib_webview.client.BaseWebChromeClient;
import com.mirkowu.lib_webview.client.BaseWebViewClient;
import com.mirkowu.lib_webview.config.WebConfig;
import com.mirkowu.lib_webview.setting.WebSettingUtil;
import com.mirkowu.lib_webview.util.WebViewUtil;
import com.mirkowu.lib_widget.Toolbar;
import com.mirkowu.lib_widget.stateview.StateView;
import com.tencent.smtt.sdk.ValueCallback;
import com.tencent.smtt.sdk.WebChromeClient;


public class WebViewActivity extends BaseMVMActivity {


    private static final String KEY_TITLE = "title";
    private static final String KEY_URL = "url";

    public static void start(Context context, String title, String url) {
        if (TextUtils.isEmpty(url)) {
            return;
        }
        Intent starter = new Intent(context, WebViewActivity.class);
        starter.putExtra(KEY_TITLE, title);
        starter.putExtra(KEY_URL, url);
        context.startActivity(starter);
    }

    private Toolbar mToolbar;
    private StateView mStateView;
    private CommonWebView mWebView;
    private ProgressBar mProgressBar;
    private IWebViewCallBack mWebViewCallBack;

    @Override
    protected BaseMediator initMediator() {
        return null;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.webview_activity_web_view;
    }

    @Override
    protected void initialize() {

        String title = getIntent().getStringExtra(KEY_TITLE);
        String url = getIntent().getStringExtra(KEY_URL);



        mToolbar = findViewById(R.id.mToolbar);
        mToolbar.setTitle(title);
        mToolbar.setVisibility(View.VISIBLE);

        mWebView = findViewById(R.id.mWebView);
        mProgressBar = findViewById(R.id.mProgressBar);
        mProgressBar.setVisibility(View.VISIBLE);
        mStateView = findViewById(R.id.mStateView);
        mStateView.setOnRefreshListener(() -> mWebView.reload());
        mStateView.setLoadingState();

        WebConfig webConfig = getWebConfig();
        mWebViewCallBack = webConfig.getWebViewCallBack();

        WebSettingUtil.toSetting(mWebView, webConfig.getUserAgent());
        mWebView.setHeaders(webConfig.getHeaders());
        mWebView.setWebViewClient(new BaseWebViewClient(mWebView, mWebViewCallBack));
        mWebView.setWebChromeClient(new BaseWebChromeClient(mWebViewCallBack, mProgressBar));
        mWebView.addJavascriptInterface(mWebViewCallBack, webConfig.getJsInjectionArrays());


        mWebView.clearHistory();
        mWebView.postDelayed(new Runnable() {
            @Override
            public void run() {
                mWebView.loadUrl(url);
            }
        }, 2000L);


        mWebView.setVisibility(View.VISIBLE);
    }


    protected WebConfig getWebConfig() {
        return new WebConfig().setShowProgress(true)
                .setShowBack(false)
//                .setJsInjectionArrays(new String[]{BROWSER_ADD_JS, YI_ZHEN_JS_BRIDGE_ADD_JS})
                .setCallBack(new IWebViewCallBack() {
                    @Override
                    public boolean onShowFileChooser(CommonWebView webView, ValueCallback<Uri[]> filePathCallback, WebChromeClient.FileChooserParams fileChooserParams) {
                        return false;
                    }

                    @Override
                    public void pageStarted(CommonWebView webView, String url) {

                    }

                    @Override
                    public void pageFinished(CommonWebView webView, String url) {
                        mStateView.setGoneState();
                    }

                    @Override
                    public void onReceivedError(BaseWebViewClient client, CommonWebView view, int errorCode, String description, String failingUrl) {

                    }

                    @Override
                    public boolean shouldOverrideUrlLoading(CommonWebView webView, String url) {
                        return false;
                    }

                    @Override
                    public <T> T jsCallAndroid(String action, Object content, Class<T> t) {
                        return null;
                    }

                    @Override
                    public void onActivityResult(CommonWebView webView, int requestCode, int resultCode, Intent data) {

                    }
                });
    }

    @Override
    protected void onResume() {
        super.onResume();
        WebViewUtil.onResume(mWebView);
    }

    @Override
    protected void onPause() {
        super.onPause();
        WebViewUtil.onPause(mWebView);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (mWebViewCallBack != null) {
            mWebViewCallBack.onActivityResult(mWebView, requestCode, resultCode, data);
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (mWebView != null && mWebView.canGoBack()) {
                mWebView.goBack();
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        WebViewUtil.clearWebView(mWebView);
        super.onDestroy();
    }
}