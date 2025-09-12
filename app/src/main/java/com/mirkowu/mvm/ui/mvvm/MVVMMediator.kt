package com.mirkowu.mvm.ui.mvvm

import android.util.Log
import com.mirkowu.lib_base.mediator.BaseMediator
import com.mirkowu.lib_base.util.RxLife
import com.mirkowu.lib_base.util.RxScheduler
import com.mirkowu.lib_base.view.IBaseView
import com.mirkowu.lib_network.request.ErrorData
import com.mirkowu.lib_network.request.rxjava.asRequestLiveData
import com.mirkowu.lib_network.request.RequestData
import com.mirkowu.lib_network.request.RequestLiveData
import com.mirkowu.lib_util.LogUtil
import com.mirkowu.lib_util.MmkvStorage
import com.mirkowu.lib_util.livedata.FixedLiveData
import com.mirkowu.lib_util.utilcode.util.NetworkUtils
import com.mirkowu.mvm.BizModel
import com.mirkowu.mvm.bean.GankBaseBean
import com.mirkowu.mvm.bean.ImageBean
import com.mirkowu.mvm.bean.ImageListBean
import com.mirkowu.mvm.bean.RandomImageBean
import com.mirkowu.mvm.network.ImageClient
import com.mirkowu.mvm.network.RxObserver
import com.mirkowu.mvm.network.asResponseLiveData
import io.reactivex.rxjava3.core.Observable
import kotlinx.coroutines.flow.Flow

open class MVVMMediator : BaseMediator<IBaseView, BizModel>() {
    @JvmField
    val mImageData = RequestLiveData<List<RandomImageBean>?>()
    var mLiveData = FixedLiveData<Any>()
    var mError = FixedLiveData<Throwable>()

    @JvmField
    var mRequestImageListData = RequestLiveData<List<ImageBean>>()

    @JvmField
    var imageBean = RequestLiveData<ImageListBean>()

    @JvmField
    var mImageError = FixedLiveData<ErrorData>()

    var mPingResult = RequestLiveData<Boolean>()

    val ssp by lazy { MmkvStorage("sss") }
    fun loadImage(page: Int, pageSize: Int) {
        mModel.loadImage(page, pageSize)
            .doOnDispose { LogUtil.d("RxJava 被解绑") }
            .to(RxLife.bindLifecycle(mView))
            .subscribe(object : RxObserver<GankBaseBean<ImageListBean>>() {
                override fun onSuccess(data: GankBaseBean<ImageListBean>) {
                    if (data.isSuccess) {
                        mRequestImageListData.setValue(RequestData.success(data.data.list))
                    }
                }

                override fun onFailure(bean: ErrorData) {
                    mRequestImageListData.setValue(RequestData.failure(bean))
                }
            })
        //  loadImage2()
    }

    fun loadImage2(
        page: Int,
        pageSize: Int
    ): Flow<GankBaseBean<ImageListBean>> {
        return flow {
            emit(GankBaseBean<ImageListBean>())
        }
    }

    fun loadImageAsLiveData(
        page: Int,
        pageSize: Int
    ): RequestLiveData<ImageListBean> {
        return mModel.loadImage(page, pageSize)
            .doOnDispose { LogUtil.d("RxJava 被解绑") }

            .to(RxLife.bindLifecycle(mView))
//            .subscribeRequest {
//                onSuccess{ }
//                onFailure {  }
//            }
//            .asLiveData()
//            .asResponseLiveData(gankImageBean)

            .asResponseLiveData(imageBean)
//            .observeRequest(mView.lifecycleOwner) {
//                onLoading { LogUtil.e("asResponseLiveData onLoading") }
//                onFinish { LogUtil.e("asResponseLiveData onFinish") }
//            }
    }

    fun loadImage2() {
        //todo  这里不用model层也是可以的，直接把api 放到mediator中
        //mModel.loadImage2()
        ImageClient.getImageApi()
            .getRandomImage()
            .compose(RxScheduler.ioToMain())
            .doOnDispose { LogUtil.d("RxJava 被解绑") }
            .to(RxLife.bindLifecycle(mView))
            .subscribe(object : RxObserver<List<RandomImageBean>>() {
                override fun onSuccess(data: List<RandomImageBean>) {
                    mImageData.setValue(RequestData.success(data))
                }


                override fun onFailure(bean: ErrorData) {
                    mImageData.setValue(RequestData.failure(bean))
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

                    override fun onFailure(bean: ErrorData) {


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
//            .subscribe(
//                onSuccess = {
//                    mPingResult.value = ResponseData.success(it)
//                },
//                onFailure = {
//                    mPingResult.value = ResponseData.error(it)
//                })
            .subscribe(object : RxObserver<Boolean>() {
                override fun onStart() {
                    super.onStart()
                    mPingResult.value = RequestData.loading()
                }

                override fun onFinish() {
                    super.onFinish()
                    Log.e(
                        "xxx",
                        "set  -- onFinish hasActiveObservers=" + mPingResult.hasActiveObservers()
                    )
                    mPingResult.value = RequestData.finish()

                }

                override fun onSuccess(data: Boolean) {
                    mPingResult.value = RequestData.success(data)
                }

                override fun onFailure(bean: ErrorData) {
                    mPingResult.value = RequestData.failure(bean)
                }
            })
    }

    fun getPing2LiveData(): RequestLiveData<Boolean> {
        return Observable.create<Boolean> {
            val result = NetworkUtils.isAvailableByPing("baidu.com")
            it.onNext(result)
            it.onComplete()
        }
            .compose(RxScheduler.ioToMain())
            .to(RxLife.bindLifecycle(mView))
            .asRequestLiveData()
    }
}