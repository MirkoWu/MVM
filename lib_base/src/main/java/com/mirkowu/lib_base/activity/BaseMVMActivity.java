package com.mirkowu.lib_base.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleOwner;

import com.mirkowu.lib_base.mediator.BaseMediator;
import com.mirkowu.lib_base.view.IBaseView;
import com.mirkowu.lib_util.PermissionsUtil;
import com.mirkowu.lib_widget.dialog.LoadingDialog;

/**
 * 更多可配置的
 */
public abstract class BaseMVMActivity<M extends BaseMediator> extends AppCompatActivity implements IBaseView {
    protected M mMediator;
    private LoadingDialog mLoadingDialog;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        onCreateDelegate(savedInstanceState);
    }

    /*** onCreate代理 */
    protected void onCreateDelegate(@Nullable Bundle savedInstanceState) {
        initStatusBar();
        bindContentView();
        bindMediator();
        initialize();
    }

    /*** 绑定布局 */
    protected void bindContentView() {
        setContentView(getLayoutId());
    }

    /*** 初始化状态栏 setContentView 之前*/
    protected void initStatusBar() {
//        //沉浸式状态栏 + 亮色模式
//        StatusBarUtil.setTranslucent(this);
//        StatusBarUtil.setLightMode(this);
    }

    /*** 绑定中间件 */
    protected void bindMediator() {
        //实现方式交给子类
        mMediator = initMediator();

        if (mMediator != null) {
            mMediator.attachView(this);
            //让Mediator拥有View的生命周期感应
            getLifecycle().addObserver(mMediator);
        }
    }

    /*** 解绑中间件 */
    private void detachMediator() {
        if (mMediator != null) {
            mMediator.detachView();
            mMediator = null;
        }
    }

    @Override
    protected void onDestroy() {
        detachMediator();
        super.onDestroy();
    }

    @Override
    public void showLoadingDialog() {
        showLoadingDialog("加载中...");
    }

    @Override
    public void showLoadingDialog(final String msg) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mLoadingDialog != null && mLoadingDialog.isShowing()) {
                    mLoadingDialog.setMessage(msg);
                    return;
                }
                mLoadingDialog = new LoadingDialog(getContext(), msg);
                if (!isFinishing() && !mLoadingDialog.isShowing()) {
                    mLoadingDialog.show();
                }
            }
        });
    }

    @Override
    public void hideLoadingDialog() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mLoadingDialog != null) {
                    mLoadingDialog.cancel();
                    mLoadingDialog = null;
                }
            }
        });
    }

    /**
     * 权限回调
     *
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        PermissionsUtil.getInstance().onRequestPermissionsResult(this, requestCode, permissions, grantResults);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        PermissionsUtil.getInstance().onActivityResult(this, requestCode, resultCode, data);
    }

    @Override
    public Context getContext() {
        return this;
    }

    @Override
    public Activity getActivity() {
        return this;
    }

    @Override
    public LifecycleOwner getLifecycleOwner() {
        return this;
    }

    @Override
    public Lifecycle.Event bindLifecycleUntil() {
        return Lifecycle.Event.ON_DESTROY;
    }

    protected abstract int getLayoutId();

    protected abstract void initialize();

    protected abstract M initMediator();


}
