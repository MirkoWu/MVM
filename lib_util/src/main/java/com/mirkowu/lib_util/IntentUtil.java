package com.mirkowu.lib_util;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;

import java.io.File;

/**
 * @author: mirko
 * @date: 20-3-31
 */
public class IntentUtil {
    /**
     * 打开Scheme意图
     *
     * @param context
     * @param url
     * @return
     */
    public static boolean openScheme(Context context, String url) {
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(url));
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
            return true;
        } catch (Throwable e) {
            LogUtil.e(e.toString());
        }
        return false;
    }

    /**
     * 打开浏览器
     *
     * @param context
     * @param url
     * @return
     */
    public static boolean openBrowse(Context context, String url) {
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(url));
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
            return true;
        } catch (Throwable e) {
            LogUtil.e(e.toString());
        }
        return false;
    }

    public static void openWeChatScan(Context context) {
        try {
            Intent intent = new Intent();
            intent.setComponent(new ComponentName("com.tencent.mm", "com.tencent.mm.ui.LauncherUI"));
            intent.putExtra("LauncherUI.From.Scaner.Shortcut", true);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.setAction("android.intent.action.VIEW");
            context.startActivity(intent);
        } catch (Exception e) {
            LogUtil.e(e.toString());
        }
    }

    public static boolean openAliPay(Context context, String url) {
        try {
            Intent intent = Intent.parseUri(url, Intent.URI_INTENT_SCHEME);
            context.startActivity(intent);
            return true;
        } catch (Throwable e) {
            LogUtil.e(e.toString());
        }
        return false;

    }

    /**
     * 启动apk安装
     */
    public static void startInstall(Context context, File apkFile) {
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setDataAndType(FileUtil.createUri(context, apkFile), "application/vnd.android.package-archive");
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            }
            context.startActivity(intent);
        } catch (Throwable e) {
            LogUtil.e(e.toString());
        }
    }


}
