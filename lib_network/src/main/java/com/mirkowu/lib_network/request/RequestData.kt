package com.mirkowu.lib_network.request

import androidx.annotation.Keep
import androidx.lifecycle.MutableLiveData

@Keep
enum class RequestState {
    LOADING, SUCCESS, FAILURE, FINISH
}

/**
 * 提供类型别名,减少泛型嵌套
 */
typealias RequestLiveData<T> = MutableLiveData<RequestData<T>>

@Keep
data class RequestData<T> @JvmOverloads private constructor(
    val state: RequestState,
    var data: T? = null,
    var error: ErrorData? = null,
) {

    val isSuccess: Boolean
        get() = state === RequestState.SUCCESS
    val isFailure: Boolean
        get() = state === RequestState.FAILURE
    val isLoading: Boolean
        get() = state === RequestState.LOADING
    val isFinish: Boolean
        get() = state === RequestState.FINISH

    companion object {
        fun <T> loading(): RequestData<T> {
            return RequestData(RequestState.LOADING)
        }

        fun <T> finish(): RequestData<T> {
            return RequestData(RequestState.FINISH)
        }

        fun <T> success(data: T): RequestData<T> {
            return RequestData(RequestState.SUCCESS, data)
        }

        fun <T> failure(bean: ErrorData): RequestData<T> {
            return RequestData(RequestState.FAILURE, null, bean)
        }

        fun <T> failure(
            type: ErrorType,
            code: String,
            msg: String?,
            e: Throwable?
        ): RequestData<T> {
            return RequestData(RequestState.FAILURE, null, ErrorData(type, code, msg, e))
        }
    }
}