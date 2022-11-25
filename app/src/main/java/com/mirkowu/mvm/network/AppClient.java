package com.mirkowu.mvm.network;

import com.mirkowu.lib_network.AbsRetrofitClient;

public class AppClient extends AbsRetrofitClient {

    private static class Singleton {
        private static final AppClient INSTANCE = new AppClient();
    }

    public static AppClient getInstance() {
        return AppClient.Singleton.INSTANCE;
    }

    private AppClient() {
        setDebug(true);
    }



    @Override
    public String getBaseUrl() {
        return "http://rlw8qmxdu.hd-bkt.clouddn.com";
    }

}
