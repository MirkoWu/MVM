package com.mirkowu.lib_image;

import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.widget.ImageView;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RawRes;

import com.bumptech.glide.request.BaseRequestOptions;
import com.bumptech.glide.request.RequestListener;
import com.mirkowu.lib_util.LogUtil;

import java.io.File;

public class ImageLoader {

    private volatile static ILoader sILoader;

    public static void setLoader(ILoader loader) {
        sILoader = loader;
    }

    public static ILoader getLoader() {
        if (sILoader == null) {
            synchronized (ImageLoader.class) {
                if (sILoader == null) {
                    sILoader = new GlideLoader();
                }
            }
        }
        return sILoader;
    }

    public static void load(@NonNull ImageView view, @Nullable String url) {
        getLoader().load(view, url);
    }


    public static void load(@NonNull ImageView view, @RawRes @DrawableRes @Nullable Integer resourceId) {
        getLoader().load(view, resourceId);
    }

    public static void load(@NonNull ImageView view, @Nullable File file) {
        getLoader().load(view, file);
    }

    public static void load(@NonNull ImageView view, @Nullable Uri uri) {
        getLoader().load(view, uri);
    }

    public static void load(@NonNull ImageView view, @Nullable Object model) {
        getLoader().load(view, model);
    }

    public static void load(@NonNull ImageView view, @Nullable String url, BaseRequestOptions<?> options) {
        if (getLoader() != null && getLoader() instanceof GlideLoader) {
            ((GlideLoader) getLoader()).load(view, url, options);
        } else {
            LogUtil.e(String.format("Loader：%s 不支持当前方法", getLoader()));
        }
    }

    public static void load(@NonNull ImageView view, @Nullable String url, BaseRequestOptions<?> options, RequestListener<Drawable> listener) {
        if (getLoader() != null && getLoader() instanceof GlideLoader) {
            ((GlideLoader) getLoader()).load(view, url, options, listener);
        } else {
            LogUtil.e(String.format("Loader：%s 不支持当前方法", getLoader()));
        }
    }
}
