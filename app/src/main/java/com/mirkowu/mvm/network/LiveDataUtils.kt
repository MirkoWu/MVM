package com.mirkowu.mvm.network

import autodispose2.ObservableSubscribeProxy
import com.mirkowu.lib_network.AbsRxObserver
import com.mirkowu.lib_network.request.ApiException
import com.mirkowu.lib_network.request.ErrorData
import com.mirkowu.lib_network.request.ErrorType
import com.mirkowu.lib_network.request.RequestData
import com.mirkowu.lib_network.request.RequestLiveData
import com.mirkowu.mvm.bean.GankBaseBean


fun <T> ObservableSubscribeProxy<GankBaseBean<T>>.asResponseLiveData(liveData: RequestLiveData<T>? = null): RequestLiveData<T> {
    return (liveData ?: RequestLiveData<T>()).apply {
        subscribe(object : AbsRxObserver<GankBaseBean<T>>() {
            override fun onStart() {
                super.onStart()
                value = RequestData.loading()
            }

            override fun onFinish() {
                super.onFinish()
                value = RequestData.finish()
            }

            override fun onSuccess(data: GankBaseBean<T>?) {
                data?.let { it ->
                    value = if (it.isSuccess) {
                        RequestData.success(it.data)
                    } else {
                        RequestData.failure(
                            ErrorType.API,
                            it.code.toString(),
                            "",
                            ApiException(
                                it.code.toString(),
                                ""
                            )
                        )
                    }
                }
            }

            override fun onFailure(bean: ErrorData) {
                value = RequestData.failure(bean)
            }
        })
    }
}