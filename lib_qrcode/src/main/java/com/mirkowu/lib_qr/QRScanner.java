package com.mirkowu.lib_qr;

import android.content.Context;

public class QRScanner {

    private ScanConfig mScanConfig;
    private OnScanResultListener mOnScanResultListener;
    private static volatile QRScanner sScanner;

    public static QRScanner getInstance() {
        if (sScanner == null) {
            synchronized (QRScanner.class) {
                if (sScanner == null) {
                    sScanner = new QRScanner();
                }
            }
        }
        return sScanner;
    }

    private QRScanner() {
    }

    public QRScanner setScanConfig(ScanConfig config) {
        this.mScanConfig = config;
        return this;
    }

    /**
     * 设置默认的配置
     */
    public ScanConfig getScanConfig() {
        if (mScanConfig == null) {
            mScanConfig = new ScanConfig();
        }
        return mScanConfig;
    }

    public QRScanner setOnScanResultListener(OnScanResultListener resultListener) {
        this.mOnScanResultListener = resultListener;
        return this;
    }

    public OnScanResultListener getOnScanResultListener() {
        return mOnScanResultListener;
    }


    public void removeOnScanResultListener() {
        mOnScanResultListener = null;
    }

    public void start(Context context) {
        QrScanActivity.start(context);
    }

}
