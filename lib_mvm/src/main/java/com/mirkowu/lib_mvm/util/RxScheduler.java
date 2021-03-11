package com.mirkowu.lib_mvm.util;


import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.ObservableSource;
import io.reactivex.rxjava3.core.ObservableTransformer;
import io.reactivex.rxjava3.schedulers.Schedulers;

/**
 * RxJava 线程调度
 */
public class RxScheduler {

    /**
     * io线程 切换到 Android主线程
     */
    public static <T> ObservableTransformer<T, T> ioToMain() {
        return new ObservableTransformer<T, T>() {
            @Override
            public ObservableSource<T> apply(Observable<T> upstream) {
                return upstream.subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread());
            }
        };
    }

}
