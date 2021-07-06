package com.mirkowu.lib_network.download;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.ArrayMap;

import androidx.annotation.RequiresApi;

import com.mirkowu.lib_util.LogUtil;
import com.mirkowu.lib_util.utilcode.util.UriUtils;
import com.mirkowu.lib_util.utilcode.util.Utils;

import java.io.File;
import java.io.OutputStream;
import java.util.Map;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.ObservableEmitter;
import io.reactivex.rxjava3.core.ObservableOnSubscribe;
import io.reactivex.rxjava3.observers.DisposableObserver;
import io.reactivex.rxjava3.schedulers.Schedulers;
import okhttp3.Call;
import okhttp3.Request;
import okhttp3.Response;
import okio.BufferedSink;
import okio.Okio;
import okio.Sink;

public class Downloader {


    private static Map<Long, Downloader> sRequestMap = new ArrayMap();

    private boolean isDebug;
    private String url;
    private String filePath;
    private Map<String, String> header;
    private OnDownloadListener downloadListener;
    private DisposableObserver<File> observer;
    private Call call;

    private Downloader() {
    }

    private Downloader(String url) {
        this.url = url;
    }

    public static Downloader create(String url) {
        return new Downloader(url);
    }

    public static void cancel(long id) {
        if (sRequestMap.containsKey(id)) {
            Downloader downloader = sRequestMap.get(id);
            if (downloader != null && !downloader.isCanceled()) {
                downloader.cancel();
            }
            sRequestMap.remove(downloader);
            downloader = null;
        }
    }

    private void clear() {
        if (sRequestMap.containsValue(this)) {
            if (!isCanceled()) {
                cancel();
            }
            sRequestMap.remove(this);
        }
    }

    private void disposeTask() {
        if (observer != null && !observer.isDisposed()) {
            observer.dispose();
        }
    }

    public Downloader setOnProgressListener(OnDownloadListener downloadListener) {
        this.downloadListener = downloadListener;
        return this;
    }

//    public Downloader isUseSSLVerifier(boolean isUseSSLVerifier) {
//        this.isUseSSLVerifier = isUseSSLVerifier;
//        return this;
//    }

//    public Downloader setIsDebug(boolean isDebug) {
//        this.isDebug = isDebug;
//        return this;
//    }

    public Downloader setUrl(String url) {
        this.url = url;
        return this;
    }

    /**
     * Android 10及以上 如果要保存在公共以外的目录，需要开启
     * {@link android.permission.MANAGE_EXTERNAL_STORAGE} 权限
     * 否则默认存储在{@link Environment.DIRECTORY_DOWNLOADS} 目录下
     * <p>
     * 同时 也要动态权限获取存储权限。
     * 如果时私有目录(包括内部和外部存储) 都不需要权限
     * <p>
     * 注意:
     * 公共目录中 有关图片相关目录只能存储图片文件，视频目录存放视频，音频文件存放音频，
     *
     * @param path
     * @return
     */
    public Downloader setFilePath(@NonNull String path) {
        this.filePath = path;
        return this;
    }


    public Downloader setHeader(Map<String, String> header) {
        this.header = header;
        return this;
    }

    private boolean isCanceled() {
        return call != null && call.isCanceled();
    }

    public void cancel() {
        if (call != null) {
            call.cancel();
        }
    }

