package com.mirkowu.lib_network;

import com.mirkowu.lib_network.request.ErrorData;
import com.mirkowu.lib_network.request.ErrorCode;
import com.mirkowu.lib_network.request.ErrorType;
import com.mirkowu.lib_util.LogUtil;

import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.observers.DisposableObserver;

public abstract class AbsRxObserver<T> extends DisposableObserver<T> {
    @Override
    protected void onStart() {
    }

    @Override
    public void onNext(@NonNull T o) {
        onFinish();
        doOnSuccess(o);
    }

    /**
     * 处理成功
     *
     * @param o
     */
    protected void doOnSuccess(T o) {
        try {
            onSuccess(o);
        } catch (Throwable e) {
            onFailure(new ErrorData(ErrorType.API, ErrorCode.ERROR_BIZ, e.getMessage(), e));
            LogUtil.e("onSuccess 业务异常", e.toString());
            if (LogUtil.isDebug()) {
                throw e;
            }
        }
    }

    @Override
    public void onError(@NonNull Throwable e) {
        onFinish();
        doOnError(e);
    }

    /**
     * 处理失败
     *
     * @param e
     */
    protected void doOnError(Throwable e) {
        if (e instanceof RxJava2NullException) {
            doOnSuccess(null);
        } else {
            LogUtil.e("onFailure 请求异常", e.toString());
            onFailure(ErrorData.create(e));
        }
    }

    @Override
    public void onComplete() {
    }


    public void onFinish() {
        //只可能来自onNext/onError二者之一
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
    public abstract void onFailure(@NonNull ErrorData error);


}
