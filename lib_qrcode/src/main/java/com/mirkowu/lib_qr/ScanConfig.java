package com.mirkowu.lib_qr;

public class ScanConfig {
    private String title; //标题
    private boolean isShowFlashlight = true; //是否显示闪光灯
    private boolean isShowAlbumPick = true; //是否显示相册选取图片

    private boolean isVibrate = true; //是否震动
    private boolean isPlayBeep = false; //是否播放音效
    private boolean isTouchZoom = true; //是否支持触摸缩放
    private boolean isCameraAutoZoom = false; //是否支持相机自动缩放识别二维码

    public String getTitle() {
        return title;
    }

    public ScanConfig setTitle(String title) {
        this.title = title;
        return this;
    }

    public boolean isShowFlashlight() {
        return isShowFlashlight;
    }

    public ScanConfig setShowFlashlight(boolean showFlashlight) {
        isShowFlashlight = showFlashlight;
        return this;
    }

    public boolean isShowAlbumPick() {
        return isShowAlbumPick;
    }

    public ScanConfig setShowAlbumPick(boolean showAlbumPick) {
        isShowAlbumPick = showAlbumPick;
        return this;
    }

    public boolean isVibrate() {
        return isVibrate;
    }

    public ScanConfig setVibrate(boolean vibrate) {
        isVibrate = vibrate;
        return this;
    }

    public boolean isPlayBeep() {
        return isPlayBeep;
    }

    public ScanConfig setPlayBeep(boolean playBeep) {
        isPlayBeep = playBeep;
        return this;
    }

    public boolean isTouchZoom() {
        return isTouchZoom;
    }

    public ScanConfig setTouchZoom(boolean touchZoom) {
        isTouchZoom = touchZoom;
        return this;
    }

    public boolean isCameraAutoZoom() {
        return isCameraAutoZoom;
    }

    public ScanConfig setCameraAutoZoom(boolean cameraAutoZoom) {
        isCameraAutoZoom = cameraAutoZoom;
        return this;
    }
}
