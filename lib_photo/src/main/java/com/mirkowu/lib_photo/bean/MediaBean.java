package com.mirkowu.lib_photo.bean;

import android.text.TextUtils;

import androidx.annotation.NonNull;

/**
 * 图片实体
 */
public class MediaBean {
    @NonNull
    public String path;
    public String name;
    public String type;
    public long time;
    public long duration;


    public MediaBean(@NonNull String path) {
        this.path = path;
    }

    public MediaBean(@NonNull String path, String name, String type, long time) {
        this.path = path;
        this.name = name;
        this.type = type;
        this.time = time;
    }

    public MediaBean(@NonNull String path, String name, String type, long time, long duration) {
        this.path = path;
        this.name = name;
        this.type = type;
        this.time = time;
        this.duration = duration;
    }

    @Override
    public boolean equals(Object o) {
        try {
            MediaBean other = (MediaBean) o;
            return TextUtils.equals(this.path, other.path);
        } catch (ClassCastException e) {
            e.printStackTrace();
        }
        return super.equals(o);
    }
}
