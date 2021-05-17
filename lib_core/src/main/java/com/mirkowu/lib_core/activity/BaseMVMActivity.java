package com.mirkowu.lib_core.activity;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleOwner;

import com.mirkowu.lib_core.mediator.BaseMediator;
import com.mirkowu.lib_core.view.IBaseView;
import com.mirkowu.lib_core.util.InstanceFactory;


public abstract class BaseMVMActivity<M extends BaseMediator> extends AppCompatActivity implements IBaseView {
    protected M mediator;
//    private LoadingDialog mLoadingDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        onCreateDelegate(savedInstanceState);
    }

    /*** onCreate代理 */
    protected void onCreateDelegate(@Nullable Bundle savedInstanceState) {

        initStatusBar();

        bindContentView();

        createMediator();

        bindMediator();

        initialize();
//        bindCommonObserver();
//        bindObserver();
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

    /*** 创建中间件 */
    protected void createMediator() {
        if (mediator == null) {
            mediator = InstanceFactory.newMediator(this, getClass());
        }
    }

    /*** 绑定中间件 */
    protected void bindMediator() {
        if (mediator != null) {
            mediator.attachView(this);
            //让Mediator拥有View的生命周期感应
            getLifecycle().addObserver(mediator);
        }
    }

    /*** 解绑中间件 */
    private void detachMediator() {
        if (mediator != null) {
            mediator.detachView();
            mediator = null;
        }
    }

    @Override
    protected void onDestroy() {
        detachMediator();
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

    protected abstract int getLayoutId();

    protected abstract void initialize();


}
