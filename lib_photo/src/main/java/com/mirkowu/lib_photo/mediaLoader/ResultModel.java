package com.mirkowu.lib_photo.mediaLoader;

import com.mirkowu.lib_photo.ImagePicker;
import com.mirkowu.lib_photo.bean.MediaBean;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

public class ResultModel {
    //    private static ArrayList<MediaBean> sList = new ArrayList<>();
    private static LinkedHashMap<String, MediaBean> sMap = new LinkedHashMap<>();
    private static boolean sIsOriginPhoto;

    public static ArrayList<MediaBean> getList() {
        return new ArrayList<>(sMap.values());
    }

    public static HashMap<String, MediaBean> getMap() {
        return sMap;
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
        return sMap.size() < pickCount;
    }


    public static void addSingle(MediaBean bean) {
        clear();
//        sList.add(bean);
        sMap.put(bean.path, bean);
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

    public static void addMulti(MediaBean bean) {
        if (checkCanAdd() && !contains(bean)) {
            sMap.put(bean.path, bean);
        }
    }

    public static void remove(MediaBean bean) {
        if (contains(bean)) {
            sMap.remove(bean.path);
        }
    }

    public static boolean contains(MediaBean bean) {
        return sMap.containsKey(bean.path);
    }

    public static boolean isEmpty() {
        return sMap.isEmpty();
    }

    public static int size() {
        return sMap.size();
    }

    public static void clear() {
        sIsOriginPhoto = false;
        sMap.clear();
    }
}
