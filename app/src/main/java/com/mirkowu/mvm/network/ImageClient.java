package com.mirkowu.mvm.network;

import com.mirkowu.lib_network.AbsRetrofitClient;

public class ImageClient extends AbsRetrofitClient {
    private final String HOST = "https://acg.toubiec.cn/";

    private static class Singleton {
        private static final ImageClient INSTANCE = new ImageClient();
    }

    public static ImageClient getInstance() {
        return Singleton.INSTANCE;
    }

    private ImageClient() {
        setDebug(HostUrl.isDebug);
    }

    @Override
    public String getBaseUrl() {
        return HOST;
    }

    public static <T> T getAPIService(Class<T> service) {
        return getInstance().getService(service);
    }

    public static RandomImageApi getImageApi() {
        return getAPIService(RandomImageApi.class);
    }
}
