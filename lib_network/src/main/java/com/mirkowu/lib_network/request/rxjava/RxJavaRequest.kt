package com.mirkowu.lib_network.request.rxjava

import autodispose2.ObservableSubscribeProxy
import com.mirkowu.lib_network.AbsRxObserver
import com.mirkowu.lib_network.request.ErrorData
import com.mirkowu.lib_network.request.RequestCallback
import com.mirkowu.lib_network.request.RequestData
import com.mirkowu.lib_network.request.RequestLiveData
import io.reactivex.rxjava3.core.Observable

fun <T> ObservableSubscribeProxy<T>.request(callback: RequestCallback<T>.() -> Unit) {
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

            override fun onFailure(bean: ErrorData) {
                onFailure?.invoke(bean)
            }
        })
    }
}

fun <T : Any> Observable<T>.request(callback: RequestCallback<T>.() -> Unit) {
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

            override fun onFailure(bean: ErrorData) {
                onFailure?.invoke(bean)
            }
        })
    }
}


fun <T> ObservableSubscribeProxy<T>.asRequestLiveData(liveData: RequestLiveData<T>? = null): RequestLiveData<T> {
    return (liveData ?: RequestLiveData<T>()).apply {
        subscribe(object : AbsRxObserver<T>() {
            override fun onStart() {
                super.onStart()
                value = RequestData.loading()
            }

            override fun onFinish() {
                super.onFinish()
                value = RequestData.finish()
            }

            override fun onSuccess(data: T?) {
                value = RequestData.success(data)
            }

            override fun onFailure(bean: ErrorData) {
                value = RequestData.failure(bean)
            }
        })
    }
}

fun <T : Any> Observable<T>.asRequestLiveData(liveData: RequestLiveData<T>? = null): RequestLiveData<T> {
    return (liveData ?: RequestLiveData<T>()).apply {
        subscribe(object : AbsRxObserver<T>() {
            override fun onStart() {
                super.onStart()
                value = RequestData.loading()
            }

            override fun onFinish() {
                super.onFinish()
                value = RequestData.finish()
            }

            override fun onSuccess(data: T?) {
                value = RequestData.success(data)
            }

            override fun onFailure(bean: ErrorData) {
                value = RequestData.failure(bean)
            }
        })
    }
}