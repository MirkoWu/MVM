package com.mirkowu.mvm.network;

import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.observers.DisposableObserver;

public abstract class RxObserver<T> extends DisposableObserver<T> {
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
            onFailure(t);
        }
    }

    @Override
    public void onError(@NonNull Throwable e) {
        onFinish();
        onFailure(e);
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
     * @param e
     */
    public abstract void onFailure(Throwable e);
}
