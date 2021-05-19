package com.mirkowu.mvm.network;

import com.mirkowu.lib_network.AbsRetrofitClient;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Interceptor;

public class RetrofitClient extends AbsRetrofitClient {

    private static class Singleton {
        private static final RetrofitClient INSTANCE = new RetrofitClient();
    }

    private static RetrofitClient getInstance() {
        return Singleton.INSTANCE;
    }

    private RetrofitClient() {
    }

    @Override
    protected boolean isDebug() {
        return HostUrl.isDebug;
    }

    @Override
    protected String getHost() {
        return HostUrl.HOST;
    }

    @Override
    protected List<Interceptor> getInterceptor() {
        List<Interceptor> list = new ArrayList<>();
        return list;
    }

    public static <T> T getAPIService(Class<T> service) {
        return getInstance().getRetrofit().create(service);
    }


}
