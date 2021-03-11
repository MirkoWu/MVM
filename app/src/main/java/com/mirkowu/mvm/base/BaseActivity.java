package com.mirkowu.mvm.base;

import com.mirkowu.lib_mvm.core.BaseMVMActivity;
import com.mirkowu.lib_mvm.core.BaseMediator;

public abstract class BaseActivity<M extends BaseMediator> extends BaseMVMActivity<M> {

    @Override
    public void showLoading(String msg) {

    }

    @Override
    public void hideLoading() {

    }

}
