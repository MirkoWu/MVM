package com.mirkowu.lib_network.download;

import androidx.annotation.NonNull;

import com.mirkowu.lib_network.AbsOkHttpClient;

import okhttp3.Call;
import okhttp3.OkHttpClient;

public class DownloadClient extends AbsOkHttpClient {
    private static class Singleton {
        private static final DownloadClient INSTANCE = new DownloadClient();
    }

    public static DownloadClient getInstance() {
        return Singleton.INSTANCE;
    }

    private DownloadClient() {
    }

    public Call createCall(@NonNull RequestConfig config) {
        return getOkHttpClient(config.getDownloadListener()).newCall(config.getRequest());
    }

    public OkHttpClient getOkHttpClient(OnProgressListener downloadListener) {
        //带进度的拦截器
        if (downloadListener != null) {
            addInterceptor(new DownloadInterceptor(downloadListener));
        }
        return getOkHttpClient();
    }

}
