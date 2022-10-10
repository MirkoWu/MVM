package com.mirkowu.mvm.network;


import com.mirkowu.mvm.bean.GankBaseBean;
import com.mirkowu.mvm.bean.ImageBean;
import com.mirkowu.mvm.bean.ImageListBean;

import java.util.List;

import io.reactivex.rxjava3.core.Observable;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface GankApi {
    @GET("api/getImages")
    Observable<GankBaseBean<ImageListBean>> loadImage(@Query("type") String type,
                                                                 @Query("page") int page,
                                                                 @Query("pageSize") int pageSize);
}
