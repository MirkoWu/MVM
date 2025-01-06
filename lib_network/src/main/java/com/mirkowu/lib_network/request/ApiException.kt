package com.mirkowu.lib_network.request

/**
 * api业务内部的异常
 */
open class ApiException @JvmOverloads constructor(
    val code: String,
    val msg: String?,
    val displayMsg: String? = null,
    val throwable: Throwable? = null,
) : RuntimeException(msg)