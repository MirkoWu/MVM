package com.mirkowu.lib_network;

import com.mirkowu.lib_util.LogUtil;

import java.lang.reflect.Field;
import java.net.Proxy;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;
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

/**
 * 如果有多个Client 或者 Host 实现多个子类即可
 * getOkHttpClient()后，再修改参数配置将无效
 */
public abstract class AbsOkHttpClient {
    private static final long TIME_OUT = 10L; //默认超时时10s
    private boolean mIsDebug = false;
    private boolean mIsIgnoreSSLVerifier = false;
    private List<Interceptor> mInterceptorList;

    public boolean isDebug() {
        return mIsDebug;
    }

    public AbsOkHttpClient setDebug(boolean isDebug) {
        this.mIsDebug = isDebug;
        return this;
    }

    public List<Interceptor> getInterceptorList() {
        return mInterceptorList;
    }

    public AbsOkHttpClient setInterceptorList(List<Interceptor> interceptorList) {
        this.mInterceptorList = interceptorList;
        return this;
    }

    public AbsOkHttpClient addInterceptor(Interceptor interceptor) {
        if (mInterceptorList == null) {
            mInterceptorList = new ArrayList<>();
        }
        mInterceptorList.add(interceptor);
        return this;
    }

    /**
     * 是否忽略SSL证书
     *
     * @return
     */
    public AbsOkHttpClient setIgnoreSSLVerifier(boolean ignoreSSLVerifier) {
        mIsIgnoreSSLVerifier = ignoreSSLVerifier;
        return this;
    }

    public boolean isIgnoreSSLVerifier() {
        return mIsIgnoreSSLVerifier;
    }

    /**
     * 设置超时时间
     */
    protected long getConnectTimeout() {
        return TIME_OUT;
    }

    protected long getWriteTimeout() {
        return TIME_OUT;
    }

    protected long getReadTimeout() {
        return TIME_OUT;
    }


    public OkHttpClient getOkHttpClient() {
        OkHttpClient.Builder builder = new OkHttpClient.Builder()
                //设置超时
                .connectTimeout(getConnectTimeout(), TimeUnit.SECONDS)
                .writeTimeout(getWriteTimeout(), TimeUnit.SECONDS)
                .readTimeout(getReadTimeout(), TimeUnit.SECONDS)
                //错误重连
                .retryOnConnectionFailure(false);


        //添加拦截器，eg.数据处理
        List<Interceptor> interceptors = getInterceptorList();
        if (interceptors != null && !interceptors.isEmpty()) {
            for (Interceptor interceptor : interceptors) {
                if (interceptor != null) {
                    builder.addInterceptor(interceptor);
                }
            }
        }

        if (isDebug()) {
            builder.addInterceptor(sLoggingInterceptor);
        } else {
            //禁用代理,防抓包
            builder.proxy(Proxy.NO_PROXY);
        }
        OkHttpClient client = builder.build();

        //解决https认证
        if (isIgnoreSSLVerifier()) {
            ignoreSSLVerifier(client);
        }

        return client;
    }

    protected static HttpLoggingInterceptor sLoggingInterceptor =
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

    /**
     * 解决Https证书认证问题
     *
     * @param okHttpClient
     */
    protected void ignoreSSLVerifier(OkHttpClient okHttpClient) {
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
