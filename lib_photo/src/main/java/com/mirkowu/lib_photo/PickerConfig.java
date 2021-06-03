package com.mirkowu.lib_photo;

import com.mirkowu.lib_photo.engine.GlideLoader;
import com.mirkowu.lib_photo.engine.ILoader;

import java.util.ArrayList;

public class PickerConfig {
    private boolean isShowGif = false; //是否显示GIF格式图片 默认不显示
    private boolean isShowVideo = false; //是否显示视频 默认不显示
    private boolean isShowCamera = false; //是否显示拍照按钮 默认不显示
    private ILoader iLoader = null; //图片加载引擎 默认GlideLoader
    private ArrayList<String> originSelectList; //原始选择的图片列表
    private int maxPickCount = PickerConstant.DEFAULT_IMAGE_SIZE; //最大可选图片数量 默认9张
    private int spanCount = PickerConstant.DEFAULT_SPAN_COUNT; //图片展示列表列数 默认3列

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

    public void setSpanCount(int spanCount) {
        this.spanCount = spanCount;
    }

    public int getMaxPickCount() {
        return maxPickCount;
    }

    public PickerConfig setMaxPickCount(int maxPickCount) {
        this.maxPickCount = maxPickCount;
        return this;
    }

    public ILoader getILoader() {
        if (iLoader == null) {
            iLoader = new GlideLoader();
        }
        return iLoader;
    }

    public PickerConfig setILoader(ILoader loader) {
        this.iLoader = loader;
        return this;
    }

    public ArrayList<String> getOriginSelectList() {
        return originSelectList;
    }

    public PickerConfig setOriginSelectList(ArrayList<String> originSelectList) {
        this.originSelectList = originSelectList;
        return this;
    }
}
