package com.mirkowu.lib_network.upload;

import com.mirkowu.lib_network.download.OnProgressListener;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class UploadInterceptor implements Interceptor {
    public OnProgressListener mProgressListener;

    public UploadInterceptor(OnProgressListener progressListener) {
        this.mProgressListener = progressListener;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request oldRequest = chain.request();
        RequestBody requestBody = oldRequest.body();
        ProgressRequestBody newRequestBody = new ProgressRequestBody(requestBody, mProgressListener);
        Request newRequest = oldRequest.newBuilder().method(oldRequest.method(), newRequestBody).build();
        Response response = chain.proceed(newRequest);
        return response;
    }
}
