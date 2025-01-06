package com.mirkowu.lib_network.request

import androidx.annotation.Keep
import com.mirkowu.lib_network.R
import com.mirkowu.lib_util.utilcode.util.StringUtils
import retrofit2.HttpException
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

@Keep
data class ErrorData @JvmOverloads constructor(
    val type: ErrorType,
    val code: String,
    val msg: String?,
    var throwable: Throwable? = null,
) {

    val displayMsg: String?
        get() {
          return  when (throwable) {
                is UnknownHostException -> StringUtils.getString(R.string.network_connect_failed_please_check)
                is ConnectException -> StringUtils.getString(R.string.network_connect_failed_please_check)
                is SocketTimeoutException -> StringUtils.getString(R.string.network_request_timeout_please_retry)
                //Http错误
                is HttpException -> (throwable as HttpException).message
                //Api异常
                is ApiException -> (throwable as ApiException).let { it.displayMsg ?: it.msg }
                else -> StringUtils.getString(R.string.network_request_failed_) + throwable?.message
            }
        }

    val isNetError: Boolean
        get() = type == ErrorType.NET
    val isEmptyData: Boolean
        get() = type == ErrorType.API && code == ErrorCode.EMPTY_DATA
    val isApiError: Boolean
        get() = type == ErrorType.API
    val isUnKnowError: Boolean
        get() = type == ErrorType.UNKNOWN

    override fun toString(): String {
        return "ErrorBean{" +
                "type=" + type +
                ", code=" + code +
                ", msg='" + msg + '\'' +
                ", throwable=" + throwable +
                '}'
    }

    companion object {
        @JvmStatic
        fun create(e: Throwable): ErrorData {
            return when (e) {
                is UnknownHostException ->
                    ErrorData(ErrorType.NET, ErrorCode.NET_UNKNOWN_HOST, e.message, e)

                is ConnectException ->
                    ErrorData(ErrorType.NET, ErrorCode.NET_CONNECT, e.message, e)

                is SocketTimeoutException ->
                    ErrorData(ErrorType.NET, ErrorCode.NET_TIMEOUT, e.message, e)

                is HttpException ->
                    ErrorData(ErrorType.API, "${e.code()}", e.message, e)

                is ApiException ->
                    ErrorData(ErrorType.API, "${e.code}", e.displayMsg ?: e.msg, e)

                else ->
                    ErrorData(ErrorType.UNKNOWN, ErrorCode.UNKNOWN, e.message, e)
            }
        }
    }
}

enum class ErrorType {
    NET,  //网络错误
    API,  //API异常
    UNKNOWN //未知异常
}

interface ErrorCode {
    companion object {
        //网络连接失败
        const val NET_CONNECT = "1001"

        //请求超时
        const val NET_TIMEOUT = "1002"

        //未知HOST
        const val NET_UNKNOWN_HOST = "1003"

        //空数据
        const val EMPTY_DATA = "2001"

        const val ERROR_BIZ = "3000"

        //未知
        const val UNKNOWN = "9999"
    }
}


