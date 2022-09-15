package com.mirkowu.mvm.network;

import com.mirkowu.lib_base.view.IBaseView;
import com.mirkowu.lib_network.AbsRxObserver;
import com.mirkowu.lib_network.ErrorBean;
import com.mirkowu.lib_network.ErrorType;
import com.mirkowu.lib_util.utilcode.util.ToastUtils;


import io.reactivex.rxjava3.annotations.NonNull;

public abstract class RxObserver<T> extends AbsRxObserver<T> {
    private IBaseView mIBaseView;

    public RxObserver() {
    }

    public RxObserver(IBaseView baseView) {
        this.mIBaseView = baseView;
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mIBaseView != null) {
            mIBaseView.showLoadingDialog();
        }
    }

    @Override
    public void onFinish() {
        super.onFinish();
        if (mIBaseView != null) {
            mIBaseView.hideLoadingDialog();
        }
    }

    @Override
    public void onFailure(@NonNull ErrorBean error) {
        ToastUtils.showShort(error.code() + ":" + error.msg());
    }

}
