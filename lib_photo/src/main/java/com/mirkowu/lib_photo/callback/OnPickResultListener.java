package com.mirkowu.lib_photo.callback;

import com.mirkowu.lib_photo.bean.MediaBean;

import java.util.ArrayList;

public interface OnPickResultListener {
    void onPickResult(ArrayList<MediaBean> imageList);
}
