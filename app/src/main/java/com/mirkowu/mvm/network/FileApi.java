package com.mirkowu.mvm.network;


import io.reactivex.rxjava3.core.Observable;
import okhttp3.MultipartBody;
import retrofit2.http.Body;
import retrofit2.http.Multipart;
import retrofit2.http.POST;

public interface FileApi {

    @POST("upload")
    Observable<Object> uploadFile(@Body MultipartBody body);
}
