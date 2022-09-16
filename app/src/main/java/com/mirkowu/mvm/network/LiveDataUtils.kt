package com.mirkowu.mvm.network

import autodispose2.ObservableSubscribeProxy
import com.mirkowu.lib_network.AbsRxObserver
import com.mirkowu.lib_network.ApiException
import com.mirkowu.lib_network.ErrorBean
import com.mirkowu.lib_network.ErrorType
import com.mirkowu.lib_network.state.ResponseData
import com.mirkowu.lib_network.state.ResponseLiveData
import com.mirkowu.mvm.bean.GankBaseBean


fun <T> ObservableSubscribeProxy<GankBaseBean<T>>.asResponseLiveData(liveData: ResponseLiveData<T>? = null): ResponseLiveData<T> {
    return (liveData ?: ResponseLiveData<T>()).apply {
        subscribe(object : AbsRxObserver<GankBaseBean<T>>() {
            override fun onStart() {
                super.onStart()
                value = ResponseData.loading()
            }

            override fun onFinish() {
                super.onFinish()
                value = ResponseData.finish()
            }

            override fun onSuccess(data: GankBaseBean<T>?) {
                data?.let { it ->
                    value = if (it.isSuccess) {
                        ResponseData.success(it.data)
                    } else {
                        ResponseData.error(
                            ErrorType.API,
                            it.status,
                            "",
                            ApiException(it.status, "")
                        )
                    }
                }
            }

            override fun onFailure(bean: ErrorBean) {
                value = ResponseData.error(bean)
            }
        })
    }
}