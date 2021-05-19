package com.mirkowu.mvm.mvvm;

import androidx.lifecycle.MutableLiveData;

import com.mirkowu.lib_base.mediator.BaseMediator;
import com.mirkowu.lib_base.view.IBaseView;
import com.mirkowu.lib_util.LogUtil;
import com.mirkowu.lib_base.util.RxLife;
import com.mirkowu.mvm.BizModel;
import com.mirkowu.mvm.network.RxObserver;

public class MVVMMediator extends BaseMediator<IBaseView, BizModel> {

    MutableLiveData<Object> mLiveData = new MutableLiveData<>();
    MutableLiveData<Throwable> mError = new MutableLiveData<>();

    public void loadImage() {
        mModel.loadImage()
                .doOnDispose(() -> LogUtil.d("RxJava 被解绑"))
                .to(RxLife.bindLifecycle(mView))
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

    public void getData() {
        mModel.loadData()
                .doOnDispose(() -> LogUtil.d("RxJava 被解绑"))
                .to(RxLife.bindLifecycle(mView))
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
