package com.mirkowu.lib_network.download;

import okhttp3.Request;

public class RequestConfig {
    private Request request;
    private OnProgressListener downloadListener;

    public Request getRequest() {
        return request;
    }

    public RequestConfig setRequest(Request request) {
        this.request = request;
        return this;
    }


    public OnProgressListener getDownloadListener() {
        return downloadListener;
    }

    public RequestConfig setDownloadListener(OnProgressListener downloadListener) {
        this.downloadListener = downloadListener;
        return this;
    }

    public RequestConfig() {
    }

    public RequestConfig(Request request, OnProgressListener downloadListener) {
        this.request = request;
        this.downloadListener = downloadListener;
    }
}
