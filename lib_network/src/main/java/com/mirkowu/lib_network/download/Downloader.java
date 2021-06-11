//package com.mirkowu.lib_network.download;
//
//import android.text.TextUtils;
//import android.util.ArrayMap;
//
//import com.mirkowu.lib_util.LogUtil;
//
//import java.io.File;
//import java.util.Map;
//
//import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
//import io.reactivex.rxjava3.annotations.NonNull;
//import io.reactivex.rxjava3.core.Observable;
//import io.reactivex.rxjava3.core.ObservableEmitter;
//import io.reactivex.rxjava3.core.ObservableOnSubscribe;
//import io.reactivex.rxjava3.observers.DisposableObserver;
//import io.reactivex.rxjava3.schedulers.Schedulers;
//import okhttp3.Call;
//import okhttp3.Request;
//import okhttp3.Response;
//
//public class Downloader {
//    public static Downloader create(){
//        return new Downloader();
//    }
//    private static Map<Long, Call> sCallMap = new ArrayMap();
//
//    private boolean isDebug;
//    private String url;
//    private String filePath;
//    private Map<String, String> header;
//    private OnDownloadListener downloadListener;
//
//    public DownloadClient setOnProgressListener(OnDownloadListener downloadListener) {
//        this.downloadListener = downloadListener;
//        return this;
//    }
//
//    public DownloadClient isUseSSLVerifier(boolean isUseSSLVerifier) {
//        this.isUseSSLVerifier = isUseSSLVerifier;
//        return this;
//    }
//
//    public DownloadClient setIsDebug(boolean isDebug) {
//        this.isDebug = isDebug;
//        return this;
//    }
//
//    public DownloadClient setUrl(String url) {
//        this.url = url;
//        return this;
//    }
//
//    public DownloadClient setFilePath(@NonNull String path) {
//        this.filePath = path;
//        return this;
//    }
//
//
//    public DownloadClient setHeader(Map<String, String> header) {
//        this.header = header;
//        return this;
//    }
//
//    private void clear(Call call) {
//        if (sCallMap.containsValue(call)) {
//            sCallMap.remove(call);
//        }
//    }
//
//    public void cancel(long id) {
//        if (sCallMap.containsKey(id)) {
//            Call call = sCallMap.get(id);
//            if (call != null && !call.isCanceled() && call.isExecuted()) {
//                call.cancel();
//            }
//        }
//    }
//    public /*Observable<Response>*/ long start() {
//        if (TextUtils.isEmpty(filePath)) {
//            if (downloadListener != null) {
//                downloadListener.onFailure(new IllegalArgumentException("filePath 不能为空！"));
//            }
//            return -1;
//        }
//        if (TextUtils.isEmpty(url)) {
//            if (downloadListener != null) {
//                downloadListener.onFailure(new IllegalArgumentException("url 不能为空！"));
//            }
//            return -1;
//        }
//        Request.Builder builder = new Request.Builder();
//        builder.url(url).get();
//        if (header != null && !header.isEmpty()) {
//            for (Map.Entry<String, String> entry : header.entrySet()) {
//                builder.header(entry.getKey(), entry.getValue());
//            }
//        }
//
//
//
//        long id=System.currentTimeMillis();
//        sCallMap.put(id, call);
//        Observable.create(new ObservableOnSubscribe<File>() {
//            @Override
//            public void subscribe(@NonNull ObservableEmitter<File> emitter) throws Throwable {
//                try {
//                    Call call = DownloadClient.getInstance().execute(builder.build());
//                } catch (Throwable e) {
//                    emitter.onError(e);
//                }
//            }
//        }).subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(new DisposableObserver<File>() {
//
//                    @Override
//                    public void onNext(@NonNull File file) {
//                        clear(call);
//                        LogUtil.e("下载成功" + file.getAbsolutePath());
//                        if (downloadListener != null) {
//                            downloadListener.onSuccess(file);
//                        }
//                    }
//
//                    @Override
//                    public void onError(@NonNull Throwable e) {
//                        clear(call);
//                        e.printStackTrace();
//                        LogUtil.e("下载失败" + e.getMessage());
//                        if (downloadListener != null) {
//                            downloadListener.onFailure(e);
//                        }
//                    }
//
//                    @Override
//                    public void onComplete() {
//
//                    }
//                });
//        return id;
//
//    }
//}
