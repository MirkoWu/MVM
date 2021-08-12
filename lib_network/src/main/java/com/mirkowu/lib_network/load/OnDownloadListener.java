package com.mirkowu.lib_network.load;

import androidx.annotation.NonNull;

import java.io.File;

public interface OnDownloadListener extends OnProgressListener {

    void onSuccess(@NonNull File file);

    void onFailure(Throwable e);
}
