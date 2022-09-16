package com.mirkowu.lib_util;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import androidx.annotation.NonNull;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * 使用的系统 分享 和自己的账号没关系
 */
public class SystemShareUtils {


    /**
     * 分享到微信朋友圈 带文字和图片
     *
     * @param context
     * @param content
     * @param file
     */

    public static boolean shareToWxCircle(@NonNull Context context, String content, @NonNull File file) {
        try {
            Uri uri = FileUtils.createUri(context, file);
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_SEND);
            intent.putExtra(Intent.EXTRA_STREAM, uri);
            intent.putExtra("Kdescription", content);
            intent.setType("image/*");
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            context.startActivity(Intent.createChooser(intent, "选择要分享的应用"));
            return true;
        } catch (Throwable e) {
            LogUtil.e("分享失败", e.toString());
        }
        return false;
    }

    public static boolean shareText(@NonNull Context context, @NonNull String text) {
        try {
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_SEND);
            intent.putExtra(Intent.EXTRA_TEXT, text);
            intent.setType("text/*");
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            context.startActivity(Intent.createChooser(intent, "选择要分享的应用"));
            return true;
        } catch (Throwable e) {
            LogUtil.e("分享失败", e.toString());
        }
        return false;
    }

    public static boolean shareImage(@NonNull Context context, @NonNull File file) {
        try {
            Uri uri = FileUtils.createUri(context, file);
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_SEND);
            intent.putExtra(Intent.EXTRA_STREAM, uri);
            intent.setType("image/*");
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            context.startActivity(Intent.createChooser(intent, "选择要分享的应用"));
            return true;
        } catch (Throwable e) {
            LogUtil.e("分享失败", e.toString());
        }
        return false;
    }

    public static boolean shareImage(@NonNull Context context, @NonNull List<File> files) {
        try {
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_SEND_MULTIPLE);
            ArrayList<Uri> imageUris = new ArrayList<Uri>();
            for (File file : files) {
                Uri uri = FileUtils.createUri(context, file);
                imageUris.add(uri);
            }
            intent.setType("image/*");
            intent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, imageUris);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            context.startActivity(Intent.createChooser(intent, "选择要分享的应用"));
            return true;
        } catch (Throwable e) {
            LogUtil.e("分享失败", e.toString());
        }
        return false;
    }
}
