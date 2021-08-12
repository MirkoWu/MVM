package com.mirkowu.lib_network.load;

import androidx.annotation.NonNull;

import java.io.File;

public interface OnProgressListener {
    void onProgress(long readBytes, long totalBytes);
}
