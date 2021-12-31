package com.mirkowu.mvm.network;

import androidx.annotation.NonNull;

import com.mirkowu.lib_base.view.IBaseView;
import com.mirkowu.lib_network.AbsRxObserver;
import com.mirkowu.lib_network.ErrorType;
import com.mirkowu.lib_util.utilcode.util.ToastUtils;

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
    public void doOnError(Throwable e) {
        super.doOnError(e);
    }

    @Override
    public void onFailure(@NonNull ErrorType type, int code, String msg) {
        ToastUtils.showShort(code + ":" + msg);
    }
}
