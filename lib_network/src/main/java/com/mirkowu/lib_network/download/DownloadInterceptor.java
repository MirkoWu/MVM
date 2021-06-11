package com.mirkowu.lib_network.download;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Response;

public class DownloadInterceptor implements Interceptor {
    public OnDownloadListener progressListener;

    public DownloadInterceptor(OnDownloadListener progressListener) {
        this.progressListener = progressListener;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Response response = chain.proceed(chain.request());
        return response.newBuilder().body(new ProgressResponseBody(response.body(), progressListener)).build();
    }
}
