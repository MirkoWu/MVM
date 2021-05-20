package com.mirkowu.lib_image;

import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.BaseRequestOptions;
import com.bumptech.glide.request.RequestListener;

import java.io.File;

public class GlideLoader implements ILoader {
    @Override
    public void load(@NonNull ImageView view, @Nullable String url) {
        Glide.with(view.getContext()).load(url).into(view);
    }

    @Override
    public void load(@NonNull ImageView view, @Nullable Integer resourceId) {
        Glide.with(view.getContext()).load(resourceId).into(view);
    }

    @Override
    public void load(@NonNull ImageView view, @Nullable File file) {
        Glide.with(view.getContext()).load(file).into(view);
    }

    @Override
    public void load(@NonNull ImageView view, @Nullable Uri uri) {
        Glide.with(view.getContext()).load(uri).into(view);
    }

    @Override
    public void load(@NonNull ImageView view, @Nullable Object model) {
        Glide.with(view.getContext()).load(model).into(view);
    }

    public void load(@NonNull ImageView view, @Nullable String url, BaseRequestOptions<?> options) {
        Glide.with(view.getContext()).load(url).apply(options).into(view);
    }

    public void load(@NonNull ImageView view, @Nullable String url, BaseRequestOptions<?> options, RequestListener<Drawable> listener) {
        Glide.with(view.getContext()).load(url).apply(options).listener(listener).into(view);
    }
}
