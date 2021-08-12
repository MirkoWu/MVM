package com.mirkowu.mvm.network;

import com.mirkowu.lib_network.AbsRetrofitClient;

public class FileClient extends AbsRetrofitClient {
    @Override
    public String getBaseUrl() {
        return "http://192.168.182.1:8080/";
    }

    private static class Singleton {
        private static final FileClient INSTANCE = new FileClient();
    }

    public static FileClient getInstance() {
        return Singleton.INSTANCE;
    }

    private FileClient() {
    }

    private static <T> T getAPIService(Class<T> service) {
        return getInstance().getService(service);
    }

    public static FileApi getUploadFileApi() {
        return getAPIService(FileApi.class);
    }

}
