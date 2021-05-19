package com.mirkowu.mvm.network;


import io.reactivex.rxjava3.core.Observable;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface MainService {
    @GET("api/data/福利/{pageSize}/{page})")
    Observable<Object> loadImage(@Path("pageSize") int pageSize, @Path("page") int page);
}
