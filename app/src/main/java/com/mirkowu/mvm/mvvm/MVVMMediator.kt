package com.mirkowu.mvm.mvvm

import androidx.lifecycle.MutableLiveData
import com.mirkowu.lib_base.mediator.BaseMediator
import com.mirkowu.lib_base.util.RxLife
import com.mirkowu.lib_base.util.RxScheduler
import com.mirkowu.lib_base.view.IBaseView
import com.mirkowu.lib_network.ErrorBean
import com.mirkowu.lib_network.ErrorType
import com.mirkowu.lib_network.state.ResponseData
import com.mirkowu.lib_network.state.ResponseLiveData
import com.mirkowu.lib_util.LogUtil
import com.mirkowu.lib_util.utilcode.util.NetworkUtils
import com.mirkowu.mvm.BizModel
import com.mirkowu.mvm.bean.GankBaseBean
import com.mirkowu.mvm.bean.GankImageBean
import com.mirkowu.mvm.network.RxObserver
import io.reactivex.rxjava3.core.Observable

class MVVMMediator : BaseMediator<IBaseView?, BizModel?>() {
    var mLiveData = MutableLiveData<Any>()
    var mError = MutableLiveData<Throwable>()

    @JvmField
    var mRequestImageListData = MutableLiveData<ResponseData<List<GankImageBean>>>()

    @JvmField
    var mImageError = MutableLiveData<ErrorBean>()

    var mPingResult = ResponseLiveData<Boolean>()


    fun loadImage(page: Int, pageSize: Int) {
        mModel.loadImage(page, pageSize)
            .doOnDispose { LogUtil.d("RxJava 被解绑") }
            .to(RxLife.bindLifecycle(mView))
            .subscribe(object : RxObserver<GankBaseBean<List<GankImageBean>>>() {
                override fun onSuccess(data: GankBaseBean<List<GankImageBean>>) {
                    if (data.isSuccess) {
                        mRequestImageListData.setValue(ResponseData.success(data.data))
                    }
                }

                override fun onFailure(type: ErrorType, code: Int, msg: String) {
                    mRequestImageListData.setValue(ResponseData.error(type, code, msg))
                }
            })
        loadImage2()
    }

    fun loadImage2() {
        mModel.loadImage2()
            .doOnDispose { LogUtil.d("RxJava 被解绑") }
            .to(RxLife.bindLifecycle(mView))
            .subscribe(object : RxObserver<Any>() {
                override fun onSuccess(data: Any) {
                    mLiveData.setValue(data)
                }

                override fun onFailure(errorType: ErrorType, code: Int, msg: String) {
                    mImageError.setValue(ErrorBean(errorType, code, msg))
                }
            })
    }

    //                        mError.setValue(e);
    val data: Unit
        get() {
            mModel.loadData()
                .doOnDispose { LogUtil.d("RxJava 被解绑") }
                .to(RxLife.bindLifecycle(mView))
                .subscribe(object : RxObserver<Any>() {
                    override fun onSuccess(data: Any) {
                        mLiveData.setValue(data)
                    }

                    override fun onFailure(errorType: ErrorType, code: Int, msg: String) {


//                        mError.setValue(e);
                    }
                })
        }

    fun getPing() {
        Observable.create<Boolean> {
            val result = NetworkUtils.isAvailableByPing("baidu.com")
            it.onNext(result)
            it.onComplete()
        }
            .compose(RxScheduler.ioToMain())
            .to(RxLife.bindLifecycle(mView))
            .subscribe(object : RxObserver<Boolean>() {
                override fun onSuccess(data: Boolean) {
                    mPingResult.value = ResponseData.success(data)
                }

                override fun onFailure(errorType: ErrorType, code: Int, msg: String) {
                    mPingResult.value = ResponseData.error(errorType, code, msg)
                }
            })
    }
}