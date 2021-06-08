package com.mirkowu.lib_photo;

import com.mirkowu.lib_photo.bean.MediaBean;

import java.util.ArrayList;

public class PickerConfig {
    private boolean isOnlyVideo = false; //是否显示GIF格式图片 默认不显示
    private boolean isShowGif = true; //是否显示GIF格式图片 默认显示
    private boolean isShowVideo = false; //是否显示视频 默认不显示
    private boolean isShowCamera = false; //是否显示拍照按钮 默认不显示
    private ArrayList<MediaBean> originSelectList; //原始选择的图片列表
    private int maxPickCount = PickerConstant.DEFAULT_IMAGE_SIZE; //最大可选图片数量 默认9张
    private int spanCount = PickerConstant.DEFAULT_SPAN_COUNT; //图片展示列表列数 默认3列
    private String[] typeArray; //媒体格式数组

    public boolean isOnlyVideo() {
        return isOnlyVideo;
    }

    public PickerConfig setOnlyVideo(boolean onlyVideo) {
        isOnlyVideo = onlyVideo;
        return this;
    }

    public boolean isShowCamera() {
        return isShowCamera;
    }

    public PickerConfig setShowCamera(boolean showCamera) {
        isShowCamera = showCamera;
        return this;
    }

    public boolean isShowGif() {
        return isShowGif;
    }

    public PickerConfig setShowGif(boolean showGif) {
        isShowGif = showGif;
        return this;
    }

    public boolean isShowVideo() {
        return isShowVideo;
    }

    public PickerConfig setShowVideo(boolean showVideo) {
        isShowVideo = showVideo;
        return this;
    }

    public int getSpanCount() {
        return spanCount;
    }

    public PickerConfig setSpanCount(int spanCount) {
        this.spanCount = spanCount;
        return this;
    }

    public int getMaxPickCount() {
        return maxPickCount;
    }

    public PickerConfig setMaxPickCount(int maxPickCount) {
        this.maxPickCount = maxPickCount;
        return this;
    }

    public ArrayList<MediaBean> getOriginSelectList() {
        return originSelectList;
    }

    public PickerConfig setOriginSelectList(ArrayList<MediaBean> originSelectList) {
        this.originSelectList = originSelectList;
        return this;
    }
}
