package com.mirkowu.lib_photo.utils;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;

import androidx.core.content.FileProvider;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * 文件操作类
 * Created by Nereo on 2015/4/8.
 */
public class FileUtils {

    public static Uri createFileUri(Context context, File file) {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
            String authority = context.getPackageName() + ".mvm_ivp.fileProvider";
            return FileProvider.getUriForFile(context, authority, file);
        } else {
            return Uri.fromFile(file);
        }
    }

    public static void saveBitmapToGallery(Context context, Bitmap bitmap, File dirFile, String saveName) {
        if (dirFile != null && dirFile.exists() && dirFile.isDirectory()) {

            File file = new File(dirFile, saveName);
            Log.d("imagepricek  ", file.getAbsolutePath());
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
            try {
                //保存到系统相册
                MediaStore.Images.Media.insertImage(context.getContentResolver(), file.getAbsolutePath(), saveName, null);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            Log.d("getAbsolutePath=", file.getAbsolutePath() + " Uri.fromFile=" + Uri.fromFile(file));
            context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(file)));//刷新相册

        } else {
            throw new RuntimeException("the file is not exists or it is not directory !");
        }


    }

    /**
     * 根据Uri获取图片路径
     *
     * @param context
     * @param uri
     * @return
     */
    public static String getFilePathByContentResolver(Context context, Uri uri) {
        if (null == uri) {
            return null;
        }
        Cursor c = context.getContentResolver().query(uri, null, null, null, null);
        String filePath = null;
        if (null == c) {
            throw new IllegalArgumentException("Query on " + uri + " returns null result.");
        }
        try {
            if ((c.getCount() != 1) || !c.moveToFirst()) {
            } else {
                filePath = c.getString(c.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA));
            }
        } finally {
            c.close();
        }
        return filePath;
    }

    //往SD卡写入文件的方法
    public void savaFileToSD(Context context, String filename, byte[] bytes) throws Exception {
        //如果手机已插入sd卡,且app具有读写sd卡的权限
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            String filePath = Environment.getExternalStorageDirectory().getCanonicalPath() + "/budejie";
            File dir1 = new File(filePath);
            if (!dir1.exists()) {
                dir1.mkdirs();
            }
            filename = filePath + "/" + filename;
            //这里就不要用openFileOutput了,那个是往手机内存中写数据的
            FileOutputStream output = new FileOutputStream(filename);
            output.write(bytes);
            //将bytes写入到输出流中
            output.close();
            //关闭输出流
            Toast.makeText(context, "图片已成功保存到" + filePath, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, "SD卡不存在或者不可读写", Toast.LENGTH_SHORT).show();
        }
    }

    public static void saveBitmap2File(Context context, Bitmap bitmap, File dirFile, String saveName) {
        if (dirFile != null && dirFile.exists() && dirFile.isDirectory()) {

            File file = new File(dirFile, saveName);
            Log.d("imagepricek  ", file.getAbsolutePath());
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
            context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(file))); //刷新相册
        } else {
            throw new RuntimeException("the file is not exists or it is not directory !");
        }
    }

//    /**
//     * @param context
//     * @param url     图片的下载地址
//     */
//    public static Observable<Bitmap> save2SDCard(final Context context, final String url) {
//        Log.d("imagepricek url==", url);
//        return Observable.create(new ObservableOnSubscribe<Bitmap>() {
//            @Override
//            public void subscribe(ObservableEmitter<Bitmap> e) throws Exception {
//                Bitmap bitmap = ImagePicker.getInstance()
//                        .getImageEngine()
//                        .loadAsBitmap(context, url);
//
//                if (!e.isDisposed()) e.onNext(bitmap);
//            }
//        }).subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread());
//    }

}
