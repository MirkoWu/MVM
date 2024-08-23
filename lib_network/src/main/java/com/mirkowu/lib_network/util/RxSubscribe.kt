package com.mirkowu.lib_network.util

import autodispose2.ObservableSubscribeProxy
import com.mirkowu.lib_network.AbsRxObserver
import com.mirkowu.lib_network.ErrorBean
import com.mirkowu.lib_network.state.RequestCallback
import com.mirkowu.lib_network.state.ResponseData
import com.mirkowu.lib_network.state.ResponseLiveData
import io.reactivex.rxjava3.core.Observable


fun <T> ObservableSubscribeProxy<T>.subscribeRequest(callback: RequestCallback<T>.() -> Unit) {
    RequestCallback<T>().apply(callback).apply {
        subscribe(object : AbsRxObserver<T>() {
            override fun onStart() {
                super.onStart()
                onLoading?.invoke()
            }

            override fun onFinish() {
                super.onFinish()
                onFinish?.invoke()
            }

            override fun onSuccess(data: T) {
                onSuccess?.invoke(data)
            }

            override fun onFailure(bean: ErrorBean) {
                onFailure?.invoke(bean)
            }
        })
    }
}

fun <T : Any> Observable<T>.subscribeRequest(callback: RequestCallback<T>.() -> Unit) {
    RequestCallback<T>().apply(callback).apply {
        subscribe(object : AbsRxObserver<T>() {
            override fun onStart() {
                super.onStart()
                onLoading?.invoke()
            }

            override fun onFinish() {
                super.onFinish()
                onFinish?.invoke()
            }

            override fun onSuccess(data: T) {
                onSuccess?.invoke(data)
            }

            override fun onFailure(bean: ErrorBean) {
                onFailure?.invoke(bean)
            }
        })
    }
}


fun <T> ObservableSubscribeProxy<T>.asResponseLiveData(liveData: ResponseLiveData<T>? = null): ResponseLiveData<T> {
    return (liveData ?: ResponseLiveData<T>()).apply {
        subscribe(object : AbsRxObserver<T>() {
            override fun onStart() {
                super.onStart()
                value = ResponseData.loading()
            }

            override fun onFinish() {
                super.onFinish()
                value = ResponseData.finish()
            }

            override fun onSuccess(data: T?) {
                value = ResponseData.success(data)
            }

            override fun onFailure(bean: ErrorBean) {
                value = ResponseData.error(bean)
            }
        })
    }
}

fun <T : Any> Observable<T>.asResponseLiveData(liveData: ResponseLiveData<T>? = null): ResponseLiveData<T> {
    return (liveData ?: ResponseLiveData<T>()).apply {
        subscribe(object : AbsRxObserver<T>() {
            override fun onStart() {
                super.onStart()
                value = ResponseData.loading()
            }

            override fun onFinish() {
                super.onFinish()
                value = ResponseData.finish()
            }

            override fun onSuccess(data: T?) {
                value = ResponseData.success(data)
            }

            override fun onFailure(bean: ErrorBean) {
                value = ResponseData.error(bean)
            }
        })
    }
}