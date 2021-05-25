//package com.mirkowu.lib_webview;
//
//import android.content.Context;
//import android.content.Intent;
//import android.view.ViewGroup;
//import android.widget.FrameLayout;
//import android.widget.ProgressBar;
//
//import com.mirkowu.lib_base.activity.BaseMVMActivity;
//import com.mirkowu.lib_base.mediator.BaseMediator;
//import com.mirkowu.lib_webview.callback.IWebViewCallBack;
//import com.mirkowu.lib_webview.config.WebConfig;
//import com.mirkowu.lib_webview.setting.WebSettingUtil;
//
//public abstract class BaseWebActivity<M extends BaseMediator> extends BaseMVMActivity<M> {
//    public static void start(Context context) {
//        Intent starter = new Intent(context, BaseWebActivity.class);
//        starter.putExtra();
//        context.startActivity(starter);
//    }
//    @Override
//    protected int getLayoutId() {
//        return R.layout.webview_activity_web_view;
//    }
//
//    private CommonWebView mWebView;
//    private ProgressBar mProgressBar;
//    private IWebViewCallBack mWebViewCallBack;
//    @Override
//    protected void initialize() {
//        mWebView = new CommonWebView(getApplicationContext());
//
//        ViewGroup rootView = findViewById(R.id.flRootView);
//        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
//        rootView.addView(mWebView, 0, params);
//
//        mProgressBar = findViewById(R.id.mProgressBar);
////        mStateView = findViewById(R.id.mStateView);
////        mStateView.setOnRefreshListener { onRefresh() }
////        initWebView(mWebView)
//
//        WebConfig webConfig=getWebConfig();
//
//        mWebView.addJavascriptInterface(mWebViewCallBack,webConfig.getJsInjectionArrays());
//        WebSettingUtil.toSetting(mWebView, webConfig.getUserAgent());
//
//        mWebView.loadUrl();
//
//
//    }
//
//    protected abstract WebConfig getWebConfig();
//
//}
