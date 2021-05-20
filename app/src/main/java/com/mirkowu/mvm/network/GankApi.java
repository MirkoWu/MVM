package com.mirkowu.mvm.network;


import com.mirkowu.mvm.bean.GankBaseBean;
import com.mirkowu.mvm.bean.GankImageBean;

import java.util.List;

import io.reactivex.rxjava3.core.Observable;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface GankApi {
    @GET("api/v2/data/category/Girl/type/Girl/page/{page}/count/{pageSize}")
    Observable<GankBaseBean<List<GankImageBean>>> loadImage(@Path("page") int page, @Path("pageSize") int pageSize);
}
