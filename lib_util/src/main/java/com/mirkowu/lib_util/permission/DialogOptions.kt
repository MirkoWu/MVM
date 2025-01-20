package com.mirkowu.lib_util.permission

import androidx.annotation.Keep
import com.mirkowu.lib_util.R
import com.mirkowu.lib_util.utilcode.util.StringUtils

@Keep
data class DialogOptions @JvmOverloads constructor(
    var title: String,
    var content: String,
    var negativeButton: String = StringUtils.getString(R.string.util_permission_dialog_cancel),
    //这个按钮可以固定
//    var positiveButton: String = StringUtils.getString(R.string.util_permission_dialog_ok),
)