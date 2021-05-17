//package com.mirkowu.mvm.base;
//
//import androidx.databinding.DataBindingUtil;
//import androidx.viewbinding.ViewBinding;
//
//import com.mirkowu.lib_core.activity.BaseMVMActivity;
//import com.mirkowu.lib_core.mediator.BaseMediator;
//
//public abstract class BaseBindingActivity<M extends BaseMediator,VB extends ViewBinding> extends BaseMVMActivity<M> {
//protected VB mBinding;
//
//    @Override
//    protected void bindContentView() {
////        super.bindContentView();
//        this.mBinding = (VB) DataBindingUtil.setContentView(this,  getLayoutId());
//        setContentView(mBinding.getRoot());
//
//    }
//
//
////    @Override
////    protected int getLayoutId() {
////        return 0;
////    }
//
//    @Override
//    public void showLoading(String msg) {
//
//    }
//
//    @Override
//    public void hideLoading() {
//
//    }
//
//}
