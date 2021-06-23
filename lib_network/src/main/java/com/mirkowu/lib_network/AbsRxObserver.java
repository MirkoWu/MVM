package com.mirkowu.lib_network;

import com.mirkowu.lib_util.LogUtil;

import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

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
            onFinish();
            onSuccess(o);
        } catch (Throwable t) {
            onFailure(ErrorType.API, ErrorCode.ERROR_BIZ, t.getMessage());
        }
    }

    @Override
    public void onError(@NonNull Throwable e) {
        onFinish();
        if (!(e instanceof RxJava2NullException)) {
            LogUtil.e(e, "AbsRxObserver 网络请求Error");
        }
        if (e instanceof RxJava2NullException) {
            doOnSuccess(null);
        } else if (e instanceof ApiException) {
            //api异常
            ApiException apiException = (ApiException) e;
            onFailure(ErrorType.API, apiException.code(), apiException.msg());
        } else if (e instanceof HttpException) {
            //网络错误
            HttpException httpException = (HttpException) e;
            onFailure(ErrorType.NET, httpException.code(), httpException.message());
        } else if (e instanceof ConnectException) {
            onFailure(ErrorType.NET, ErrorCode.NET_CONNECT, "网络连接失败，请检查网络！");
        } else if (e instanceof SocketTimeoutException) {
            onFailure(ErrorType.NET, ErrorCode.NET_TIMEOUT, "请求超时，请稍候重试！");
        } else if (e instanceof UnknownHostException) {
            onFailure(ErrorType.NET, ErrorCode.NET_UNKNOWNHOST, "请求失败，无法连接到服务器！");
        } else {
            onFailure(ErrorType.UNKONW, ErrorCode.UNKNOW, "请求失败，" + e.getMessage());
        }
    }

    @Override
    public void onComplete() {
    }


    public void onFinish() {
        //只可能来自doOnSuccess/onError二者之一
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
    public abstract void onFailure(@androidx.annotation.NonNull ErrorType type, int code, String msg);
}
