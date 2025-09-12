package com.mirkowu.lib_network.request


/**
 * 回调顺序 loading -> finish -> success/fail
 * loading / finish 主要用于UI处理
 */
class RequestCallback<T> {
    internal var onLoading: (() -> Unit)? = null
    internal var onSuccess: ((T) -> Unit)? = null
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

    fun success(call: ((T) -> Unit)?) {
        onSuccess = call
    }

    fun fail(call: ((ErrorData) -> Unit)?) {
        onFailure = call
    }

}
