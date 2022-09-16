package com.mirkowu.lib_util;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.text.TextUtils;

/**
 * @author by DELL
 * @date on 2019/8/24
 * @describe
 */
public class AppInfoUtils {
    /**
     * 获取版本号
     *
     * @return 当前应用的版本号
     */
    public static String getVersionName(Context context) {
        PackageInfo info = getPackageInfo(context);
        return info == null ? null : info.versionName;
    }

    public static int getVersionCode(Context context) {
        PackageInfo info = getPackageInfo(context);
        return info == null ? 0 : info.versionCode;
    }

    /**
     * 获取当前应用信息类
     *
     * @param context
     * @return PackageInfo
     */
    public static PackageInfo getPackageInfo(Context context) {
        try {
            PackageManager manager = context.getPackageManager();
            PackageInfo info = manager.getPackageInfo(context.getPackageName(), 0);
            return info;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 根据包名获取意图
     *
     * @param context     上下文
     * @param packageName 包名
     * @return 意图
     */
    private static Intent getIntentByPackageName(Context context, String packageName) {
        return context.getPackageManager().getLaunchIntentForPackage(packageName);
    }

    /**
     * 根据包名判断App是否安装
     *
     * @param context     上下文
     * @param packageName 包名
     * @return {@code true}: 已安装<br>{@code false}: 未安装
     */
    public static boolean isInstallApp(Context context, String packageName) {
        if (TextUtils.isEmpty(packageName)) return false;
        return getIntentByPackageName(context, packageName) != null;
    }

    /**
     * 打开指定包名的App
     *
     * @param context     上下文
     * @param packageName 包名
     * @return {@code true}: 打开成功<br>{@code false}: 打开失败
     */
    public static boolean startAppByPackageName(Context context, String packageName) {
        try {
            Intent intent = getIntentByPackageName(context, packageName);
            if (intent != null) {
                context.startActivity(intent);
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

}
