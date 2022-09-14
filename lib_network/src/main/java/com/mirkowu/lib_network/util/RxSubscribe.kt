package com.mirkowu.lib_network.util

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import autodispose2.ObservableSubscribeProxy
import com.mirkowu.lib_network.AbsRxObserver
import com.mirkowu.lib_network.ErrorBean
import com.mirkowu.lib_network.ErrorType
import com.mirkowu.lib_network.state.ResponseData
import com.mirkowu.lib_network.state.ResponseLiveData
import com.mirkowu.lib_util.livedata.SingleLiveData
import io.reactivex.rxjava3.core.Observable


fun <T> ObservableSubscribeProxy<T>.subscribe(
    onSuccess: (T?) -> Unit = {},
    onFailure: (ErrorBean) -> Unit = {}
) {
    subscribe(object : AbsRxObserver<T>() {
        override fun onSuccess(data: T) {
            onSuccess(data)
        }

        override fun onFailure(bean: ErrorBean) {
            onFailure(bean)
        }
    })
}

fun <T> ObservableSubscribeProxy<T>.asLiveData(): LiveData<ResponseData<T>> {
    return SingleLiveData<ResponseData<T>>().apply {
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

fun <T> ObservableSubscribeProxy<T>.asResponseLiveData(): ResponseLiveData<T> {
    return ResponseLiveData<T>().apply {
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


fun <T> Observable<T>.subscribe(
    onSuccess: (T?) -> Unit = {},
    onFailure: (ErrorBean) -> Unit = {}
) {
    subscribe(object : AbsRxObserver<T>() {
        override fun onSuccess(data: T) {
            onSuccess(data)
        }

        override fun onFailure(bean: ErrorBean) {
            onFailure(bean)
        }
    })
}

fun <T> Observable<T>.asLiveData(): LiveData<ResponseData<T>> {
    return SingleLiveData<ResponseData<T>>().apply {
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


fun <T> Observable<T>.asResponseLiveData(): ResponseLiveData<T> {
    return ResponseLiveData<T>().apply {
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