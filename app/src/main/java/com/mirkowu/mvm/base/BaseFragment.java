package com.mirkowu.mvm.base;

import com.mirkowu.lib_base.fragment.BaseMVMFragment;
import com.mirkowu.lib_base.mediator.BaseMediator;
import com.mirkowu.lib_base.util.InstanceFactory;

public abstract class BaseFragment<M extends BaseMediator> extends BaseMVMFragment<M> {
    @Override
    protected M initMediator() {
        if (mMediator == null) {
            mMediator = InstanceFactory.newMediator(this, getClass());
        }
        return mMediator;
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
}
