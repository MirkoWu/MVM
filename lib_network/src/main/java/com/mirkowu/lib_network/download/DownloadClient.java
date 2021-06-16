package com.mirkowu.lib_network.download;

import androidx.annotation.NonNull;

import java.lang.reflect.Field;
import java.net.Proxy;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.KeyManager;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import okhttp3.Call;
import okhttp3.OkHttpClient;

public class DownloadClient {
    private static class Singleton {
        private static final DownloadClient INSTANCE = new DownloadClient();
    }

    public static DownloadClient getInstance() {
        return Singleton.INSTANCE;
    }

    private DownloadClient() {
    }

//    private boolean isUseSSLVerifier;
//
//    public DownloadClient isUseSSLVerifier(boolean isUseSSLVerifier) {
//        this.isUseSSLVerifier = isUseSSLVerifier;
//        return this;
//    }

    public Call createCall(@NonNull RequestConfig config) {
        return getOkHttpClient(config.getDownloadListener()).newCall(config.getRequest());
    }

    public OkHttpClient getOkHttpClient(OnDownloadListener downloadListener) {
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

//        //解决https认证
//        if (isUseSSLVerifier) {
//            handleSSLVerifier(client);
//        }

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
