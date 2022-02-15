package com.mirkowu.mvm.base;


import android.content.res.Resources;

import androidx.annotation.NonNull;

import com.mirkowu.lib_base.activity.BaseMVMActivity;
import com.mirkowu.lib_base.mediator.BaseMediator;
import com.mirkowu.lib_base.util.InstanceFactory;
import com.mirkowu.lib_screen.AutoSizeManager;
import com.mirkowu.lib_screen.internal.IAutoAdapt;
import com.mirkowu.lib_util.utilcode.util.BarUtils;

public abstract class BaseActivity<M extends BaseMediator> extends BaseMVMActivity<M> implements IAutoAdapt {

    @Override
    protected M initMediator() {
        //自己决定是用反射 还是new手动创建
        if (mMediator == null) {
            mMediator = InstanceFactory.newMediator(this, getClass());
        }
        return mMediator;
    }

    @Override
    protected void initStatusBar() {
        BarUtils.setStatusBarLightMode(this, true);
    }
//    @Override
//    public void showStateView(StateBean bean) {
//
//    }
//
//    @Override
//    public void hideStateView() {
//
//    }
//
//    @Override
//    public void onFailure() {
//
//    }

    /**
     * 也可以放在Base中
     */
    //protected VB mBinding;
//
//    @Override
//    protected void bindContentView() {
////        super.bindContentView();
//        this.mBinding = (VB) DataBindingUtil.setContentView(this,  getLayoutId());
//        setContentView(mBinding.getRoot());
//
//    }
    @Override
    public Resources getResources() {
        //这个地方慎用，可能会导致在不恰当的时间 调用
        //    adaptScreenSize(super.getResources());
        return super.getResources();
    }

    //横屏时 以短边为准
//    override fun isBaseOnWidth() = false
//    override fun getSizeInDp() = 375f

    //横屏时 以长边为准
//    override fun isBaseOnWidth() = true
//    override fun getSizeInDp() = 812f
//横竖屏切换时，这里的参数也要变动


    protected void adaptScreenSize(@NonNull Resources superResources) {
        AutoSizeManager.autoConvertActivity(super.getResources(), this); //如果没有自定义需求用这个方法
    }
}
