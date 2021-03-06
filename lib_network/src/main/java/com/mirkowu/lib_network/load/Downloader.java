package com.mirkowu.lib_network.load;

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
    private String mUrl;
    private String mFilePath;
    private Map<String, String> mHeader;
    private OnDownloadListener mDownloadListener;
    private DisposableObserver<File> mObserver;
    private Call mCall;

    private Downloader() {
    }

    private Downloader(String url) {
        this.mUrl = url;
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
        if (mObserver != null && !mObserver.isDisposed()) {
            mObserver.dispose();
        }
    }

    public Downloader setOnProgressListener(OnDownloadListener downloadListener) {
        this.mDownloadListener = downloadListener;
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

    public Downloader setUrl(String mUrl) {
        this.mUrl = mUrl;
        return this;
    }

    /**
     * Android 10????????? ??????????????????????????????????????????????????????
     * {@link android.permission.MANAGE_EXTERNAL_STORAGE} ??????
     * ?????????????????????{@link Environment.DIRECTORY_DOWNLOADS} ?????????
     * <p>
     * ?????? ???????????????????????????????????????
     * ?????????????????????(???????????????????????????) ??????????????????
     * <p>
     * ??????:
     * ??????????????? ?????????????????????????????????????????????????????????????????????????????????????????????????????????
     *
     * @param path
     * @return
     */
    public Downloader setFilePath(@NonNull String path) {
        this.mFilePath = path;
        return this;
    }


    public Downloader setHeader(Map<String, String> mHeader) {
        this.mHeader = mHeader;
        return this;
    }

    private boolean isCanceled() {
        return mCall != null && mCall.isCanceled();
    }

    public void cancel() {
        if (mCall != null) {
            mCall.cancel();
        }
    }

    public long start() {
        if (TextUtils.isEmpty(mFilePath)) {
            if (mDownloadListener != null) {
                mDownloadListener.onFailure(new IllegalArgumentException("filePath ???????????????"));
            }
            return -1;
        }
        if (TextUtils.isEmpty(mUrl)) {
            if (mDownloadListener != null) {
                mDownloadListener.onFailure(new IllegalArgumentException("url ???????????????"));
            }
            return -1;
        }
        Request.Builder builder = new Request.Builder();
        builder.url(mUrl).get();
        if (mHeader != null && !mHeader.isEmpty()) {
            for (Map.Entry<String, String> entry : mHeader.entrySet()) {
                builder.header(entry.getKey(), entry.getValue());
            }
        }

        RequestConfig config = new RequestConfig(builder.build(), mDownloadListener);
        mCall = DownloadClient.getInstance()
                .createCall(config);

        Observable<File> observable = Observable.create(new ObservableOnSubscribe<File>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<File> emitter) throws Throwable {
                try {
                    Response response = mCall.execute();
                    if (response.isSuccessful()) {
                        File file;
                        //????????????android10,?????????????????????
//                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q && isExternalMediaDir(filePath)) {
//                            file = saveFileOnAndroidQ(response, filePath);
//                        } else {
                        file = saveFile(response, mFilePath);
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
        mObserver = new DisposableObserver<File>() {

            @Override
            public void onNext(@NonNull File file) {
                LogUtil.e("???????????????" + file.getAbsolutePath());
                if (mDownloadListener != null) {
                    mDownloadListener.onSuccess(file);
                }
            }

            @Override
            public void onError(@NonNull Throwable e) {
                LogUtil.e("????????????"+ e.toString());
                if (mDownloadListener != null) {
                    mDownloadListener.onFailure(e);
                }
                disposeTask();
            }

            @Override
            public void onComplete() {
                disposeTask();
            }
        };

        long id = System.currentTimeMillis();
        observable.subscribe(mObserver);
        sRequestMap.put(id, this);
        return id;
    }


    private boolean isExternalDir(String path) {
        if (TextUtils.isEmpty(path)) return false; //???????????? ???????????????????????????
        String rootPath = Environment.getExternalStorageDirectory().getAbsolutePath();
        return path.startsWith(rootPath); //??????????????????
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    private boolean isExternalMediaDir(String path) {
        if (TextUtils.isEmpty(path)) return false; //???????????? ???????????????????????????
        return isPublicDirectory(path);
//
//        String rootPath = Environment.getExternalStorageDirectory().getAbsolutePath();
//        return path.startsWith(rootPath); //??????????????????
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
     * ?????????????????? Android 10 ?????????????????????????????????????????????????????????
     *
     * @param response
     * @param path
     * @return
     * @throws Throwable
     */
    @RequiresApi(api = Build.VERSION_CODES.Q)
    private File saveFileOnAndroidQ(Response response, String path) throws Throwable {
        File file = new File(path);
        // ??????ContentValues, ???????????????
        ContentValues values = new ContentValues();
//        values.put(MediaStore.Images.Media.DESCRIPTION, file.getName());
        values.put(MediaStore.Files.FileColumns.DISPLAY_NAME, file.getName());
//        values.put(MediaStore.Images.Media.MIME_TYPE, file.type());
//        values.put(MediaStore.Images.Media.TITLE, file.getName());

        //?????????????????????
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

        // ?????????ContentResolver????????????Uri
        ContentResolver resolver = Utils.getApp().getContentResolver();
        Uri insertUri = resolver.insert(MediaStore.Files.getContentUri("external"), values);
//        Uri insertUri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
//        Uri insertUri = resolver.insert(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, values);
//        Uri insertUri = resolver.insert(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, values);
//        Uri insertUri = resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, values);

        // ??????OutputStream
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
        File parentFile = file.getParentFile();
        if (!parentFile.exists()) {
            parentFile.mkdirs();
        }
        Sink sink = Okio.sink(file);
        BufferedSink buffer = Okio.buffer(sink);
        buffer.writeAll(response.body().source());
        buffer.flush();
        return file;
    }

}
