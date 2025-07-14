package com.mirkowu.lib_webview;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.KeyEvent;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.mirkowu.lib_base.activity.BaseMVMActivity;
import com.mirkowu.lib_base.mediator.BaseMediator;
import com.mirkowu.lib_util.utilcode.util.BarUtils;
import com.mirkowu.lib_webview.config.WebConfig;
import com.mirkowu.lib_webview.util.WebViewUtil;


/**
 * 通用的WebView
 * 1.支持X5
 * 2.支持JsBridge
 * <p>
 * 如果需要自定义的，可继承此Activity 或使用 {@link CommonWebFragment}
 * 如需开启多进程，请记得在AndroidManifest.xlm中注册 process
 */
public class CommonWebActivity extends BaseMVMActivity {
    private static String FRAGMENT_TAG = "com.mirkowu.lib.webview.fragment";

    public static final String KEY_TITLE = "title";
    public static final String KEY_URL = "url";

    public static void start(Context context, String title, String url) {
        start(context, title, url, WebViewUtil.getUseMultiProcess());
    }

    public static void start(Context context, String title, String url, boolean newProcess) {
        if (TextUtils.isEmpty(url)) {
            return;
        }
        Intent starter;
        if (newProcess) {
            starter = new Intent(context, CommonWebMultiProcessActivity.class);
            starter.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        } else {
            starter = new Intent(context, CommonWebActivity.class);
        }
        starter.putExtra(KEY_TITLE, title);
        starter.putExtra(KEY_URL, url);
        context.startActivity(starter);
    }

    protected CommonWebFragment webFragment;

    @Override
    protected void bindContentView() {
        setContentView(R.layout.webview_layout_container);
    }

    @Override
    protected BaseMediator initMediator() {
        return null;
    }

    @Override
    protected void initStatusBar() {
        BarUtils.setStatusBarLightMode(this, true);
    }

    @Override
    protected void initialize() {
        String title = getIntent().getStringExtra(KEY_TITLE);
        String url = getIntent().getStringExtra(KEY_URL);
        loadWebFragment(title, url);
    }

    protected void loadWebFragment(String title, String url) {
        Fragment fragment = getSupportFragmentManager().findFragmentByTag(FRAGMENT_TAG);
        if (fragment != null && fragment instanceof CommonWebFragment) {
            webFragment = (CommonWebFragment) fragment;
        } else {
            webFragment = CommonWebFragment.newInstance(title, url);
        }
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        if (webFragment.isAdded()) {
            transaction.show(webFragment).commit();
        } else {
            transaction.add(R.id.fl_container, webFragment, FRAGMENT_TAG).commit();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //由于处理都是用的activity的context，所以这里要手动调用
        webFragment.handleWebFileChooserResult(requestCode, resultCode, data);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && handleWebBack()) {
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onBackPressed() {
        if (handleWebBack()) {
            return;
        }
        super.onBackPressed();
    }

    /**
     * 处理返回键
     *
     * @return
     */
    protected boolean handleWebBack() {
        return webFragment.handleWebBack();
    }

}