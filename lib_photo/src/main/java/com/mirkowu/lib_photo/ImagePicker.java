package com.mirkowu.lib_photo;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.mirkowu.lib_photo.callback.OnPickResultListener;
import com.mirkowu.lib_photo.engine.GlideLoader;
import com.mirkowu.lib_photo.engine.IImageEngine;
import com.mirkowu.lib_photo.mediaLoader.MediaModel;
import com.mirkowu.lib_photo.mediaLoader.ResultModel;
import com.mirkowu.lib_photo.ui.ImagePickerActivity;
import com.mirkowu.lib_photo.ui.PreviewActivity;
import com.mirkowu.lib_photo.utils.CameraUtil;
import com.mirkowu.lib_util.LogUtil;

import java.util.ArrayList;

/**
 * 图片选择器
 */
public class ImagePicker {
    private static final String TAG = ImagePicker.class.getSimpleName();

    private IImageEngine mIImageEngine;
    private PickerConfig mPickerConfig;
    private PickerConfig mPickerConfigCache;
    private OnPickResultListener mOnPickResultListener;
    private static volatile ImagePicker sSelector;

    public static ImagePicker getInstance() {
        if (sSelector == null) {
            synchronized (ImagePicker.class) {
                if (sSelector == null) {
                    sSelector = new ImagePicker();
                }
            }
        }
        return sSelector;
    }

    private ImagePicker() {
    }


    /**
     * 设置加载引擎
     *
     * @param iLoader
     * @return
     */
    public ImagePicker setImageEngine(@NonNull IImageEngine iLoader) {
        mIImageEngine = iLoader;
        return this;
    }

    /**
     * 获取加载引擎 默认Glide
     *
     * @return
     */
    public IImageEngine getImageEngine() {
        if (mIImageEngine == null) {
            mIImageEngine = new GlideLoader();
        }
        return mIImageEngine;
    }

    /**
     * 设置挑选配置 如果不设置则使用默认配置
     *
     * @param pickerConfig
     * @return
     */
    public ImagePicker setPickerConfig(PickerConfig pickerConfig) {
        mPickerConfigCache = pickerConfig;
        return this;
    }

    public PickerConfig getPickerConfig() {
        return mPickerConfig;
    }

    public ImagePicker setOnPickResultListener(OnPickResultListener pickResultListener) {
        mOnPickResultListener = pickResultListener;
        return this;
    }

    public OnPickResultListener getOnPickResultListener() {
        return mOnPickResultListener;

    }

    /**
     * 检查配置是否非法
     */
    private boolean checkIllegalConfig() {
        mPickerConfig = mPickerConfigCache;
        mPickerConfigCache = null;

        if (mPickerConfig == null) {
            setPickerConfig(new PickerConfig());
        }
        if (mPickerConfig.getMaxPickCount() < 1) {
            LogUtil.e(TAG, "MaxPickCount must be greater than 0 ！");
            return true;
        }
        if (mPickerConfig.getOriginSelectList() != null
                && mPickerConfig.getMaxPickCount() < mPickerConfig.getOriginSelectList().size()) {
            LogUtil.e(TAG, "OriginSelectList must be less than MaxPickCount ！");
            return true;
        }
        if (mPickerConfig.getSpanCount() < 1) {
            LogUtil.e(TAG, "SpanCount must be greater than 0 ！");
            return true;
        }
        return false;
    }

    /**
     * 清除配置缓存
     */
    public void clear() {
        MediaModel.clear();
        ResultModel.clear();
        mPickerConfig = null;
    }

    /**
     * 跳转到挑选界面
     *
     * @param context
     */
    public void start(@NonNull final Context context) {
        clear();
        if (checkIllegalConfig()) {
            return;
        }

        Intent intent = new Intent(context, ImagePickerActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }


    /**
     * 带保存按钮的 预览界面
     *
     * @param context
     * @param savePath   图片保存路径
     * @param originData 需要传进去的数据
     * @param currentPos 当前点击的数据下标
     */
    public static void previewImageWithSave(Context context, String savePath, ArrayList<String> originData, int currentPos) {
        PreviewActivity.start(context, savePath, ResultModel.pathsToBeans(originData), currentPos);
    }

    public static void previewImage(Context context, ArrayList<String> originData, int currentPos) {
        previewImageWithSave(context, null, originData, currentPos);
    }

    /**
     * 直接前往拍照
     *
     * @param activity
     */
    public void startCamera(Activity activity) {
        CameraUtil.startCameraAction(activity);
    }

    public void startCamera(Fragment fragment) {
        CameraUtil.startCameraAction(fragment);
    }
}
