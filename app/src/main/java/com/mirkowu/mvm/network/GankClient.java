package com.mirkowu.mvm.network;

import com.mirkowu.lib_network.AbsRetrofitClient;

public class GankClient extends AbsRetrofitClient {

    @Override
    public String getBaseUrl() {
        return HostUrl.HOST;
    }

    private static class Singleton {
        private static final GankClient INSTANCE = new GankClient();
    }

    public static GankClient getInstance() {
        return Singleton.INSTANCE;
    }

    private GankClient() {
        setDebug(HostUrl.isDebug);
    }


    private static <T> T getAPIService(Class<T> service) {
        return getInstance().getService(service);
    }

    public static GankApi getGankApi() {
        return getAPIService(GankApi.class);
    }

}
