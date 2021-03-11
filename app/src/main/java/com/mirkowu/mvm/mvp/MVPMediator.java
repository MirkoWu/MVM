package com.mirkowu.mvm.mvp;

import com.mirkowu.lib_mvm.core.BaseMediator;
import com.mirkowu.lib_mvm.util.LogUtil;
import com.mirkowu.lib_mvm.util.RxLife;
import com.mirkowu.mvm.BizModel;
import com.mirkowu.mvm.network.RxObserver;

import io.reactivex.rxjava3.functions.Action;

public class MVPMediator extends BaseMediator<IMVPView, BizModel> {


    public void getData() {
        model.loadData()
                .doOnDispose(new Action() {
                    @Override
                    public void run() throws Throwable {
                        LogUtil.d("RxJava 被解绑");
                    }
                })
                .to(RxLife.bindLifecycle(view))
                .subscribe(new RxObserver<Object>() {
                    @Override
                    public void onSuccess(Object data) {
                        view.onLoadDataSuccess(data);
                    }

                    @Override
                    public void onFailure(Throwable e) {
                        view.onLoadDataError(e);
                    }
                });
    }
}
