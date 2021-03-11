package com.mirkowu.lib_mvm.util;

import android.util.Log;

import com.orhanobut.logger.Logger;


public class LogUtil {
    public static final String TAG = "LogUtils";
    public static boolean isDebug = true;

    public static void json(String json) {
        if (isDebug) {
            try {
                Logger.json(json);
            } catch (Throwable e) {
                e(TAG, "------------Json数据异常----------");
                e.printStackTrace();
            }
        }
    }

    public static void i(String msg) {
        if (isDebug) {
            Logger.i(msg);
        }
    }

    public static void d(String msg) {
        if (isDebug) {
            Logger.d(msg);
        }
    }

    public static void e(String msg) {
        if (isDebug) {
            Logger.e(msg);
        }
    }

    public static void i(String tag, String msg) {
        if (isDebug) {
            Log.i(tag, msg);
        }
    }

    public static void d(String tag, String msg) {
        if (isDebug) {
            Log.d(tag, msg);
        }
    }

    public static void e(String tag, String args) {
        if (isDebug) {
            Log.e(tag, args);
        }
    }

    public static void e(String tag, String args, Throwable t) {
        if (isDebug) {
            Log.e(tag, args, t);
        }
    }


}
