package com.mirkowu.lib_util;

import android.util.Log;

import com.mirkowu.lib_util.utilcode.util.CrashUtils;
import com.orhanobut.logger.AndroidLogAdapter;
import com.orhanobut.logger.Logger;


public class LogUtil {
    public static final String TAG = "WM_LogUtils";
    private static boolean isDebug;

    public static void init(boolean isOpenDebug) {
        isDebug = isOpenDebug;
        //设置打印适配器，保证只有一个
        Logger.clearLogAdapters();
        Logger.addLogAdapter(new AndroidLogAdapter());

        //本地Crash日志
        CrashUtils.init();
    }

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

    public static void e(Throwable t, String msg) {
        if (isDebug) {
            Logger.e(t, msg);
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
