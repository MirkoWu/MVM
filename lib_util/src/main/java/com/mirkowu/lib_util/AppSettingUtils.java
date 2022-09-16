package com.mirkowu.lib_util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.provider.Settings;

import androidx.fragment.app.Fragment;

/**
 * @author by DELL
 * @date on 2019/8/24
 * @describe
 */
public class AppSettingUtils {


    public static void startNetworkSetting(Context context) {
        Intent intent;
        try {
            intent = new Intent(Settings.ACTION_DATA_ROAMING_SETTINGS);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        } catch (Exception e) {
            try {
                intent = new Intent(Settings.ACTION_WIRELESS_SETTINGS);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            } catch (Exception e1) {
                LogUtil.e("startNetworkSetting", e1.toString());
            }
        }
    }


    public static boolean startAppSettingNoResult(Context context) {
        try {
            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                    Uri.parse("package:" + context.getPackageName()));
            intent.addCategory(Intent.CATEGORY_DEFAULT);
            //ForResult不能加NEW_TASK
            context.startActivity(intent);
            return true;
        } catch (Exception e) {
            LogUtil.e("startAppSettingNoResult", e.toString());
        }
        return false;
    }

    public static boolean startAppSettingForResult(Activity activity, int requestCode) {
        try {
            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            intent.setData(Uri.parse("package:" + activity.getPackageName()));
            intent.addCategory(Intent.CATEGORY_DEFAULT);
            //ForResult不能加NEW_TASK
            activity.startActivityForResult(intent, requestCode);
            return true;
        } catch (Exception e) {
            LogUtil.e("Activity startAppSettingForResult", e.toString());
        }
        return false;
    }

    public static boolean startAppSettingForResult(Fragment fragment, int requestCode) {
        try {
            final Context context = fragment.getContext();
            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            intent.setData(Uri.parse("package:" + context.getPackageName()));
            intent.addCategory(Intent.CATEGORY_DEFAULT);
            //ForResult不能加NEW_TASK
            fragment.startActivityForResult(intent, requestCode);
            return true;
        } catch (Exception e) {
            LogUtil.e("Fragment startAppSettingForResult", e.toString());
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


}
