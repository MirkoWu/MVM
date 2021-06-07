package com.mirkowu.lib_util;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

/**
 * @author by DELL
 * @date on 2019/10/19
 * @describe
 */
public class PermissionsUtil {

    private static class Singleton {
        private static final PermissionsUtil INSTANCE = new PermissionsUtil();
    }

    public static PermissionsUtil getInstance() {
        return PermissionsUtil.Singleton.INSTANCE;
    }

    private PermissionsUtil() {
    }

    /**
     * 摄像头，文件存储
     */
    public static String[] PERMISSION_CAMERA = new String[]{
            Manifest.permission.CAMERA,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE
    };
    /**
     * 文件存储
     */
    public static String[] PERMISSION_STORAGE = new String[]{
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE
    };
    /**
     * 手机信息
     */
    public static String[] PERMISSION_PHONE = new String[]{
            Manifest.permission.READ_PHONE_STATE
    };

    public interface OnPermissionsListener {
        void onPermissionGranted(int requestCode);

        void onPermissionShowRationale(int requestCode, String[] permissions);

        void onPermissionDenied(int requestCode);
    }

    private static int mRequestCode;
    private static String[] mRequestPermissions;
    private static OnPermissionsListener mOnPermissionsListener;
    public static final int REQUEST_CODE = 0x2488;
    private static final int REQUEST_CODE_DETAIL_SETTING = 1022;


    /**
     * 请求权限
     */
    public void requestPermissions(@NonNull Activity activity, @NonNull String[] permissions,
                                   @NonNull OnPermissionsListener listener) {
        requestPermissions(activity, permissions, REQUEST_CODE, listener);
    }

    public void requestPermissions(@NonNull Activity activity, @NonNull String[] permissions, int requestCode,
                                   @NonNull OnPermissionsListener listener) {
        mRequestPermissions = permissions;
        mOnPermissionsListener = listener;
        mRequestCode = requestCode;

        if (mRequestPermissions == null || mOnPermissionsListener == null) {
            throw new IllegalArgumentException("permissions or onPermissionsListener is null");
        }

        if (hasPermissions(activity, permissions)) {
            listener.onPermissionGranted(requestCode);
        } else {
            //没权限就去请求
            ActivityCompat.requestPermissions(activity, permissions, requestCode);
        }
    }

    /**
     * 请求权限
     */
    public void requestPermissions(@NonNull Fragment fragment, @NonNull String[] permissions,
                                   @NonNull OnPermissionsListener listener) {
        requestPermissions(fragment, permissions, REQUEST_CODE, listener);
    }

    public void requestPermissions(@NonNull Fragment fragment, @NonNull String[] permissions, int requestCode,
                                   @NonNull OnPermissionsListener listener) {
        mRequestPermissions = permissions;
        mOnPermissionsListener = listener;
        mRequestCode = requestCode;

        if (mRequestPermissions == null || mOnPermissionsListener == null) {
            throw new IllegalArgumentException("permissions or onPermissionsListener is null");
        }

        if (hasPermissions(fragment.getContext(), permissions)) {
            listener.onPermissionGranted(requestCode);
        } else {
            //没权限就去请求
            fragment.requestPermissions(permissions, requestCode);
        }
    }


    public static boolean hasPermissions(Context context, String[] permissions) {
        for (String perms : permissions) {
            if (ContextCompat.checkSelfPermission(context, perms)
                    != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    public static boolean shouldRationale(Activity activity, String[] permissions) {
        for (String perms : permissions) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(activity, perms)) {
                return true;
            }
        }
        return false;
    }

    public static boolean shouldRationale(Fragment fragment, String[] permissions) {
        for (String perms : permissions) {
            if (fragment.shouldShowRequestPermissionRationale(perms)) {
                return true;
            }
        }
        return false;
    }


    public void onRequestPermissionsResult(Activity activity, int requestCode, String[] permissions, int[] grantResults) {
        //有可能会返回空数组,做下判断
        if (requestCode == mRequestCode && grantResults != null && grantResults.length > 0) {
            boolean isGranted = true;
            for (int result : grantResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    isGranted = false;
                    break;
                }
            }

            if (mOnPermissionsListener != null) {
                if (isGranted) {
                    mOnPermissionsListener.onPermissionGranted(requestCode);
                } else if (shouldRationale(activity, mRequestPermissions)) {
                    mOnPermissionsListener.onPermissionShowRationale(requestCode, mRequestPermissions);
                } else {
                    mOnPermissionsListener.onPermissionDenied(requestCode);
                }
            }
        }
    }

    public void onRequestPermissionsResult(Fragment fragment, int requestCode, String[] permissions, int[] grantResults) {
        //有可能会返回空数组,做下判断
        if (requestCode == mRequestCode && grantResults != null && grantResults.length > 0) {
            boolean isGranted = true;
            for (int result : grantResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    isGranted = false;
                    break;
                }
            }

            if (mOnPermissionsListener != null) {
                if (isGranted) {
                    mOnPermissionsListener.onPermissionGranted(requestCode);
                } else if (shouldRationale(fragment, mRequestPermissions)) {
                    mOnPermissionsListener.onPermissionShowRationale(requestCode, mRequestPermissions);
                } else {
                    mOnPermissionsListener.onPermissionDenied(requestCode);
                }
            }
        }
    }


    public void onActivityResult(Activity activity, int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_DETAIL_SETTING) {
            if (mOnPermissionsListener != null) {
                requestPermissions(activity, mRequestPermissions, requestCode, mOnPermissionsListener);
            }
        }
    }

    public void onActivityResult(Fragment fragment, int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_DETAIL_SETTING) {
            if (mOnPermissionsListener != null) {
                requestPermissions(fragment, mRequestPermissions, requestCode, mOnPermissionsListener);
            }
        }
    }

    /**
     * 前往应用设置详情界面
     *
     * @param activity
     */
    public static void startAppSettingDetail(Activity activity) {
        AppInfoUtil.startAppSettingDetail(activity, REQUEST_CODE_DETAIL_SETTING);
    }


}
