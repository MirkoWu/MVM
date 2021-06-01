package com.mirkowu.lib_photo.bean;

import android.text.TextUtils;

import java.util.List;

/**
 * 文件夹
 */
public class Folder {
    public String name;
    public String path;
    public MediaBean cover;
    public List<MediaBean> mediaBeans;

    @Override
    public boolean equals(Object o) {
        try {
            Folder other = (Folder) o;
            return TextUtils.equals(other.path, path);
        }catch (ClassCastException e){
            e.printStackTrace();
        }
        return super.equals(o);
    }
}
