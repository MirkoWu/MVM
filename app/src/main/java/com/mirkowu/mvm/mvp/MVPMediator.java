package com.mirkowu.mvm.mvp;

import androidx.annotation.NonNull;

import com.mirkowu.lib_base.mediator.BaseMediator;
import com.mirkowu.lib_base.util.RxLife;
import com.mirkowu.lib_network.ErrorType;
import com.mirkowu.lib_util.LogUtil;
import com.mirkowu.mvm.BizModel;
import com.mirkowu.mvm.network.RxObserver;

import org.jetbrains.annotations.NotNull;

import io.reactivex.rxjava3.functions.Action;

public class MVPMediator extends BaseMediator<IMVPView, BizModel> {


    public void getData() {
        mModel.loadData()
                .doOnDispose(new Action() {
                    @Override
                    public void run() throws Throwable {
                        LogUtil.d("RxJava 被解绑");
                    }
                })
                .to(RxLife.bindLifecycle(mView))
                .subscribe(new RxObserver<Object>() {
                    @Override
                    public void onSuccess(Object data) {
                        mView.onLoadDataSuccess(data);
                    }

                    @Override
                    public void onFailure(@NonNull @NotNull ErrorType errorType, int code, String msg) {

                    }
                });
    }
}
