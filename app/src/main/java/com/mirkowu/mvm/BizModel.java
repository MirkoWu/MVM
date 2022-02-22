package com.mirkowu.mvm;


import com.mirkowu.lib_base.model.BaseModel;
import com.mirkowu.lib_base.util.RxScheduler;
import com.mirkowu.mvm.bean.GankBaseBean;
import com.mirkowu.mvm.bean.GankImageBean;
import com.mirkowu.mvm.bean.RandomImageBean;
import com.mirkowu.mvm.network.GankClient;
import com.mirkowu.mvm.network.ImageClient;

import java.util.Calendar;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.ObservableEmitter;
import io.reactivex.rxjava3.core.ObservableOnSubscribe;

public class BizModel extends BaseModel {

    public Observable<GankBaseBean<List<GankImageBean>>> loadImage(int page, int pageSize) {
        return GankClient.getGankApi()
                .loadImage(page, pageSize)
                .compose(RxScheduler.ioToMain());
    }

    public Observable<List<RandomImageBean>> loadImage2() {
        return ImageClient.getImageApi()
                .getRandomImage()
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
