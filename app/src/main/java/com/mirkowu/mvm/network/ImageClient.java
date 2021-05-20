package com.mirkowu.mvm.network;

import com.mirkowu.lib_network.AbsRetrofitClient;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Interceptor;

public class ImageClient extends AbsRetrofitClient {
    private final String HOST = "https://acg.toubiec.cn/";

    private static class Singleton {
        private static final ImageClient INSTANCE = new ImageClient();
    }

    public static ImageClient getInstance() {
        return Singleton.INSTANCE;
    }

    private ImageClient() {
    }

    @Override
    protected boolean isDebug() {
        return HostUrl.isDebug;
    }

    @Override
    protected String getHost() {
        return HOST;
    }

    @Override
    protected List<Interceptor> getInterceptor() {
        List<Interceptor> list = new ArrayList<>();
        return list;
    }

    public static <T> T getAPIService(Class<T> service) {
        return getInstance().getService(service);
    }

    public static RandomImageApi getImageApi() {
        return getAPIService(RandomImageApi.class);
    }
}
