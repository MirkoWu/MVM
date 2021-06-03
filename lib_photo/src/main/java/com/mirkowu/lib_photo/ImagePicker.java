package com.mirkowu.lib_photo;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import androidx.fragment.app.Fragment;

import com.mirkowu.lib_photo.ui.ImagePickerActivity;
import com.mirkowu.lib_photo.ui.ImagePreviewActivity;
import com.mirkowu.lib_photo.utils.PermissionsUtils;
import com.mirkowu.lib_util.LogUtil;

import java.io.File;
import java.util.ArrayList;

import static android.app.Activity.RESULT_OK;

/**
 * 图片选择器
 */
public class ImagePicker {
    public static final String TAG = ImagePicker.class.getSimpleName();
    public static final String EXTRA_RESULT = PickerConstant.EXTRA_RESULT;

    private PickerConfig mPickerConfig;
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
     * 设置挑选配置 如果不设置则使用默认配置
     *
     * @param pickerConfig
     * @return
     */
    public ImagePicker setPickerConfig(PickerConfig pickerConfig) {
        mPickerConfig = pickerConfig;
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
    public boolean checkIllegalConfig() {
        if (mPickerConfig == null) {
            mPickerConfig = new PickerConfig();
        }
        if (mPickerConfig.getMaxPickCount() < 1) {
            LogUtil.e(TAG, "MaxPickCount must be greater than 0 ！");
            return true;
        }
        if (mPickerConfig.getSpanCount() < 1) {
            LogUtil.e(TAG, "MaxPickCount must be greater than 0 ！");
            return true;
        }
        return false;
    }


    /**
     * 跳转到挑选界面
     *
     * @param context
     */
    public void start(final Context context) {
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
        ImagePreviewActivity.startFromPreview(context, savePath, originData, currentPos);
    }

    public static void previewImage(Context context, ArrayList<String> originData, int currentPos) {
        previewImageWithSave(context, null, originData, currentPos);
    }

    /**
     * 直接前往拍照
     *
     * @param activity
     */
    public void showCameraAction(Activity activity) {
        PermissionsUtils.showCameraAction(activity);
    }

    public void showCameraAction(Fragment fragment) {
        PermissionsUtils.showCameraAction(fragment);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data, OnPickResultListener onPickResultListener) {
        if (onPickResultListener != null) {
            if (requestCode == PermissionsUtils.REQUEST_ALBUM && resultCode == RESULT_OK) {
                ArrayList<String> selectPath = data.getStringArrayListExtra(ImagePicker.EXTRA_RESULT);
                onPickResultListener.onPickResult(selectPath);
            } else if (requestCode == PermissionsUtils.REQUEST_CAMERA) {
                File file = PermissionsUtils.onActivityResult(requestCode, resultCode, data);
                if (resultCode == RESULT_OK && file != null) {
                    ArrayList<String> selectPath = new ArrayList<>();
                    selectPath.add(file.getAbsolutePath());
                    onPickResultListener.onPickResult(selectPath);
                }
            }
        }
    }

    public interface OnPickResultListener {
        void onPickResult(ArrayList<String> imageList);
    }


}
