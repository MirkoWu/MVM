package com.mirkowu.mvm.network;

import io.reactivex.rxjava3.core.Observable;
import retrofit2.http.GET;

public interface RandomImageApi {
    @GET("random?return=json")
    Observable<Object> getRandomImage();
}
