package com.mirkowu.mvm.mvvm;

import androidx.lifecycle.MutableLiveData;

import com.mirkowu.lib_core.mediator.BaseMediator;
import com.mirkowu.lib_core.view.IBaseView;
import com.mirkowu.lib_core.util.LogUtil;
import com.mirkowu.lib_core.util.RxLife;
import com.mirkowu.mvm.BizModel;
import com.mirkowu.mvm.network.RxObserver;

import io.reactivex.rxjava3.functions.Action;

public class MVVMMediator extends BaseMediator<IBaseView, BizModel> {

    MutableLiveData<Object> mLiveData = new MutableLiveData<>();
    MutableLiveData<Throwable> mError = new MutableLiveData<>();

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
                        mLiveData.setValue(data);
                    }

                    @Override
                    public void onFailure(Throwable e) {
                        mError.setValue(e);
                    }
                });
    }
}
