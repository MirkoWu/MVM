package com.mirkowu.lib_network.request.flow

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.liveData
import com.mirkowu.lib_network.request.ErrorData
import com.mirkowu.lib_network.request.RequestCallback
import com.mirkowu.lib_network.request.RequestData
import com.mirkowu.lib_network.request.RequestLiveData
import com.mirkowu.lib_network.request.invoke
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlin.coroutines.CoroutineContext

/**
 * Request2 返回结果为可空，需要自行判断处理
 */
/**
 * 返回结果为非空，空数据可在empty{} 中获取事件
 * 但这个用法可能不好理解，所以新加方法 request2 ,让使用者自行判空
 */
internal const val DEFAULT_TIMEOUT = 5000L

/**
 * 适用于Flow,绑定LifecycleOwner,防止内存泄漏
 * 一步到位，不需要单独写在协程内部
 *
 */
fun <T> Flow<T?>.requestObserve(
    owner: LifecycleOwner,
    context: CoroutineContext = Dispatchers.IO,
    callbackFun: RequestCallback<T?>.() -> Unit
) {
    asRequestLiveData(null, context)
        .observe(owner) {
            it.invoke(callbackFun)
        }
}

/**
 * 适用于Flow,ObserveForever,记得removeObserver
 * 一步到位，不需要单独写在协程内部
 */
fun <T> Flow<T?>.requestObserveForever(
    context: CoroutineContext = Dispatchers.IO,
    callbackFun: RequestCallback<T?>.() -> Unit
) {
    asRequestLiveData(null, context)
        .observeForever {
            it.invoke(callbackFun)
        }
}

/**
 * 适用于LiveData ,绑定LifecycleOwner
 */
fun <T> RequestLiveData<T?>.requestObserve(
    owner: LifecycleOwner,
    callbackFun: RequestCallback<T?>.() -> Unit
) {
    observe(owner) { it.invoke(callbackFun) }
}

/**
 * 适用于LiveData ,ObserveForever,记得removeObserver
 */
fun <T> RequestLiveData<T?>.requestObserveForever(
    callbackFun: RequestCallback<T?>.() -> Unit
) {
    observeForever { it.invoke(callbackFun) }
}


/**
 * 转换为LiveData
 * @param owner 任务启动对应协程，需要绑定页面生命周期时传入LifecycleOwner 可防止内存泄漏
 * @param context 耗时任务所在协程
 */
fun <T> Flow<T>.asRequestLiveData(
    owner: LifecycleOwner? = null,
    context: CoroutineContext = Dispatchers.IO
): RequestLiveData<T> {
    return liveData(owner?.lifecycleScope?.coroutineContext ?: context, DEFAULT_TIMEOUT) {
        onStart {
            emit(RequestData.loading<T>())
        }
            .catch { e: Throwable ->
                emit(RequestData.failure<T>(ErrorData.create(e)))
            }
//            .onCompletion {
//                emit(RequestData.finish<T>())
//            }
            .flowOn(context)
            .collect { emit(RequestData.success(it)) }
    } as RequestLiveData<T>
}

/**
 *通用的发起Flow请求，获取回调结果
 */
suspend fun <T> Flow<T>.request(
    context: CoroutineContext = Dispatchers.IO,
    callbackFun: (RequestCallback<T>.() -> Unit) = {}
) {
    request2NoCollect(context)
        .collect {
            it.invoke(callbackFun)
        }
}

internal fun <T> Flow<T>.request2NoCollect(context: CoroutineContext = Dispatchers.IO): Flow<RequestData<T>> {
    return map { RequestData.success(it) }
        .onStart {
            emit(RequestData.loading<T>())
        }
        .catch { e: Throwable ->
            emit(RequestData.failure<T>(ErrorData.create(e)))
        }
//        .onCompletion { //catch之后再onCompletion，二者都会调用
//            emit(RequestData.finish<T>())
//        }
        .flowOn(context)
}


