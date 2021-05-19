package com.mirkowu.lib_network;

import android.util.ArrayMap;

import com.mirkowu.lib_util.LogUtil;

import java.lang.reflect.Field;
import java.net.Proxy;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.KeyManager;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public abstract class AbsRetrofitClient {
    private static Map<String, Retrofit> sRetrofitMap = new ArrayMap();

    protected abstract boolean isDebug();

    protected abstract String getHost();

    protected abstract List<Interceptor> getInterceptor();


    public Retrofit getRetrofit() {
        String baseUrl = getHost();
        if (sRetrofitMap.containsKey(baseUrl)) {
            return sRetrofitMap.get(baseUrl);
        } else {
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(baseUrl)
                    .client(getOkHttpClient())
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
                    .build();
            sRetrofitMap.put(baseUrl, retrofit);
            return retrofit;
        }
    }


    public OkHttpClient getOkHttpClient() {
        OkHttpClient.Builder builder = new OkHttpClient.Builder()
                //设置超时
                .connectTimeout(20L, TimeUnit.SECONDS)
                .writeTimeout(20L, TimeUnit.SECONDS)
                .readTimeout(20L, TimeUnit.SECONDS)
                //错误重连
                .retryOnConnectionFailure(true);


        //添加拦截器，eg.数据处理
        List<Interceptor> interceptors = getInterceptor();
        if (interceptors != null) {
            for (Interceptor interceptor : interceptors) {
                if (interceptor != null) {
                    builder.addInterceptor(interceptor);
                }
            }
        }

        if (isDebug()) {
            builder.addInterceptor(loggingInterceptor);
        } else {
            //禁用代理,防抓包
            builder.proxy(Proxy.NO_PROXY);
        }
        OkHttpClient client = builder.build();

        //解决https
        //  handleSSLVerifier(client);

        return client;
    }

    public static HttpLoggingInterceptor loggingInterceptor =
            new HttpLoggingInterceptor(message -> {
                //打印日志
                LogUtil.d("HttpLog: ", message);
            }).setLevel(HttpLoggingInterceptor.Level.BODY);

    /**
     * okhttp 头部不能包含空格和中文，这里做下转义
     */
    public static String formatName(String name) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < name.length(); i++) {
            char c = name.charAt(i);
            if (c <= '\u001f' || c >= '\u007f') {
                sb.append(String.format("\\u%04x", (int) c));
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }


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
