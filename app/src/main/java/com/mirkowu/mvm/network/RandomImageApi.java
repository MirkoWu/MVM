package com.mirkowu.mvm.network;

import com.mirkowu.mvm.bean.RandomImageBean;

import java.util.List;

import io.reactivex.rxjava3.core.Observable;
import retrofit2.http.GET;

public interface RandomImageApi {
    @GET("random.php?ret=json")
    Observable<List<RandomImageBean>> getRandomImage();
}
