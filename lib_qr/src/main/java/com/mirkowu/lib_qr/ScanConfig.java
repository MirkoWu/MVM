package com.mirkowu.lib_qr;

public class ScanConfig {
    private String title;
    private boolean isShowFlashlight;
    private boolean isShowAlbumPick;

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
}
