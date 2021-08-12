package com.mirkowu.lib_network.load;

import okhttp3.Request;

public class RequestConfig {
    private Request request;
    private OnDownloadListener downloadListener;

    public Request getRequest() {
        return request;
    }

    public RequestConfig setRequest(Request request) {
        this.request = request;
        return this;
    }


    public OnDownloadListener getDownloadListener() {
        return downloadListener;
    }

    public RequestConfig setDownloadListener(OnDownloadListener downloadListener) {
        this.downloadListener = downloadListener;
        return this;
    }

    public RequestConfig() {
    }

    public RequestConfig(Request request, OnDownloadListener downloadListener) {
        this.request = request;
        this.downloadListener = downloadListener;
    }
}
