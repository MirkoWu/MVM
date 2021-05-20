package com.mirkowu.lib_util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.provider.Settings;
import android.text.TextUtils;

/**
 * @author by DELL
 * @date on 2019/8/24
 * @describe
 */
public class AppInfoUtil {
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

//    public static void toSelfSetting(Context context) {
//        try {
//            Intent intent = new Intent();
//            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            if (Build.VERSION.SDK_INT >= 9) {
//                intent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
//                intent.setData(Uri.fromParts("package", context.getPackageName(), null));
//            } else if (Build.VERSION.SDK_INT <= 8) {
//                intent.setAction(Intent.ACTION_VIEW);
//                intent.setClassName("com.android.settings", "com.android.setting.InstalledAppDetails");
//                intent.putExtra("com.android.settings.ApplicationPkgName", context.getPackageName());
//            }
//            context.startActivity(intent);
//        } catch (Exception e) {
//            e.printStackTrace();
//            //  UMManager.reportError(e);
//        }
//    }

    public static boolean startAppSettingDetail(Activity activity, int requestCode) {
        try {
            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                    Uri.parse("package:" + activity.getPackageName()));
            intent.addCategory(Intent.CATEGORY_DEFAULT);
            //ForResult不能加NEW_TASK
            //intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            activity.startActivityForResult(intent, requestCode);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static void startNetworkSetting(Context context) {
        Intent intent;
        try {
            intent = new Intent(Settings.ACTION_DATA_ROAMING_SETTINGS);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();

            intent = new Intent(Settings.ACTION_WIRELESS_SETTINGS);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        }
    }

}
