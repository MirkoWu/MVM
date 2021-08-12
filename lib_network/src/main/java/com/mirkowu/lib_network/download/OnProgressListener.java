package com.mirkowu.lib_network.download;

import androidx.annotation.NonNull;

import java.io.File;

public interface OnProgressListener {
    void onProgress(long readBytes, long totalBytes);

    void onSuccess(@NonNull File file);

    void onFailure(Throwable e);
}
