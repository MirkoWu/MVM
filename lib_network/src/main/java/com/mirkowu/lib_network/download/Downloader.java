package com.mirkowu.lib_network.download;

import android.text.TextUtils;
import android.util.ArrayMap;

import com.mirkowu.lib_util.LogUtil;

import java.io.File;
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

    public Downloader setIsDebug(boolean isDebug) {
        this.isDebug = isDebug;
        return this;
    }

    public Downloader setUrl(String url) {
        this.url = url;
        return this;
    }

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
            public void subscribe(@androidx.annotation.NonNull ObservableEmitter<File> emitter) throws Throwable {
                try {
                    Response response = call.execute();
                    if (response.isSuccessful()) {
                        File file = saveFile(response, filePath);
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
                LogUtil.e("下载成功" + file.getAbsolutePath());
                if (downloadListener != null) {
                    downloadListener.onSuccess(file);
                }
            }

            @Override
            public void onError(@NonNull Throwable e) {
                e.printStackTrace();
                LogUtil.e("下载失败" + e.getMessage());
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

    private File saveFile(Response response, String path) throws Throwable {
        File file = new File(path);
        Sink sink = Okio.sink(file);
        BufferedSink buffer = Okio.buffer(sink);
        buffer.writeAll(response.body().source());
        buffer.flush();
        return file;
    }

}
