package com.mirkowu.lib_network.request

import androidx.lifecycle.LifecycleOwner


fun <T> RequestLiveData<T>.request(
    owner: LifecycleOwner,
    callback: RequestCallback<T>.() -> Unit
) {
    observe(owner) { it.invoke(callback) }
}

internal fun <T> RequestData<T>.invoke(callbackFun: RequestCallback<T>.() -> Unit) {
    RequestCallback<T>().apply(callbackFun).apply {
        when (state) {
            RequestState.LOADING -> onLoading?.invoke()
            RequestState.SUCCESS -> {
                onFinish?.invoke()
                onSuccess?.invoke(data)
            }

            RequestState.FAILURE -> {
                onFinish?.invoke()
                error?.let {
                    onFailure?.invoke(it)
                }
            }

            //finish要放在success和fail前，这里的不会再调用
            RequestState.FINISH -> onFinish?.invoke()
        }
    }
}

/**
 * 回调顺序 loading -> finish -> success/fail
 * loading / finish 主要用于UI处理
 */
class RequestCallback<T> {
    internal var onLoading: (() -> Unit)? = null
    internal var onSuccess: ((T?) -> Unit)? = null
    internal var onFailure: ((ErrorData) -> Unit)? = null
    internal var onFinish: (() -> Unit)? = null

    fun loading(call: (() -> Unit)?) {
        onLoading = call
    }

    /**
     * 在 success/fail 前调用，主要用于UI处理
     */
    fun finish(call: (() -> Unit)?) {
        onFinish = call
    }

    fun success(call: ((T?) -> Unit)?) {
        onSuccess = call
    }

    fun fail(call: ((ErrorData) -> Unit)?) {
        onFailure = call
    }

}
