package com.mirkowu.lib_qr;

public interface OnScanResultListener {
    void onSuccess(String data);

    default void onFailure() {

    }
}