    public long start() {
        if (TextUtils.isEmpty(filePath)) {
            if (downloadListener != null) {
                downloadListener.onFailure(new IllegalArgumentException("filePath 不能为空！"));
            }
            return -1;
        }
        if (TextUtils.isEmpty(url)) {
            if (downloadListener != null) {
                downloadListener.onFailure(new IllegalArgumentException("url 不能为空！"));
            }
            return -1;
        }
        Request.Builder builder = new Request.Builder();
        builder.url(url).get();
        if (header != null && !header.isEmpty()) {
            for (Map.Entry<String, String> entry : header.entrySet()) {
                builder.header(entry.getKey(), entry.getValue());
            }
        }

        RequestConfig config = new RequestConfig(builder.build(), downloadListener);
        call = DownloadClient.getInstance().createCall(config);

        Observable<File> observable = Observable.create(new ObservableOnSubscribe<File>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<File> emitter) throws Throwable {
                try {
                    Response response = call.execute();
                    if (response.isSuccessful()) {
                        File file;
                        //大于等于android10,且存在外部存储
//                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q && isExternalMediaDir(filePath)) {
//                            file = saveFileOnAndroidQ(response, filePath);
//                        } else {
                            file = saveFile(response, filePath);
//                        }
                        emitter.onNext(file);
                        emitter.onComplete();
                    } else {
                        emitter.onError(new RuntimeException("HttpException: code = " + response.code() + ", message = " + response.message()));
                    }
                } catch (Throwable e) {
                    emitter.onError(e);
                }
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
        observer = new DisposableObserver<File>() {

            @Override
            public void onNext(@NonNull File file) {
                LogUtil.e("下载成功：" + file.getAbsolutePath());
                if (downloadListener != null) {
                    downloadListener.onSuccess(file);
                }
            }

            @Override
            public void onError(@NonNull Throwable e) {
                e.printStackTrace();
                LogUtil.e("下载失败：" + e.getMessage());
                if (downloadListener != null) {
                    downloadListener.onFailure(e);
                }
                disposeTask();
            }

            @Override
            public void onComplete() {
                disposeTask();
            }
        };

        long id = System.currentTimeMillis();
        observable.subscribe(observer);
        sRequestMap.put(id, this);
        return id;
    }


    private boolean isExternalDir(String path) {
        if (TextUtils.isEmpty(path)) return false; //路径为空 则默认存储在缓存中
        String rootPath = Environment.getExternalStorageDirectory().getAbsolutePath();
        return path.startsWith(rootPath); //外部公共空间
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    private boolean isExternalMediaDir(String path) {
        if (TextUtils.isEmpty(path)) return false; //路径为空 则默认存储在缓存中
        return isPublicDirectory(path);
//
//        String rootPath = Environment.getExternalStorageDirectory().getAbsolutePath();
//        return path.startsWith(rootPath); //外部公共空间
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    public static final String[] STANDARD_DIRECTORIES = {
            Environment.DIRECTORY_MUSIC,
            Environment.DIRECTORY_PODCASTS,
            Environment.DIRECTORY_RINGTONES,
            Environment.DIRECTORY_ALARMS,
            Environment.DIRECTORY_NOTIFICATIONS,
            Environment.DIRECTORY_PICTURES,
            Environment.DIRECTORY_MOVIES,
            Environment.DIRECTORY_DOWNLOADS,
            Environment.DIRECTORY_DCIM,
            Environment.DIRECTORY_DOCUMENTS,
            Environment.DIRECTORY_AUDIOBOOKS,
    };


    @RequiresApi(api = Build.VERSION_CODES.Q)
    private static boolean isPublicDirectory(String dir) {
        for (String valid : STANDARD_DIRECTORIES) {
            if (dir.startsWith(valid) || dir.startsWith(
                    Environment.getExternalStoragePublicDirectory(valid).getAbsolutePath())) {
                return true;
            }
        }
        return false;
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    public static String getPublicDirectory(String dir) {
        for (String valid : STANDARD_DIRECTORIES) {
            if (dir.startsWith(File.separator + valid) || dir.startsWith(
                    Environment.getExternalStoragePublicDirectory(valid).getAbsolutePath())) {
                return valid;
            }
        }
        return null;
    }

    /**
     * 只有大于等于 Android 10 的才用这种方式，否则就用传统的文件存储
     *
     * @param response
     * @param path
     * @return
     * @throws Throwable
     */
    @RequiresApi(api = Build.VERSION_CODES.Q)
    private File saveFileOnAndroidQ(Response response, String path) throws Throwable {
        File file = new File(path);
        // 创建ContentValues, 并加入信息
        ContentValues values = new ContentValues();
//        values.put(MediaStore.Images.Media.DESCRIPTION, file.getName());
        values.put(MediaStore.Files.FileColumns.DISPLAY_NAME, file.getName());
//        values.put(MediaStore.Images.Media.MIME_TYPE, file.type());
//        values.put(MediaStore.Images.Media.TITLE, file.getName());

        //外部存储文件夹
        path = file.getParentFile().getAbsolutePath();  // DCIM/IMAGE_sdfs.jpg  /storage/0...
        if (path.startsWith(File.separator)) {
            path = path.substring(File.separator.length());
        }
        if (!TextUtils.isEmpty(path)) {
            values.put(MediaStore.Files.FileColumns.RELATIVE_PATH, path);
        }

//        //path.startsWith()
//        String publicDir = getPublicDirectory(path); //DCIM
//
//        String subPath = "";
//        if (path.startsWith(File.separator + publicDir)) {
//            subPath = path.substring((File.separator + publicDir).length()); // /DCIM
//        } else if (path.startsWith(Environment.getExternalStoragePublicDirectory(publicDir).getAbsolutePath())) {
//            subPath = path.substring(Environment.getExternalStoragePublicDirectory(publicDir).getAbsolutePath().length()); // /DCIM
//        }
//
////        String rootPath = Environment.getExternalStorageDirectory().getAbsolutePath();
////        String subPath = "";
////        if (path.startsWith(rootPath)) {
////            subPath = path.substring(rootPath.length()); // /DCIM
////        }
////        String subPath = path.substring(rootPath.length()); // /DCIM
//        if (subPath.startsWith(File.separator)) {
//            subPath = subPath.substring(File.separator.length());
//        }
//
//        if (!TextUtils.isEmpty(subPath)) {
//            values.put(MediaStore.Files.FileColumns.RELATIVE_PATH, subPath);
//        }

        // 插入到ContentResolver，并返回Uri
        ContentResolver resolver = Utils.getApp().getContentResolver();
        Uri insertUri = resolver.insert(MediaStore.Files.getContentUri("external"), values);
//        Uri insertUri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
//        Uri insertUri = resolver.insert(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, values);
//        Uri insertUri = resolver.insert(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, values);
//        Uri insertUri = resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, values);

        // 获取OutputStream
        OutputStream outputStream = resolver.openOutputStream(insertUri);
        Sink sink = Okio.sink(outputStream);
        BufferedSink buffer = Okio.buffer(sink);
        buffer.writeAll(response.body().source());
        buffer.flush();
//        return file;
        return UriUtils.uri2File(insertUri);
    }

    private File saveFile(Response response, String path) throws Throwable {
        File file = new File(path);
        Sink sink = Okio.sink(file);
        BufferedSink buffer = Okio.buffer(sink);
        buffer.writeAll(response.body().source());
        buffer.flush();
        return file;
    }

}
