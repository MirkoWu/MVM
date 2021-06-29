package com.mirkowu.lib_webview;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ProgressBar;

import androidx.annotation.Nullable;

import com.mirkowu.lib_base.activity.BaseMVMActivity;
import com.mirkowu.lib_base.mediator.BaseMediator;
import com.mirkowu.lib_util.utilcode.util.BarUtils;
import com.mirkowu.lib_webview.callback.DefaultWebViewFileChooser;
import com.mirkowu.lib_webview.callback.IWebViewCallBack;
import com.mirkowu.lib_webview.client.BaseWebChromeClient;
import com.mirkowu.lib_webview.client.BaseWebViewClient;
import com.mirkowu.lib_webview.config.WebConfig;
import com.mirkowu.lib_webview.setting.WebSettingUtil;
import com.mirkowu.lib_widget.Toolbar;
import com.tencent.smtt.sdk.WebSettings;


/**
 * 通用的WebView
 * 1.支持X5
 * 2.支持JsBridge
 */
public class CommonWebActivity extends BaseMVMActivity {
    public static final String KEY_TITLE = "title";
    public static final String KEY_URL = "url";

    public static void start(Context context, String title, String url) {
        if (TextUtils.isEmpty(url)) {
            return;
        }
        Intent starter = new Intent(context, CommonWebActivity.class);
        starter.putExtra(KEY_TITLE, title);
        starter.putExtra(KEY_URL, url);
        starter.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(starter);
    }

    protected Toolbar mToolbar;
    protected CommonWebView mWebView;
    protected ProgressBar mProgressBar;
    protected IWebViewCallBack mWebViewCallBack;
    protected DefaultWebViewFileChooser mFileChooser;

    @Override
    protected BaseMediator initMediator() {
        return null;
    }


    @Override
    protected int getLayoutId() {
        return R.layout.webview_layout_common_web_view;
    }

    @Override
    protected void initialize() {
        BarUtils.setStatusBarLightMode(this, true);
        String title = getIntent().getStringExtra(KEY_TITLE);
        String url = getIntent().getStringExtra(KEY_URL);

        initView();

        WebConfig webConfig = getWebConfig();
        mProgressBar.setVisibility(webConfig.isShowProgress() ? View.VISIBLE : View.GONE);
        mToolbar.setVisibility(webConfig.isShowToolbar() ? View.VISIBLE : View.GONE);
        mToolbar.setShowBackIcon(webConfig.isShowBack());
        mToolbar.setTitle(title);

        configWebSettings(webConfig);
        loadUrl(url);
    }

    protected void initView() {
        mToolbar = findViewById(R.id.mToolbar);
        mProgressBar = findViewById(R.id.mProgressBar);
        mWebView = findViewById(R.id.mWebView);
        getLifecycle().addObserver(mWebView);
    }


    protected void configWebSettings(WebConfig webConfig) {
        mWebViewCallBack = webConfig.getWebViewCallBack();
        WebSettingUtil.toSetting(mWebView, webConfig.getUserAgent());
        mWebView.setHeaders(webConfig.getHeaders());
        mWebView.setWebViewClient(new BaseWebViewClient(mWebView, mWebViewCallBack));

        mFileChooser = new DefaultWebViewFileChooser(this);
        mWebView.setWebChromeClient(new BaseWebChromeClient(mFileChooser, mProgressBar));
    }

    protected void loadUrl(String url) {
        mWebView.loadUrl(url);
    }

    protected void clearHistory() {
        mWebView.clearHistory();
    }

    protected WebConfig getWebConfig() {
        return new WebConfig()
                .setShowBack(true)
                .setShowToolbar(true)
                .setShowProgress(true)
//                .setJsInjectionArrays(new String[]{"android"})
                .setCallBack(new IWebViewCallBack() {
                    @Override
                    public void pageStarted(CommonWebView webView, String url) {

                    }

                    @Override
                    public void pageFinished(CommonWebView webView, String url) {
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

                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        handleWebResult(requestCode, resultCode, data);
    }

    private void handleWebResult(int requestCode, int resultCode, Intent data) {
        if (mFileChooser != null) {
            mFileChooser.onActivityResult(mWebView, requestCode, resultCode, data);
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (handleWebBack(keyCode, event)) {
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }

    /**
     * 处理返回键
     *
     * @param keyCode
     * @param event
     * @return
     */
    protected boolean handleWebBack(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (mWebView != null && mWebView.canGoBack()) {
                mWebView.goBack();
                return true;
            }
        }
        return false;
    }

    /**
     * 方式2：加载html文本
     *
     * @param content
     */
    protected void loadHtmlText(String content) {
        WebSettings webSettings = mWebView.getSettings();
        //设置自适应屏幕，两者合用
        webSettings.setUseWideViewPort(false); //将图片调整到适合webview的大小
        webSettings.setLoadWithOverviewMode(false); // 缩放至屏幕的大小


        /*** 完美自适应屏幕  */
        StringBuilder sb = new StringBuilder();
        sb.append(content)
                .append("<html>")
                .append("<head>")
                .append("<meta charset=\\\"utf-8\\\">")
                .append("<meta id=\\\"viewport\\\" name=\\\"viewport\\\" content=\\\"width=device-width*0.9,initial-scale=1.0,maximum-scale=1.0,user-scalable=false\\\" />")
                .append("<meta name=\\\"apple-mobile-web-app-capable\\\" content=\\\"yes\\\" />")
                .append("<meta name=\\\"apple-mobile-web-app-status-bar-style\\\" content=\\\"black\\\" />")
                .append("<meta name=\\\"black\\\" name=\\\"apple-mobile-web-app-status-bar-style\\\" />")
                .append("<style>img{width:100%;}</style>")
                .append("<style>iframe{width:100%;}</style>")
                .append("<style>table{width:100%;}</style>")
                .append("<style>body{font-size:18px;}</style>")
                .append("<title>mWebView</title>");


        mWebView.loadDataWithBaseURL(null, sb.toString(), "text/html", "utf-8", null);
    }

}