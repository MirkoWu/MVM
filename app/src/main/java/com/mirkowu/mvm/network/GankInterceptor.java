package com.mirkowu.mvm.network;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Response;

public class GankInterceptor implements Interceptor {
    @Override
    public Response intercept(Chain chain) throws IOException {

        return chain.proceed(chain.request());
    }
}
