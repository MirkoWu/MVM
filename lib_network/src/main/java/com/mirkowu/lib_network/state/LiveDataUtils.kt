package com.mirkowu.lib_network.state

import androidx.lifecycle.LifecycleOwner
import com.mirkowu.lib_network.ErrorBean
import com.mirkowu.lib_util.livedata.FixedLiveData


/**
 * 提供类型别名,减少泛型嵌套
 */
typealias ResponseLiveData<T> = FixedLiveData<ResponseData<T>>

/**
 * 简化 LiveData.observe(LifecycleOwner owner,  Observer observer)
 */
//@Deprecated("Use observeRequest")
//inline fun <T> LiveData<ResponseData<T>>.observerRequest(
//    owner: LifecycleOwner,
//    crossinline onLoading: () -> Unit = {},
//    crossinline onFinish: () -> Unit = {},
//    crossinline onSuccess: (T?) -> Unit = {},
//    crossinline onFailure: (ErrorBean) -> Unit = {}
//) {
//    observe(owner, { t ->
//        when (t.state) {
//            RequestState.LOADING -> onLoading()
//            RequestState.SUCCESS -> onSuccess(t.data)
//            RequestState.FAILURE -> onFailure(t.error)
//            RequestState.FINISH -> onFinish()
//        }
//    })
//}

fun <T> ResponseLiveData<T>.observeRequest(
    owner: LifecycleOwner,
    callback: RequestCallback<T>.() -> Unit
) {
    observe(owner) {
        RequestCallback<T>().apply(callback).apply {
            when (it.state) {
                RequestState.LOADING -> onLoading?.invoke()
                RequestState.SUCCESS -> onSuccess?.invoke(it.data)
                RequestState.FAILURE -> onFailure?.invoke(it.error)
                RequestState.FINISH -> onFinish?.invoke()
            }

        }

    }
}

class RequestCallback<T> {
    internal var onLoading: (() -> Unit)? = null
    internal var onSuccess: ((T?) -> Unit)? = null
    internal var onFailure: ((ErrorBean) -> Unit)? = null
    internal var onFinish: (() -> Unit)? = null

    fun onLoading(call: (() -> Unit)?) {
        onLoading = call
    }

    fun onSuccess(call: ((T?) -> Unit)?) {
        onSuccess = call
    }

    fun onFailure(call: ((ErrorBean) -> Unit)?) {
        onFailure = call
    }

    fun onFinish(call: (() -> Unit)?) {
        onFinish = call
    }
}
