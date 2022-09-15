package com.mirkowu.lib_network;

import com.mirkowu.lib_util.LogUtil;
import com.mirkowu.lib_util.utilcode.util.StringUtils;

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
    public void onNext(@NonNull T o) {
        doOnSuccess(o);
    }

    /**
     * 处理成功
     *
     * @param o
     */
    public void doOnSuccess(T o) {
        try {
            onFinish();
            onSuccess(o);
        } catch (Throwable t) {
            LogUtil.e("onSuccess 业务异常", t);
            onFailure(new ErrorBean(ErrorType.API, ErrorCode.ERROR_BIZ, t.getMessage(), t));
        }
    }

    @Override
    public void onError(@NonNull Throwable e) {
        doOnError(e);
    }

    /**
     * 处理失败
     *
     * @param e
     */
    public void doOnError(Throwable e) {
        onFinish();
        if (e instanceof RxJava2NullException) {
            doOnSuccess(null);
        } else {
            LogUtil.e("onFailure 请求异常", e.toString());
            onFailure(handleError(e));
        }
    }

    protected ErrorBean handleError(Throwable e) {
        if (e instanceof ApiException) { //Api异常
            ApiException apiException = (ApiException) e;
            return new ErrorBean(ErrorType.API, apiException.code(), apiException.msg(), e);
        } else if (e instanceof HttpException) { //Http错误
            HttpException httpException = (HttpException) e;
            return new ErrorBean(ErrorType.NET, httpException.code(), httpException.getMessage(), e);
        } else if (e instanceof ConnectException) {
            return new ErrorBean(ErrorType.NET, ErrorCode.NET_CONNECT, StringUtils.getString(R.string.network_connect_failed_please_check), e);
        } else if (e instanceof UnknownHostException) {
            return new ErrorBean(ErrorType.NET, ErrorCode.NET_UNKNOWN_HOST, StringUtils.getString(R.string.network_connect_failed_please_check), e);
        } else if (e instanceof SocketTimeoutException) {
            return new ErrorBean(ErrorType.NET, ErrorCode.NET_TIMEOUT, StringUtils.getString(R.string.network_request_timeout_please_retry), e);
        } else {
            return new ErrorBean(ErrorType.UNKNOWN, ErrorCode.UNKNOWN, StringUtils.getString(R.string.network_request_failed_) + e.getMessage(), e);
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
     */
    public abstract void onFailure(@NonNull ErrorBean error);


}
