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
import com.mirkowu.lib_webview.callback.DefaultWebViewFileChooser;
import com.mirkowu.lib_webview.callback.IWebViewCallBack;
import com.mirkowu.lib_webview.client.BaseWebChromeClient;
import com.mirkowu.lib_webview.client.BaseWebViewClient;
import com.mirkowu.lib_webview.config.WebConfig;
import com.mirkowu.lib_webview.setting.WebSettingUtil;
import com.mirkowu.lib_widget.Toolbar;
import com.mirkowu.lib_widget.stateview.StateView;
import com.tencent.smtt.sdk.ValueCallback;
import com.tencent.smtt.sdk.WebChromeClient;

import me.jessyan.autosize.internal.CancelAdapt;


public class WebViewActivity extends BaseMVMActivity implements CancelAdapt {


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
    private DefaultWebViewFileChooser mFileChooser;

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

        getLifecycle().addObserver(mWebView);

        configWebSettings(webConfig);
        mWebView.clearHistory();
        mWebView.loadUrl(url);

//
//        mWebView.registerHandler("JSCallNative", new BridgeHandler() {
//
//            @Override
//            public void handler(String data, CallBackFunction function) {
//                System.out.println("registerHandler  handler = JSCallNative, data from web = " + data);
//
//                function.onCallBack("submitFromWeb exe, response data 中文 from Java");
//            }
//
//        });
//
//        Message user = new Message();
//        user.setData("我是Message");
//
//        mWebView.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                mWebView.callHandler("nativeCallJs", new Gson().toJson(user), new CallBackFunction() {
//                    @Override
//                    public void onCallBack(String data) {
//                        System.out.println("nativeCallJs js回复 onCallBack = " + data);
//                    }
//                });
//
//                mWebView.send("hello");
//            }
//        }, 1000L);

    }

    protected void configWebSettings(WebConfig webConfig) {
        WebSettingUtil.toSetting(mWebView, webConfig.getUserAgent());
        mWebView.setHeaders(webConfig.getHeaders());
        mWebView.setWebViewClient(new BaseWebViewClient(mWebView, mWebViewCallBack));

        mFileChooser = new DefaultWebViewFileChooser(this);
        mWebView.setWebChromeClient(new BaseWebChromeClient(mFileChooser, mProgressBar));
    }


    protected WebConfig getWebConfig() {
        return new WebConfig().setShowProgress(true)
                .setShowBack(false)
                .setJsInjectionArrays(new String[]{"android"})
                .setCallBack(new IWebViewCallBack() {
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

                });
    }

//    @Override
//    protected void onResume() {
//        super.onResume();
//        WebViewUtil.onResume(mWebView);
//    }
//
//    @Override
//    protected void onPause() {
//        super.onPause();
//        WebViewUtil.onPause(mWebView);
//    }
//    @Override
//    protected void onDestroy() {
//        WebViewUtil.clearWebView(mWebView);
//        super.onDestroy();
//    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (mFileChooser != null) {
            mFileChooser.onActivityResult(mWebView, requestCode, resultCode, data);
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


}