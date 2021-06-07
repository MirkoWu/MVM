package com.mirkowu.lib_photo.bean;

import android.text.TextUtils;

import java.util.List;

/**
 * 文件夹
 */
public class FolderBean {
    public String name;
    public String path;
    public MediaBean cover;
    public List<MediaBean> mediaList;

    @Override
    public boolean equals(Object o) {
        if (o instanceof FolderBean) {
            FolderBean other = (FolderBean) o;
            return TextUtils.equals(other.path, path);
        }
        return super.equals(o);
    }

    @Override
    public String toString() {
        return "FolderBean{" +
                "name='" + name + '\'' +
                ", path='" + path + '\'' +
                ", cover=" + cover +
                ", mediaList=" + mediaList +
                '}';
    }
}
