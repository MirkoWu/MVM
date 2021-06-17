package com.mirkowu.lib_photo.callback;

import androidx.annotation.NonNull;

import com.mirkowu.lib_photo.bean.MediaBean;

import java.util.ArrayList;

public interface OnPickResultListener {
    void onPickResult(@NonNull ArrayList<MediaBean> imageList);
}
