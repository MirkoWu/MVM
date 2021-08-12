package com.mirkowu.lib_network;

import android.text.TextUtils;
import android.util.ArrayMap;

import com.mirkowu.lib_util.Preconditions;

import java.util.Map;

import retrofit2.Retrofit;
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * 如果有多个Client 或者 Host 实现多个子类即可
 * 注意：这些配置只在第一次创建Retrofit时生效，如果想修改配置，需要先清除缓存{@link #clearAllClient()}
 */
public abstract class AbsRetrofitClient extends AbsOkHttpClient {
    protected static Map<String, Retrofit> sRetrofitMap = new ArrayMap();

    private boolean isCacheClient = true;

    public boolean isCacheClient() {
        return isCacheClient;
    }

    public AbsRetrofitClient setCacheClient(boolean cacheClient) {
        isCacheClient = cacheClient;
        return this;
    }

    public abstract String getBaseUrl();


    public Retrofit getRetrofit() {
        final String baseUrl = getBaseUrl();
        Preconditions.checkArgument(!TextUtils.isEmpty(baseUrl),
                "baseUrl can not be null or empty !");
        if (isCacheClient()) {
            String key = String.format("%s@%s", getClass().getSimpleName(), baseUrl);
            if (sRetrofitMap.containsKey(key)) {
                return sRetrofitMap.get(key);
            } else {
                Retrofit retrofit = newRetrofit(baseUrl);
                sRetrofitMap.put(key, retrofit);
                return retrofit;
            }
        } else {
            return newRetrofit(baseUrl);
        }
    }

    protected Retrofit newRetrofit(String baseUrl) {
        return new Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(getOkHttpClient())
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
                .build();
    }

    /**
     * 获取Retrofit服务
     */
    public <T> T getService(Class<T> service) {
        return getRetrofit().create(service);
    }

    /**
     * 清除缓存
     */
    public void clearAllClient() {
        sRetrofitMap.clear();
    }

}
