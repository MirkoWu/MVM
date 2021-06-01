package com.mirkowu.lib_photo.bean;

import android.text.TextUtils;

/**
 * 图片实体
 */
public class MediaBean {
    public String path;
    public String name;
    public long time;


    public MediaBean(String path, String name, long time){
        this.path = path;
        this.name = name;
        this.time = time;
    }

    @Override
    public boolean equals(Object o) {
        try {
            MediaBean other = (MediaBean) o;
            return TextUtils.equals(this.path, other.path);
        }catch (ClassCastException e){
            e.printStackTrace();
        }
        return super.equals(o);
    }
}
