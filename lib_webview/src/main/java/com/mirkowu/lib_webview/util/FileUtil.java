package com.mirkowu.lib_webview.util;


import android.text.TextUtils;

import com.mirkowu.lib_util.LogUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class FileUtil {
    private static final int CACHE_SIZE = 1024; //复制数据 需要的缓存大小

    /**
     * 解压文件
     *
     * @param srcZipFile 源文件
     * @param des        目标文件目录
     * @return 解压的状态。true表示解压缩成功，false表示解压缩失败
     */
    public static boolean unzip(File srcZipFile, File des) {
        if (srcZipFile == null || !srcZipFile.exists() || des == null) {
            return false;
        }
        int count;
        byte[] cache = new byte[CACHE_SIZE];
        ZipInputStream zipInputStream = null;
        FileOutputStream zipOutputStream = null;
        try {
            zipInputStream = new ZipInputStream(new FileInputStream(srcZipFile));
            ZipEntry zipEntry;
            while ((zipEntry = zipInputStream.getNextEntry()) != null) {
                LogUtil.e("tag", "while=====zipEntry====" + zipEntry);
                if (zipEntry.isDirectory()) {
                    File desDir = new File(des, zipEntry.getName());
                    desDir.mkdir();
                } else {
                    File desFile = new File(des, zipEntry.getName());
                    desFile.getParentFile().mkdirs();
                    zipOutputStream = new FileOutputStream(desFile);
                    while ((count = zipInputStream.read(cache)) != -1) {
                        zipOutputStream.write(cache, 0, count);
                    }
                }
            }
            LogUtil.e("tag", "zipEntry====" + zipEntry);
            return zipEntry == null;
        } catch (Exception e) {
            LogUtil.e("tag", "unzip----Exception ---=" + e.toString());
            return false;
        } finally {
            if (zipInputStream != null) {
                try {
                    zipInputStream.close();
                } catch (IOException e) {
                    LogUtil.e(e.toString());
                }
            }
            if (zipOutputStream != null) {
                try {
                    zipOutputStream.close();
                } catch (IOException e) {
                    LogUtil.e(e.toString());
                }
            }
        }
    }

    /**
     * 删除指定文件夹以及文件夹下面的所有文件
     *
     * @param dir 要删除的文件夹目录
     */
    public static void delDir(File dir) {
        if (dir.isDirectory()) {
            File[] subFiles = dir.listFiles();
            if (subFiles != null && subFiles.length > 0) {
                for (File subFile : subFiles) {
                    delDir(subFile);
                }
            }
            dir.delete();
        } else {
            dir.delete();
        }
    }

    /***
     * 获取文件类型
     */
    public static String getFileType(String paramString) {
        String str = "";

        if (TextUtils.isEmpty(paramString)) {
            return str;
        }
        int i = paramString.lastIndexOf('.');
        if (i <= -1) {
            return str;
        }
        str = paramString.substring(i + 1);
        return str;
    }
}
