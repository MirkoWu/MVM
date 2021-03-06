package com.mirkowu.lib_photo.engine;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.widget.ImageView;

import java.util.concurrent.ExecutionException;

public interface IImageEngine {


    /**
     * 预览加载大图时用到的方法
     * 必须填写
     *
     * @param context
     * @param image
     * @param url
     */
    void load(Context context, ImageView image, String url);

    void load(Context context, ImageView image, Uri uri);

    /**
     * 加载 文件夹，图片列表等 用到的方法 可重写size
     * 必须填写
     *
     * @param context
     * @param image
     * @param url
     * @param width   size宽度
     */
    void loadThumbnail(Context context, ImageView image, String url, int width);

    void loadThumbnail(Context context, ImageView image, Uri uri, int width);


    /**
     * 加载Gif ，保持不播放
     *
     * @param context
     * @param image
     * @param uri
     * @param width
     */
    void loadGifAsBitmap(Context context, ImageView image, Uri uri, int width);

    /**
     * 加载Gif 自动播放
     *
     * @param context
     * @param image
     * @param uri
     * @param width
     */
    void loadGif(Context context, ImageView image, Uri uri, int width);

    /**
     * 加载 已经挑选的图片 ImagePickerRecyclerView内的显示 一般和 load 相似
     * 必须填写
     *
     * @param context
     * @param image
     * @param url
     * @param width
     * @param height
     */
    void loadPicked(Context context, ImageView image, String url, int width, int height);

    void loadPicked(Context context, ImageView image, Uri uri, int width, int height);


    /**
     * 停止加载
     * 可以为空
     *
     * @param context
     */
    void pause(Context context);

    /**
     * 开始加载
     * 可以为空
     *
     * @param context
     */
    void resume(Context context);

    /**
     * 下载网络图片时用到的方法 没有用到可以不用管
     * 可以为空
     *
     * @param context
     * @param url
     * @return
     * @throws ExecutionException
     * @throws InterruptedException
     */
    Bitmap loadAsBitmap(Context context, String url) throws ExecutionException, InterruptedException;


}
