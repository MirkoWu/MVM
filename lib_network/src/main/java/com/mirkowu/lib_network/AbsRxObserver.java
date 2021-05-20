package com.mirkowu.lib_network;

import com.mirkowu.lib_util.LogUtil;

import java.net.ConnectException;
import java.net.SocketTimeoutException;

import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.observers.DisposableObserver;
import retrofit2.HttpException;

public abstract class AbsRxObserver<T> extends DisposableObserver<T> {
    @Override
    protected void onStart() {
    }

    @Override
    public void onNext(T o) {
        doOnSuccess(o);
    }

    private void doOnSuccess(T o) {
        try {
            onSuccess(o);
        } catch (Throwable t) {
            onFailure(ErrorType.ERROR_BUSINESS, 0, t.getMessage());
        }
    }

    @Override
    public void onError(@NonNull Throwable e) {
        onFinish();
        if (!(e instanceof RxJava2NullException)) {
            LogUtil.e(e, "网络请求Error");
        }
        if (e instanceof RxJava2NullException) {
            doOnSuccess(null);
        } else if (e instanceof ConnectException) {
            onFailure(ErrorType.ERROR_NET_CONNECT, 0, "网络连接失败，请检查网络！");
        } else if (e instanceof SocketTimeoutException) {
            onFailure(ErrorType.ERROR_NET_TIMEOUT, 0, "请求超时，请稍候重试！");
        } else if (e instanceof HttpException) {
            HttpException httpException = (HttpException) e;
            onFailure(ErrorType.ERROR_HTTP, httpException.code(), httpException.message());
        } else if (e instanceof ApiException) {
            //api 异常
            ApiException apiException = (ApiException) e;
            onFailure(ErrorType.ERROR_API, apiException.code(), apiException.msg());
        } else {
            onFailure(ErrorType.ERROR_UNKNOW, 0, "请求失败，" + e.getMessage());
        }
    }

    @Override
    public void onComplete() {
        onFinish();
    }


    public void onFinish() {
        //只可能来自onComplete/onError二者之一
    }

    /**
     * 数据请求成功
     *
     * @param data
     */
    public abstract void onSuccess(T data);

    /**
     * 请求失败
     *
     * @param code
     * @param msg
     */
    public abstract void onFailure(int errorType, int code, String msg);
}
