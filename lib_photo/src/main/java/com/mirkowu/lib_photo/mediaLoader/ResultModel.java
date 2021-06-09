package com.mirkowu.lib_photo.mediaLoader;

import android.text.TextUtils;

import com.mirkowu.lib_photo.ImagePicker;
import com.mirkowu.lib_photo.bean.MediaBean;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ResultModel {
    private static LinkedHashMap<String, MediaBean> sMaps = new LinkedHashMap<>();
    private static boolean sIsOriginPhoto;

    public static ArrayList<MediaBean> getList() {
        return new ArrayList<>(sMaps.values());
    }

    public static ArrayList<String> getPaths(List<MediaBean> list) {
        ArrayList<String> paths = new ArrayList<>();
        if (list == null) {
            return paths;
        }
        for (MediaBean bean : list) {
            paths.add(bean.path);
        }
        return paths;
    }

    public static ArrayList<MediaBean> pathsToBeans(List<String> list) {
        ArrayList<MediaBean> beans = new ArrayList<>();
        if (list == null) {
            return beans;
        }
        for (String path : list) {
            beans.add(new MediaBean(path));
        }
        return beans;
    }

    /**
     * 设置 是否原图
     *
     * @param isOriginPhoto
     */
    public static void setIsOriginPhoto(boolean isOriginPhoto) {
        sIsOriginPhoto = isOriginPhoto;
    }


    public static boolean isOriginPhoto() {
        return sIsOriginPhoto;
    }

    public static boolean checkCanAdd() {
        int pickCount = ImagePicker.getInstance().getPickerConfig().getMaxPickCount();
        return sMaps.size() < pickCount;
    }

    /**
     * 添加 列表
     *
     * @param list
     */
    public static void addList(List<MediaBean> list) {
        int pickCount = ImagePicker.getInstance().getPickerConfig().getMaxPickCount();
        if (list != null && list.size() <= pickCount) {
            if (pickCount > 1) {
                for (MediaBean bean : list) {
                    addMulti(bean);
                }
            } else {
                addSingle(list.get(0));
            }
        }
    }

    public static void add(MediaBean bean) {
        int pickCount = ImagePicker.getInstance().getPickerConfig().getMaxPickCount();
        if (pickCount > 1) {
            addMulti(bean);
        } else {
            addSingle(bean);
        }
    }

    public static void addSingle(MediaBean bean) {
        clear();
        sMaps.put(bean.path, bean);
    }


    public static void addMulti(MediaBean bean) {
        if (checkCanAdd() && !contains(bean)) {
            sMaps.put(bean.path, bean);
        }
    }

    public static int getNumber(MediaBean bean) {
//        return sList.indexOf(bean) + 1;
        return indexOf(bean) + 1;
    }

    private static int indexOf(MediaBean bean) {
        int i = 0;
        Iterator pairs = sMaps.entrySet().iterator();
        while (pairs.hasNext()) {
            Map.Entry pair = (Map.Entry) pairs.next();
            MediaBean mediaBean = (MediaBean) pair.getValue();
            if (TextUtils.equals(mediaBean.path, bean.path)) {
                return i;
            }
            i++;
        }
        return -1;
    }

    public static void remove(MediaBean bean) {
        if (contains(bean)) {
            sMaps.remove(bean.path);
        }
    }

    public static boolean contains(MediaBean bean) {
        return sMaps.containsKey(bean.path);
    }

    public static boolean isEmpty() {
        return sMaps.isEmpty();
    }

    public static int size() {
        return sMaps.size();
    }

    public static void clear() {
        sIsOriginPhoto = false;
        sMaps.clear();
    }
}
