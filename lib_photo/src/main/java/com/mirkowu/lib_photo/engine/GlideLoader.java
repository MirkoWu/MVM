package com.mirkowu.lib_photo.engine;

import android.content.Context;
import android.graphics.Bitmap;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.mirkowu.lib_photo.R;

import java.util.concurrent.ExecutionException;

/**
 *  默认Glide 图片加载
 */
public class GlideLoader implements ILoader {

    public void load(Context context, ImageView image, String url) {
        Glide.with(context).load(url)
                .apply(RequestOptions
                        .placeholderOf(R.drawable.ivp_default_error)
                        .error(R.drawable.ivp_default_error)
                        .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                )
                .into(image);
    }

    public void loadThumbnail(Context context, ImageView image, String url, int width) {
        Glide.with(context).load(url)
                .apply(RequestOptions
                        .placeholderOf(R.drawable.ivp_default_image)
                        .error(R.drawable.ivp_default_error)
                        .override(width, width)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                )
                .into(image);
    }

    @Override
    public void loadPicked(Context context, ImageView image, String url, int width, int height) {
        Glide.with(context).load(url)
                .apply(RequestOptions
                        .placeholderOf(R.drawable.ivp_default_image)
                        .error(R.drawable.ivp_default_error)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                )
                .into(image);
    }


    @Override
    public void pause(Context context) {
        // Glide.with(context).pauseRequests();
    }

    @Override
    public void resume(Context context) {
        // Glide.with(context).resumeRequests();
    }


    @Override
    public Bitmap loadAsBitmap(Context context, String url) throws ExecutionException, InterruptedException {
        return Glide.with(context)
                .asBitmap()
                .load(url)
                .submit(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL)
                .get();
    }


}

