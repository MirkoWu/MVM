package com.mirkowu.lib_network.load;

public interface OnProgressListener {
    void onProgress(long readBytes, long totalBytes);
}
