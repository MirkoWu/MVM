package com.mirkowu.lib_base.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.TypedArray;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleOwner;

import com.mirkowu.lib_base.R;
import com.mirkowu.lib_base.mediator.BaseMediator;
import com.mirkowu.lib_base.view.IBaseView;
import com.mirkowu.lib_util.LogUtil;
import com.mirkowu.lib_widget.dialog.LoadingDialog;

import java.lang.reflect.Method;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * MVM base 示例
 * 可自行实现接口，重写
 */
public abstract class BaseMVMActivity<M extends BaseMediator> extends AppCompatActivity implements IBaseView {

    @NonNull
    protected M mMediator;
    private LoadingDialog mLoadingDialog;

    private AtomicInteger mLoading = new AtomicInteger(0);


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        onCreateDelegate(savedInstanceState);
    }

    /*** onCreate代理 */
    protected void onCreateDelegate(@Nullable Bundle savedInstanceState) {
        bindContentView();
        bindMediator();
        initStatusBar();
        initialize(savedInstanceState);
        initialize();
    }

    /*** 绑定布局 */
    protected abstract void bindContentView();


    /*** 初始化状态栏 setContentView 之前*/
    protected void initStatusBar() {
        //开启亮色模式
//        BarUtils.setStatusBarLightMode(this,true);
        //沉浸式状态栏
//        BarUtils.transparentStatusBar(this);
    }

    protected void initialize(@Nullable Bundle savedInstanceState) {
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
        showLoadingDialog(getString(R.string.widget_loading));
    }

    @Override
    public void showLoadingDialog(final String msg) {
        runOnUiThread(() -> {
            if (mLoading.getAndIncrement() != 0 && mLoadingDialog != null) {
                mLoadingDialog.setMessage(msg);
                return;
            }

            if (!isFinishing()) {
                mLoadingDialog = new LoadingDialog(msg);
                mLoadingDialog.setOnDismissListener(dialog -> {
                    hideLoadingDialog();
                });
                mLoadingDialog.showAllowingStateLoss(getSupportFragmentManager());
            }
        });
    }

    @Override
    public void hideLoadingDialog() {
        runOnUiThread(() -> {
            if (mLoading.get() > 0 && mLoading.decrementAndGet() == 0 && mLoadingDialog != null) {
                mLoadingDialog.setOnDismissListener(null);
                mLoadingDialog.dismissAllowingStateLoss();
                mLoadingDialog = null;
            }
        });
    }

    @Override
    public void setRequestedOrientation(int requestedOrientation) {
        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.O && isTranslucentOrFloating()) {
            LogUtil.d("avoid calling setRequestedOrientation when Oreo.");
            return;
        }
        super.setRequestedOrientation(requestedOrientation);
    }

    /**
     * 是否是透明的Activity
     * 8.0透明Activity不允许旋转屏幕
     *
     * @return
     */
    private boolean isTranslucentOrFloating() {
        boolean isTranslucentOrFloating = false;
        try {
            int[] styleableRes = (int[]) Class.forName("com.android.internal.R$styleable")
                    .getField("Window").get(null);
            TypedArray ta = obtainStyledAttributes(styleableRes);
            Method m = ActivityInfo.class.getMethod("isTranslucentOrFloating", TypedArray.class);
            m.setAccessible(true);
            isTranslucentOrFloating = (boolean) m.invoke(null, ta);
            m.setAccessible(false);
        } catch (Exception e) {
            LogUtil.e(e);
        }

        return isTranslucentOrFloating;
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

    protected abstract void initialize();

    protected abstract M initMediator();

}
