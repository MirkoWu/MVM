package com.mirkowu.lib_mvm.core;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleOwner;


public abstract class BaseMVMActivity<M extends BaseMediator> extends AppCompatActivity implements IBaseView {
    protected M mediator;
//    private LoadingDialog mLoadingDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        onCreateDelegate(savedInstanceState);
    }

    /**
     * onCreate代理
     *
     * @param savedInstanceState
     */
    protected void onCreateDelegate(@Nullable Bundle savedInstanceState) {

        initStatusBar();

        bindContentView();

        createMediator();

        bindMediator();

        initialize();
//        bindCommonObserver();
//        bindObserver();
    }

    protected void bindContentView() {
        setContentView(setLayoutId());
    }

    protected void initStatusBar() {
//        //沉浸式状态栏 + 亮色模式
//        StatusBarUtil.setTranslucent(this);
//        StatusBarUtil.setLightMode(this);
    }

    protected void createMediator() {
        if (mediator == null) {
            mediator = InstanceFactory.newMediator(this, getClass());
        }
    }

    protected void bindMediator() {
        if (mediator != null) {
            mediator.attachView(this);
            //让Mediator拥有View的生命周期感应
            getLifecycle().addObserver(mediator);
        }
    }

    @Override
    protected void onDestroy() {
        if (mediator != null) {
            mediator.detachView();
            mediator=null;
        }
        super.onDestroy();
    }

//    protected abstract void bindObserver();

//    private void bindCommonObserver() {
//        mViewModel.mCommonError.observe(this, new Observer<ApiException>() {
//            @Override
//            public void onChanged(ApiException api) {
//                onCommonError(api);
//            }
//        });
//    }
//
//    protected void onCommonError(ApiException api) {
////        ToastUtil.showToast(String.format("%s(%d)", api.getMessage(), api.code));
//        ToastUtil.showToast(String.format("%s", api.getMessage()));
//    }


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

    protected abstract int setLayoutId();

    protected abstract void initialize();


}
