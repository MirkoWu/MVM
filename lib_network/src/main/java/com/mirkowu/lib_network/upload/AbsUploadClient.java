package com.mirkowu.lib_network.upload;

import androidx.annotation.NonNull;

import com.mirkowu.lib_network.AbsRetrofitClient;
import com.mirkowu.lib_network.download.OnProgressListener;

import java.io.File;

import okhttp3.OkHttpClient;

public abstract class AbsUploadClient extends AbsRetrofitClient {

    private final OnProgressListener mDeleteProgressListener = new OnProgressListener() {
        @Override
        public void onProgress(long readBytes, long totalBytes) {
            if (mProgressListener != null) {
                mProgressListener.onProgress(readBytes, totalBytes);
            }
        }

        @Override
        public void onSuccess(@NonNull File file) {
            if (mProgressListener != null) {
                mProgressListener.onSuccess(file);
            }
        }

        @Override
        public void onFailure(Throwable e) {
            if (mProgressListener != null) {
                mProgressListener.onFailure(e);
            }
        }
    };

    private OnProgressListener mProgressListener;

    public OnProgressListener getOnProgressListener() {
        return mProgressListener;
    }

    public AbsUploadClient setOnProgressListener(OnProgressListener progressListener) {
        this.mProgressListener = progressListener;
        return this;
    }


    @Override
    public OkHttpClient getOkHttpClient() {
        addInterceptor(new UploadInterceptor(mDeleteProgressListener));
        return super.getOkHttpClient();
    }
}