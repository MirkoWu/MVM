package com.mirkowu.lib_util;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.text.TextUtils;

import androidx.core.content.FileProvider;

import com.mirkowu.lib_util.utilcode.util.UriUtils;
import com.mirkowu.lib_util.utilcode.util.Utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * @author: mirko
 * @date: 19-8-21
 */
public class FileUtil {
    public static final String TAG = FileUtil.class.getName();
//    public static final String FILE_NAME = "Mirko";

    public static Uri createUri(Context context, File file) {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
            return FileProvider.getUriForFile(context, context.getPackageName() + ".mvm.fileProvider", file);
        } else {
            return Uri.fromFile(file);
        }
    }

    /**
     * 得到图片uri的实际地址
     */
    public static String getRealFilePath(final Uri uri) {
        if (null == uri) {
            return null;
        }

        return UriUtils.uri2Path(uri);
    }

    /**
     * 是否存在SD卡
     *
     * @return
     */
    public static boolean hasSDCard() {
        String status = Environment.getExternalStorageState();
        return TextUtils.equals(status, Environment.MEDIA_MOUNTED);
    }


    /**
     * 获取应用SD卡缓存的根路径 持久保存
     *
     * @return
     */
    public static String getDiskExternalPath(final String path) {
        try {
            //判断是否有SD卡
            if (hasSDCard()) {
                if (TextUtils.isEmpty(path)) {
                    return Environment.getExternalStorageDirectory().getAbsolutePath();
                } else {
                    File rootDir = new File(Environment.getExternalStorageDirectory(), path);
                    if (!rootDir.exists()) {
                        rootDir.mkdirs();
                    }
                    return rootDir.getAbsolutePath(); // /mnt/sdcard/path...
                }
            } else {
                if (TextUtils.isEmpty(path)) {
                    return Utils.getApp().getExternalCacheDir().getAbsolutePath();
                }

                File file = new File(Utils.getApp().getExternalCacheDir().getAbsolutePath(), path);
                if (!file.exists()) {
                    file.mkdirs();
                }
                return file.getAbsolutePath(); // /mnt/sdcard/path...
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return "";
    }

    public static File getDiskExternalFile(final String path) {
        try {
            //判断是否有SD卡
            if (hasSDCard()) {
                if (TextUtils.isEmpty(path)) {
                    return Environment.getExternalStorageDirectory();
                } else {
                    File rootDir = new File(Environment.getExternalStorageDirectory(), path);
                    if (!rootDir.exists()) {
                        rootDir.mkdirs();
                    }
                    return rootDir; // /mnt/sdcard/path...
                }
            } else {
                if (TextUtils.isEmpty(path)) {
                    return Utils.getApp().getExternalCacheDir();
                }
                File file = new File(Utils.getApp().getExternalCacheDir().getAbsolutePath(), path);
                if (!file.exists()) {
                    file.mkdirs();
                }
                return file;
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * 获取缓存路径 会跟随app卸载一起删除
     *
     * @param context
     * @return
     */
    public static String getAppCachePath(Context context) {
        if (hasSDCard()) {
            return context.getExternalCacheDir().getAbsolutePath(); // /mnt/sdcard/Android/data/com.my.app/cache
        } else {
            return context.getCacheDir().getAbsolutePath(); // /data/data/com.my.app/cache
        }
    }


    /**
     * 将图片保存到相册
     *
     * @param context
     * @param bmp     要保存的图片bitmap
     * @param file    要保存的路径
     * @return
     */
    public static boolean addGraphToGallery(Context context, Bitmap bmp, File file) {
        boolean result = false;
        try {
            saveBitmapToJPEG(bmp, file);

            //刷新相册
            Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
            Uri contentUri = Uri.fromFile(file);
            mediaScanIntent.setData(contentUri);
            context.sendBroadcast(mediaScanIntent);
            result = true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }


    public static void saveBitmapToJPEG(Bitmap bitmap, File file) throws IOException {
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (fos != null) {
                    fos.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 复制文件
     *
     * @param sourceFile
     * @param targetFile
     * @return
     */
    public static boolean copyFile(File sourceFile, File targetFile) {
        FileInputStream fis = null;
        FileOutputStream fos = null;
        try {
            fis = new FileInputStream(sourceFile);
            fos = new FileOutputStream(targetFile);
            byte[] buffer = new byte[1024];
            while (fis.read(buffer) != -1) {
                fos.write(buffer);
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (fis != null) {
                    fis.close();
                }
                if (fos != null) {
                    fos.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

//    public static boolean saveFileToSdCard(String url, String desPath, String fileName) {
//        FileOutputStream fos = null;//文件输出流
//        try {
//            if (TextUtils.isEmpty(desPath)) return false;
//            File desFile = new File(desPath);
//            if (!desFile.exists() && !desFile.mkdirs()) {
//                return false;
//            }
//
//            URL mUrl = new URL(url);
//            // 获得连接
//            HttpURLConnection conn = (HttpURLConnection) mUrl.openConnection();
//            conn.setConnectTimeout(6000);//设置超时
//            conn.setDoInput(true);
//            conn.setUseCaches(false);//不缓存
//            conn.connect();
//            Log.d("", "saveFileToSdCard" + conn.getResponseCode());
//            if (conn.getResponseCode() == 200) {
//                InputStream is = conn.getInputStream();//获取输入流
//
//                if (is != null) {
//                    fos = new FileOutputStream(desPath + File.pathSeparator + fileName);//指定文件保存路径，代码看下一步
//                    byte[] buf = new byte[1024];
//                    int ch;
//                    while ((ch = is.read(buf)) != -1) {
//                        fos.write(buf, 0, ch);//将获取到的流写入文件中
//                    }
//                    return true;
//                }
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        } finally {
//            try {
//                if (fos != null) {
//                    fos.flush();
//                    fos.close();
//                }
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//        return false;
//    }

    /**
     * 获取文件扩展名  .jpeg .png
     */
    public static String getExtensionName(String filename) {
        if ((filename != null) && (filename.length() > 0)) {
            int dot = filename.lastIndexOf('.');
            if ((dot > -1) && (dot < (filename.length() - 1))) {
                return filename.substring(dot); //带上点 ，dot + 1 不带点
            }
        }
        return filename;
    }

    public static String getContentType(String filename) {
        String type = null;
        Path path = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            path = Paths.get(filename);
            try {
                type = Files.probeContentType(path);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return type;
    }

    //根据图片文件路径判断文件类型
    public static boolean isGifFileType(String path) {
        String type = getContentType(path);
        if (null != type && "image/gif".equalsIgnoreCase(type)) {
            return true;
        }
        return false;
    }
}
