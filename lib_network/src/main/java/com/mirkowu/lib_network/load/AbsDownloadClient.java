package com.mirkowu.lib_network.load;

import com.mirkowu.lib_network.AbsRetrofitClient;

import okhttp3.OkHttpClient;

public abstract class AbsDownloadClient extends AbsRetrofitClient {

    private final OnProgressListener mDeleteProgressListener = new OnProgressListener() {
        @Override
        public void onProgress(long readBytes, long totalBytes) {
            if (mProgressListener != null) {
                mProgressListener.onProgress(readBytes, totalBytes);
            }
        }
    };

    private OnProgressListener mProgressListener;

    public OnProgressListener getOnProgressListener() {
        return mProgressListener;
    }

    public AbsDownloadClient setOnProgressListener(OnProgressListener progressListener) {
        this.mProgressListener = progressListener;
        return this;
    }


    @Override
    public OkHttpClient getOkHttpClient() {
        addInterceptor(new DownloadInterceptor(mDeleteProgressListener));
        return super.getOkHttpClient();
    }
}