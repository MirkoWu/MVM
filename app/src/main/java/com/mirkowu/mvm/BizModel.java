package com.mirkowu.mvm;


import com.mirkowu.lib_base.model.BaseModel;
import com.mirkowu.lib_base.util.RxScheduler;
import com.mirkowu.mvm.network.GankApi;
import com.mirkowu.mvm.network.RetrofitClient;

import java.util.Calendar;
import java.util.concurrent.TimeUnit;

import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.ObservableEmitter;
import io.reactivex.rxjava3.core.ObservableOnSubscribe;

public class BizModel extends BaseModel {

    public Observable<Object> loadImage() {
        return RetrofitClient.getAPIService(GankApi.class)
                .loadImage(10, 1)
                .compose(RxScheduler.ioToMain());
    }

    public Observable<Object> loadData() {
        return Observable.create(new ObservableOnSubscribe<Object>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<Object> emitter) throws Throwable {
                Thread.sleep(1000L);
                emitter.onNext("当前时间：" + Calendar.getInstance().getTime());
                emitter.onComplete();
            }
        }).compose(RxScheduler.ioToMain());
    }

    public Observable<Object> startTimer() {
        return Observable.interval(0, 1, TimeUnit.SECONDS)
                .compose(RxScheduler.ioToMain());
    }
}
