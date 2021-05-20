package com.mirkowu.lib_util;

import com.google.gson.Gson;

import java.lang.reflect.Type;

/**
 * @author: mirko
 * @date: 20-1-8
 */
public class GsonUtil {

    private static class Holder {
        private static final Gson mGson = new Gson();
    }


    public static <T> T fromJson(String json, Class<T> classOfT) {
        try {
            return Holder.mGson.fromJson(json, classOfT);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static <T> T fromJson(String json, Type typeOfT) {
        try {
            return Holder.mGson.fromJson(json, typeOfT);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
