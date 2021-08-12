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

    public abstract String getBaseUrl();

    public Retrofit getRetrofit() {
        final String baseUrl = getBaseUrl();
        Preconditions.checkArgument(!TextUtils.isEmpty(baseUrl), "baseUrl can not be null or empty !");

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
