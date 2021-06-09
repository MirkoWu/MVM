package com.mirkowu.mvm.mvvm;

import androidx.lifecycle.MutableLiveData;

import com.mirkowu.lib_network.ErrorBean;
import com.mirkowu.lib_base.mediator.BaseMediator;
import com.mirkowu.lib_base.view.IBaseView;
import com.mirkowu.lib_network.ErrorType;
import com.mirkowu.lib_network.state.ResponseData;
import com.mirkowu.lib_util.LogUtil;
import com.mirkowu.lib_base.util.RxLife;
import com.mirkowu.mvm.BizModel;
import com.mirkowu.mvm.bean.GankBaseBean;
import com.mirkowu.mvm.bean.GankImageBean;
import com.mirkowu.mvm.network.RxObserver;

import java.util.List;

public class MVVMMediator extends BaseMediator<IBaseView, BizModel> {

    MutableLiveData<Object> mLiveData = new MutableLiveData<>();
    MutableLiveData<Throwable> mError = new MutableLiveData<>();
    MutableLiveData<ResponseData<List<GankImageBean>>> mRequestImageListData = new MutableLiveData<>();
    MutableLiveData<ErrorBean> mImageError = new MutableLiveData<>();

    public void loadImage(int page, int pageSize) {
        mModel.loadImage(page, pageSize)
                .doOnDispose(() -> LogUtil.d("RxJava 被解绑"))
                .to(RxLife.bindLifecycle(mView))
                .subscribe(new RxObserver<GankBaseBean<List<GankImageBean>>>() {
                    @Override
                    public void onSuccess(GankBaseBean<List<GankImageBean>> data) {
                        if (data.isSuccess()) {
                            mRequestImageListData.setValue(ResponseData.success(data.data));
                        }
                    }

                    @Override
                    public void onFailure(ErrorType type, int code, String msg) {
                        mRequestImageListData.setValue(ResponseData.error(type, code, msg));
                    }
                });
    }

    public void loadImage2() {
        mModel.loadImage2()
                .doOnDispose(() -> LogUtil.d("RxJava 被解绑"))
                .to(RxLife.bindLifecycle(mView))
                .subscribe(new RxObserver<Object>() {
                    @Override
                    public void onSuccess(Object data) {
                        mLiveData.setValue(data);
                    }

                    @Override
                    public void onFailure(ErrorType errorType, int code, String msg) {
                        mImageError.setValue(new ErrorBean(errorType, code, msg));
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
                    public void onFailure(ErrorType errorType, int code, String msg) {


//                        mError.setValue(e);
                    }


                });
    }
}
