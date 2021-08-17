package com.mirkowu.lib_util;

import android.util.Log;

import androidx.annotation.NonNull;

import com.mirkowu.lib_util.utilcode.util.CrashUtils;
import com.orhanobut.logger.AndroidLogAdapter;
import com.orhanobut.logger.Logger;


public class LogUtil {
    public static final String TAG = "WM_LogUtils";
    private static boolean sIsDebug;

    public static void init(boolean isOpenDebug) {
        init(isOpenDebug, true);
    }

    /**
     * 工具类初始化
     *
     * @param isOpenDebug      是否开启Debug Log
     * @param isOpenLocalCrash 是否开启本地Crash保存功能
     */
    public static void init(boolean isOpenDebug, boolean isOpenLocalCrash) {
        sIsDebug = isOpenDebug;
        //设置打印适配器，保证只有一个
        Logger.clearLogAdapters();
        Logger.addLogAdapter(new AndroidLogAdapter());

        //本地Crash日志
        if (isOpenLocalCrash) {
            CrashUtils.init();
        }
    }

    public static void json(String json) {
        if (sIsDebug) {
            try {
                Logger.json(json);
            } catch (Throwable e) {
                e(TAG, "------------Json数据异常----------");
                e.printStackTrace();
            }
        }
    }

    public static void i(@NonNull String msg) {
        if (sIsDebug) {
            Logger.i(msg);
        }
    }

    public static void d(@NonNull String msg) {
        if (sIsDebug) {
            Logger.d(msg);
        }
    }

    public static void e(@NonNull String msg) {
        if (sIsDebug) {
            Logger.e(msg);
        }
    }

    public static void e(Throwable e) {
        if (sIsDebug) {
            Logger.e(e, e.toString());
        }
    }

    public static void e(@NonNull String msg, Throwable t) {
        if (sIsDebug) {
            Logger.e(t, msg);
        }
    }

    public static void i(String tag, String msg) {
        if (sIsDebug) {
            Log.i(tag, msg);
        }
    }

    public static void d(String tag, String msg) {
        if (sIsDebug) {
            Log.d(tag, msg);
        }
    }

    public static void e(String tag, String msg) {
        if (sIsDebug) {
            Log.e(tag, msg);
        }
    }

    public static void e(String tag, String msg, Throwable t) {
        if (sIsDebug) {
            Log.e(tag, msg, t);
        }
    }


}
