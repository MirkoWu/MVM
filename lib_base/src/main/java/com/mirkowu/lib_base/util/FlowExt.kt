package com.mirkowu.lib_base.util

import com.mirkowu.lib_base.view.IBaseView
import com.mirkowu.lib_network.request.flow.event
import com.mirkowu.lib_util.utilcode.util.ToastUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow

fun <T> Flow<T>.autoLoading(view: IBaseView, toastOnFail: Boolean = false): Flow<T> {
    return event(Dispatchers.Main) {
        loading { view.showLoadingDialog() }
        fail { if (toastOnFail) ToastUtils.showShort(it.displayMsg) }
        finish { view.hideLoadingDialog() }
    }
}