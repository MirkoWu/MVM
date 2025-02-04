package com.mirkowu.lib_base.util

import com.mirkowu.lib_base.view.IBaseView
import com.mirkowu.lib_network.request.flow.event
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow

fun <T> Flow<T>.autoLoading(view: IBaseView): Flow<T> {
    return event(Dispatchers.Main) {
        loading { view.showLoadingDialog() }
        finish { view.hideLoadingDialog() }
    }
}