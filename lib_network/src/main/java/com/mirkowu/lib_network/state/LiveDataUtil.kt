package com.mirkowu.lib_network.state

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import com.mirkowu.lib_network.ErrorBean
import com.mirkowu.lib_util.livedata.SingleLiveData


/**
 * 简化
 * LiveData.observe(LifecycleOwner owner,  Observer observer)
 *
 */
inline fun <T> LiveData<ResponseData<T>>.observerRequest(owner: LifecycleOwner,
                                                         crossinline onLoading: () -> Unit = {},
                                                         crossinline onFinish: () -> Unit = {},
                                                         crossinline onSuccess: (T?) -> Unit = {},
                                                         crossinline onFailure: (ErrorBean) -> Unit = {}) {
    observe(owner, { t ->
        when (t.state) {
            RequestState.LOADING -> onLoading()
            RequestState.SUCCESS -> onSuccess(t.data)
            RequestState.FAILURE -> onFailure(t.error)
            RequestState.FINISH -> onFinish()
        }
    })
}

/**
 * 提供类型别名,减少泛型嵌套
 */
typealias ResponseLiveData<T> = SingleLiveData<ResponseData<T>>