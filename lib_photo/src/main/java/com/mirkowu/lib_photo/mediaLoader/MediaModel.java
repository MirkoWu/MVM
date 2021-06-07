package com.mirkowu.lib_photo.mediaLoader;

import android.text.TextUtils;

import com.mirkowu.lib_photo.bean.FolderBean;
import com.mirkowu.lib_photo.bean.MediaBean;

import java.util.ArrayList;
import java.util.List;

public class MediaModel {
    private static ArrayList<FolderBean> sFolderList = new ArrayList<>();
    private static ArrayList<MediaBean> sCurChildList = new ArrayList<>(); //当前文件夹文件列表
    private static int sFolderPos = 0;

    public static void init(List<FolderBean> list) {
        clear();
        sFolderList.addAll(list);
        if (!isEmpty()) { //默认第一个文件夹 就是所有
            sCurChildList.addAll(sFolderList.get(0).mediaList);
        }
    }

    public static void selectFolder(int position) {
        if (position < 0 || position >= sFolderList.size()) {
            return;
        }
        sCurChildList.clear();
        sCurChildList.addAll(sFolderList.get(position).mediaList);
    }

    public static ArrayList<MediaBean> getCurChildList() {
        return sCurChildList;
    }


    public static boolean contains(MediaBean bean) {
        return sFolderList.contains(bean);
    }

    /**
     *
     *
     * @param path
     * @return
     */
    public static MediaBean containsPath(String path) {
        if (TextUtils.isEmpty(path)) {
            return null;
        }
        for (MediaBean bean : sCurChildList) {
            if (TextUtils.equals(path, bean.path)) {
                return bean;
            }
        }

        return null;
    }

    public static boolean isEmpty() {
        return sFolderList.isEmpty();
    }

    public static int size() {
        return sFolderList.size();
    }

    public static void clear() {
        sFolderPos = 0;
        sFolderList.clear();
        sCurChildList.clear();
    }
}
