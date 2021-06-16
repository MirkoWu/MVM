package com.mirkowu.mvm.base;


import com.mirkowu.lib_base.activity.BaseMVMActivity;
import com.mirkowu.lib_base.mediator.BaseMediator;
import com.mirkowu.lib_base.util.InstanceFactory;
import com.mirkowu.lib_util.utilcode.util.BarUtils;

public abstract class BaseActivity<M extends BaseMediator> extends BaseMVMActivity<M> {

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
}
