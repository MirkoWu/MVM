package com.mirkowu.lib_photo;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.mirkowu.lib_photo.engine.Glide4Loader;
import com.mirkowu.lib_photo.engine.ImageEngine;
import com.mirkowu.lib_photo.ui.ImagePickerActivity;
import com.mirkowu.lib_photo.ui.ImagePreviewActivity;
import com.mirkowu.lib_photo.utils.PermissionsUtils;
import com.mirkowu.lib_util.PermissionsUtil;
import com.mirkowu.lib_util.utilcode.util.PermissionUtils;

import java.io.File;
import java.util.ArrayList;

import static android.app.Activity.RESULT_OK;

/**
 * 图片选择器
 */
public class ImagePicker {

    public static final String EXTRA_RESULT = PickerConfig.EXTRA_RESULT;

    private boolean mShowCamera = true;
    private boolean isSupportGif = true;
    private int mMaxCount = 9;
    private int mMode = PickerConfig.MODE_MULTI;
    private ArrayList<String> mOriginData;
    private ImageEngine mImageEngine;
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


//    public static ImagePicker create() {
//        if (sSelector == null) {
//            sSelector = new ImagePicker();
//        }
//        return sSelector;
//    }

    public ImagePicker showCamera(boolean show) {
        mShowCamera = show;
        return sSelector;
    }

    public ImagePicker setImageEngine(ImageEngine imageLoader) {
        mImageEngine = imageLoader;
        return sSelector;
    }

    public ImageEngine getImageEngine() {
        if (mImageEngine == null) {
            mImageEngine = new Glide4Loader();
        }
        return mImageEngine;
    }

    public ImagePicker maxCount(int count) {
        mMode = PickerConfig.MODE_MULTI;
        mMaxCount = count;
        return sSelector;
    }

    public ImagePicker single() {
        mMode = PickerConfig.MODE_SINGLE;
        mMaxCount = 1;
        return sSelector;
    }
//
//    public ImagePicker multi() {
//        mMode = ImagePickerActivity.MODE_MULTI;
//        return sSelector;
//    }

    public ImagePicker origin(ArrayList<String> images) {
        mOriginData = images;
        return sSelector;
    }

    public ImagePicker supportGif(boolean isSupportGif) {
        this.isSupportGif = isSupportGif;
        return sSelector;
    }

    public Boolean isSupportGif() {
        return isSupportGif;
    }

    public ImagePicker setOnPickResultListener(OnPickResultListener pickResultListener) {
        mOnPickResultListener = pickResultListener;
        return this;
    }

    public OnPickResultListener getOnPickResultListener() {
        return mOnPickResultListener;

    }

    public void start(final Activity activity) {
        if (mMaxCount <= 0) {
            Toast.makeText(activity, R.string.max_count_more_zero, Toast.LENGTH_SHORT).show();
            return;
        }
        final Context context = activity;
        activity.startActivity(createIntent(context));
    }

    public void start(final Fragment fragment) {
        final Context context = fragment.getContext();
        final Activity activity = fragment.getActivity();
        if (mMaxCount <= 0) {
            Toast.makeText(activity, R.string.max_count_more_zero, Toast.LENGTH_SHORT).show();
            return;
        }
        fragment.startActivity(createIntent(context));
    }


    private Intent createIntent(Context context) {

        Bundle bundle = new Bundle();
        bundle.putInt(PickerConfig.EXTRA_SELECT_MODE, mMode);
        bundle.putInt(PickerConfig.EXTRA_SELECT_COUNT, mMaxCount);
        bundle.putBoolean(PickerConfig.EXTRA_SHOW_CAMERA, mShowCamera);
        if (mOriginData != null) {
            bundle.putStringArrayList(PickerConfig.EXTRA_DEFAULT_SELECTED_LIST, mOriginData);
        }
        Intent intent = new Intent(context, ImagePickerActivity.class);
        intent.putExtras(bundle);
//        intent.putExtra(ImagePickerActivity.EXTRA_SHOW_CAMERA, mShowCamera);
//        intent.putExtra(ImagePickerActivity.EXTRA_SELECT_COUNT, mMaxCount);
//        if (mOriginData != null) {
//            intent.putStringArrayListExtra(ImagePickerActivity.EXTRA_DEFAULT_SELECTED_LIST, mOriginData);
//        }
//        intent.putExtra(ImagePickerActivity.EXTRA_SELECT_MODE, mMode);

        return intent;
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
