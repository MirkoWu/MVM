package com.mirkowu.lib_util.pattern;

import android.text.TextUtils;
import android.util.ArrayMap;

import androidx.annotation.NonNull;

/**
 * 链式调用
 * @param <T>
 */
public class InterceptChainManager<T> {
    private ArrayMap<String, InterceptChain<T>> mMap = new ArrayMap<>();
    private int mCurIndex = 0;

    /**
     * 添加链路，默认key是类名
     * 每一种InterceptChain只能添加一个，后添加的会覆盖前面添加的
     * 如果需要支持多个，可以使用自定义Key添加
     *
     * @param chain
     */
    public void add(InterceptChain<T> chain) {
        if (chain != null) {
            String key = chain.getClass().getName();
            add(key, chain);
        }
    }

    /**
     * 添加链路，可以自定义Key
     * 每一种Key只能添加一个，后添加的会覆盖前面添加的
     *
     * @param key   自定义Key
     * @param chain
     */
    public void add(String key, InterceptChain<T> chain) {
        if (!mMap.containsKey(key)) {
            mMap.put(key, chain);
        }
    }

    /**
     * 启动，默认按添加顺序第一个开始
     *
     * @param data
     */
    public void start(T data) {
        next(data);
    }

    /**
     * 从指定的链路启动
     *
     * @param cls  默认类名
     * @param data
     */
    public void start(@NonNull Class<? extends InterceptChain<T>> cls, T data) {
        next(cls.getName(), data);
    }

    /**
     * 从指定的链路启动
     *
     * @param key  自定义key
     * @param data
     */
    public void start(String key, T data) {
        next(key, data);
    }

    /**
     * 下一个
     *
     * @param data
     */
    public void next(T data) {
        next("", data);
    }

    /**
     * 从指定的链路启动
     *
     * @param cls
     * @param data
     */
    public void next(@NonNull Class<? extends InterceptChain<T>> cls, T data) {
        next(cls.getName(), data);
    }


    public synchronized void next(String key, T data) {
        if (!TextUtils.isEmpty(key) && mMap.containsKey(key)) {
            mCurIndex = mMap.indexOfKey(key);
        }

        if (mCurIndex >= 0 && mCurIndex < mMap.size()) {
            InterceptChain<T> chain = mMap.valueAt(mCurIndex);
            if (chain != null) {
                chain.intercept(this, data);
            }
            mCurIndex++;
        }
    }

    public void reset() {
        mCurIndex = 0;
    }

    public void clear() {
        mMap.clear();
        mCurIndex = 0;
    }


}
