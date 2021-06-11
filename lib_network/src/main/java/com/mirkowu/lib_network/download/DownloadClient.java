package com.mirkowu.lib_network.download;

import android.text.TextUtils;
import android.util.ArrayMap;

import androidx.annotation.NonNull;

import com.mirkowu.lib_util.LogUtil;

import java.io.File;
import java.lang.reflect.Field;
import java.net.Proxy;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.KeyManager;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.ObservableEmitter;
import io.reactivex.rxjava3.core.ObservableOnSubscribe;
import io.reactivex.rxjava3.observers.DisposableObserver;
import io.reactivex.rxjava3.schedulers.Schedulers;
import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okio.BufferedSink;
import okio.Okio;
import okio.Sink;

public class DownloadClient {
    private static class Singleton {
        private static final DownloadClient INSTANCE = new DownloadClient();
    }

    public static DownloadClient getInstance() {
        return Singleton.INSTANCE;
    }

    private DownloadClient() {
    }

    private static Map<Long, Call> sCallMap = new ArrayMap();

    private boolean isUseSSLVerifier;
    private boolean isDebug;
    private String url;
    private String filePath;
    private Map<String, String> header;
    private OnDownloadListener downloadListener;

    public DownloadClient setOnProgressListener(OnDownloadListener downloadListener) {
        this.downloadListener = downloadListener;
        return this;
    }

    public DownloadClient isUseSSLVerifier(boolean isUseSSLVerifier) {
        this.isUseSSLVerifier = isUseSSLVerifier;
        return this;
    }

    public DownloadClient setIsDebug(boolean isDebug) {
        this.isDebug = isDebug;
        return this;
    }

    public DownloadClient setUrl(String url) {
        this.url = url;
        return this;
    }

    public DownloadClient setFilePath(@NonNull String path) {
        this.filePath = path;
        return this;
    }


    public DownloadClient setHeader(Map<String, String> header) {
        this.header = header;
        return this;
    }

    private void clear(Call call) {
        if (sCallMap.containsValue(call)) {
            sCallMap.remove(call);
        }
    }

    public void cancel(long id) {
        if (sCallMap.containsKey(id)) {
            Call call = sCallMap.get(id);
            if (call != null && !call.isCanceled() && call.isExecuted()) {
                call.cancel();
            }
        }
    }

    public /*Observable<Response>*/ long start() {
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


        Call call = getOkHttpClient().newCall(builder.build());
        long id = System.currentTimeMillis();
        sCallMap.put(id, call);
        Observable.create(new ObservableOnSubscribe<File>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<File> emitter) throws Throwable {
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
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DisposableObserver<File>() {

                    @Override
                    public void onNext(@NonNull File file) {
                        clear(call);
                        LogUtil.e("下载成功" + file.getAbsolutePath());
                        if (downloadListener != null) {
                            downloadListener.onSuccess(file);
                        }
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        clear(call);
                        e.printStackTrace();
                        LogUtil.e("下载失败" + e.getMessage());
                        if (downloadListener != null) {
                            downloadListener.onFailure(e);
                        }
                    }

                    @Override
                    public void onComplete() {

                    }
                });
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

//    /**
//     * 清除缓存
//     */
//    public void clearAllClient() {
//        sRetrofitMap.clear();
//    }
//
//    public <T> T getService(Class<T> service) {
//        return getRetrofit().create(service);
//    }
//
//    public Retrofit getRetrofit() {
//        final String baseUrl = getHost();
//        if (sRetrofitMap.containsKey(baseUrl)) {
//            return sRetrofitMap.get(baseUrl);
//        } else {
//            Retrofit retrofit = new Retrofit.Builder()
//                    .baseUrl(baseUrl)
//                    .client(getOkHttpClient())
//                    .addConverterFactory(GsonConverterFactory.create())
//                    .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
//                    .build();
//            sRetrofitMap.put(baseUrl, retrofit);
//            return retrofit;
//        }
//    }


    public OkHttpClient getOkHttpClient() {
        OkHttpClient.Builder builder = new OkHttpClient.Builder()
                //设置超时
                .connectTimeout(20L, TimeUnit.SECONDS)
                .writeTimeout(20L, TimeUnit.SECONDS)
                .readTimeout(20L, TimeUnit.SECONDS)
                //错误重连
                .retryOnConnectionFailure(true);


        //带进度的拦截器
        if (downloadListener != null) {
            builder.addInterceptor(new DownloadInterceptor(downloadListener));
        }


        //禁用代理,防抓包
        builder.proxy(Proxy.NO_PROXY);

        OkHttpClient client = builder.build();

        //解决https认证
        if (isUseSSLVerifier) {
            handleSSLVerifier(client);
        }

        return client;
    }

    /**
     * 解决Https证书认证问题
     *
     * @param okHttpClient
     */
    private void handleSSLVerifier(OkHttpClient okHttpClient) {
        OkHttpClient sClient = okHttpClient;
        SSLContext sc = null;

        try {
            sc = SSLContext.getInstance("SSL");
            sc.init((KeyManager[]) null, new TrustManager[]{new X509TrustManager() {
                public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                }

                public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                }

                public X509Certificate[] getAcceptedIssuers() {
                    return null;
                }
            }}, new SecureRandom());
        } catch (Exception var10) {
        }

        HostnameVerifier hv1 = new HostnameVerifier() {
            public boolean verify(String hostname, SSLSession session) {
                return true;
            }
        };
        String workerClassName = "okhttp3.OkHttpClient";

        try {
            Class workerClass = Class.forName(workerClassName);
            Field hostnameVerifier = workerClass.getDeclaredField("hostnameVerifier");
            hostnameVerifier.setAccessible(true);
            hostnameVerifier.set(sClient, hv1);
            Field sslSocketFactory = workerClass.getDeclaredField("sslSocketFactory");
            sslSocketFactory.setAccessible(true);
            sslSocketFactory.set(sClient, sc.getSocketFactory());
        } catch (Exception var9) {
        }

    }

}
