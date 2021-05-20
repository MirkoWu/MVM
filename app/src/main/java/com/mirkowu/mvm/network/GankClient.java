package com.mirkowu.mvm.network;

import com.mirkowu.lib_network.AbsRetrofitClient;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Interceptor;

public class GankClient extends AbsRetrofitClient {

    private static class Singleton {
        private static final GankClient INSTANCE = new GankClient();
    }

    public static GankClient getInstance() {
        return Singleton.INSTANCE;
    }

    private GankClient() {
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
    protected boolean isUseSSLVerifier() {
        return super.isUseSSLVerifier();
    }

    @Override
    protected List<Interceptor> getInterceptor() {
        List<Interceptor> list = new ArrayList<>();
        return list;
    }


    private static <T> T getAPIService(Class<T> service) {
        return getInstance().getService(service);
    }

    public static GankApi getGankApi() {
        return getAPIService(GankApi.class);
    }

}
