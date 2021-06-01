package com.mirkowu.lib_photo;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
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

    public static final String EXTRA_RESULT = ImagePickerActivity.EXTRA_RESULT;

    private boolean mShowCamera = true;
    private boolean isSupportGif = true;
    private int mMaxCount = 9;
    private int mMode = ImagePickerActivity.MODE_MULTI;
    private ArrayList<String> mOriginData;
    private ImageEngine mImageEngine;
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
        mMode = ImagePickerActivity.MODE_MULTI;
        mMaxCount = count;
        return sSelector;
    }

    public ImagePicker single() {
        mMode = ImagePickerActivity.MODE_SINGLE;
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


    public void start(final Activity activity) {
        if (mMaxCount <= 0) {
            Toast.makeText(activity, R.string.max_count_more_zero, Toast.LENGTH_SHORT).show();
            return;
        }
        final Context context = activity;

        PermissionsUtil.getInstance().requestPermissions(activity, PermissionsUtil.PERMISSION_STORAGE, new PermissionsUtil.OnPermissionsListener() {
            @Override
            public void onPermissionGranted(int requestCode) {

            }

            @Override
            public void onPermissionShowRationale(int requestCode, String[] permissions) {

            }

            @Override
            public void onPermissionDenied(int requestCode) {

            }
        });

        PermissionsUtils.requestReadStorage(activity).subscribe(new Consumer<Boolean>() {
            @Override
            public void accept(@NonNull Boolean aBoolean) throws Exception {
                if (aBoolean) {
                    activity.startActivityForResult(createIntent(context), PermissionsUtils.REQUEST_ALBUM);
                } else {
                    PermissionsUtils.shouldShowRequestPermissionRationaleReadStorage(activity)
                            .subscribe(new Consumer<Boolean>() {
                                @Override
                                public void accept(@NonNull Boolean aBoolean) throws Exception {
                                    if (aBoolean) {// 要再来申请下  不能直接再次申请 要弹窗提醒 否则被拒绝的话会形成死循环
                                        new AlertDialog.Builder(activity)
                                                .setTitle(R.string.ivp_permission_dialog_title)
                                                .setMessage(R.string.ivp_permission_rationale)
                                                .setPositiveButton(R.string.ivp_permission_dialog_ok, new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        start(activity);//如果想继续同意权限 就重新调用改方法
                                                    }
                                                })
                                                .setNegativeButton(R.string.ivp_permission_dialog_cancel, null)
                                                .create().show();
                                    } else {//被拒绝 且选中了 不再提醒的  要提醒用户自己去设置了
                                        new AlertDialog.Builder(activity)
                                                .setTitle(R.string.ivp_error_no_permission)
                                                .setMessage(R.string.ivp_permission_lack)
                                                .setPositiveButton(R.string.ivp_permission_dialog_ok, new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                                        intent.setData(Uri.parse("package:" + activity.getPackageName()));
                                                        activity.startActivity(intent);
                                                    }
                                                })
                                                .setNegativeButton(R.string.ivp_permission_dialog_cancel, null)
                                                .create().show();
                                    }
                                }
                            });
                }
            }
        });
    }

    public void start(final Fragment fragment) {
        final Context context = fragment.getContext();
        final Activity activity = fragment.getActivity();
        if (mMaxCount <= 0) {
            Toast.makeText(activity, R.string.max_count_more_zero, Toast.LENGTH_SHORT).show();
            return;
        }
        PermissionsUtils.requestReadStorage(activity).subscribe(new Consumer<Boolean>() {
            @Override
            public void accept(@NonNull Boolean aBoolean) throws Exception {
                if (aBoolean) {
                    fragment.startActivityForResult(createIntent(context), PermissionsUtils.REQUEST_ALBUM);
                } else {
                    PermissionsUtils.shouldShowRequestPermissionRationaleReadStorage(activity).subscribe(new Consumer<Boolean>() {
                        @Override
                        public void accept(@NonNull Boolean aBoolean) throws Exception {
                            if (aBoolean) {// 要再来申请下  不能直接再次申请 要弹窗提醒 否则被拒绝的话会形成死循环
                                new AlertDialog.Builder(activity)
                                        .setTitle(R.string.ivp_permission_dialog_title)
                                        .setMessage(R.string.ivp_permission_rationale)
                                        .setPositiveButton(R.string.ivp_permission_dialog_ok, new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                start(fragment);//如果想继续同意权限 就重新调用改方法
                                            }
                                        })
                                        .setNegativeButton(R.string.ivp_permission_dialog_cancel, null)
                                        .create().show();
                            } else {//被拒绝 且选中了 不再提醒的  要提醒用户自己去设置了
                                new AlertDialog.Builder(activity)
                                        .setTitle(R.string.ivp_error_no_permission)
                                        .setMessage(R.string.ivp_permission_lack)
                                        .setPositiveButton(R.string.ivp_permission_dialog_ok, new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                                intent.setData(Uri.parse("package:" + activity.getPackageName()));
                                                fragment.startActivity(intent);
                                            }
                                        })
                                        .setNegativeButton(R.string.ivp_permission_dialog_cancel, null)
                                        .create().show();
                            }
                        }
                    });
                }
            }
        });
    }


    private Intent createIntent(Context context) {


        Intent intent = new Intent(context, ImagePickerActivity.class);
        intent.putExtra(ImagePickerActivity.EXTRA_SHOW_CAMERA, mShowCamera);
        intent.putExtra(ImagePickerActivity.EXTRA_SELECT_COUNT, mMaxCount);
        if (mOriginData != null) {
            intent.putStringArrayListExtra(ImagePickerActivity.EXTRA_DEFAULT_SELECTED_LIST, mOriginData);
        }
        intent.putExtra(ImagePickerActivity.EXTRA_SELECT_MODE, mMode);
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
