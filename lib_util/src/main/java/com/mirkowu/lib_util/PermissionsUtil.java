package com.mirkowu.lib_util;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.Settings;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
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
    public static final String[] GROUP_CAMERA = new String[]{
            Manifest.permission.CAMERA,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE
    };
    /**
     * 文件存储
     */
    public static final String[] GROUP_STORAGE = new String[]{
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE
    };
    public static final String[] MANAGE_EXTERNAL_STORAGE = new String[]{
            Manifest.permission.MANAGE_EXTERNAL_STORAGE,
    };
    /**
     * 蓝牙权限（需要粗略定位权限 ）
     * 编译版本10及以上 需要 ACCESS_FINE_LOCATION
     * 9以下 有 ACCESS_COARSE_LOCATION 或 ACCESS_FINE_LOCATION 任一个都行
     */
    public static final String[] GROUP_BLUETOOTH = new String[]{
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.BLUETOOTH
    };
    /**
     * 定位权限
     */
    public static final String[] GROUP_LOCATION_ALL = new String[]{
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION
    };
    /**
     * 手机信息
     */
    public static final String[] GROUP_PHONE = new String[]{
            Manifest.permission.READ_PHONE_STATE
    };

    public static interface OnPermissionsListener {
        void onPermissionGranted(int requestCode);

        void onPermissionShowRationale(int requestCode, @NonNull String[] permissions);

        void onPermissionDenied(int requestCode);
    }


    public static final int REQUEST_NONE = -1; //默认状态
    public static final int REQUEST_CODE = 2488;
    public static final int REQUEST_CODE_DETAIL_SETTING = 1022;
    public static final int REQUEST_CODE_STORAGE_MANAGE = 1023;

    private static int sRequestCode = REQUEST_NONE;
    private static String[] sRequestPermissions;
    private static OnPermissionsListener sOnPermissionsListener;

    /**
     * Android 11及以上申请完全的外部存储权限
     */
    public void requestStorageManage(@NonNull Activity activity, @NonNull OnPermissionsListener listener) {
        requestStorageManage(activity, REQUEST_CODE_STORAGE_MANAGE, listener);
    }

    private void requestStorageManage(@NonNull Activity activity, int requestCode,
                                      @NonNull OnPermissionsListener listener) {
        //大于等于 Android 11 设备上
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            requestStorageManageAboveAndroid11(activity, requestCode, listener);
        } else {
            //Android 11 以前的逻辑
            requestPermissions(activity, GROUP_STORAGE, requestCode, listener);
        }
    }

    /**
     * Android 11及以上申请完全的外部存储权限
     */
    public void requestStorageManage(@NonNull Fragment fragment, @NonNull OnPermissionsListener listener) {
        requestStorageManage(fragment, REQUEST_CODE_STORAGE_MANAGE, listener);
    }

    private void requestStorageManage(@NonNull Fragment fragment, int requestCode,
                                      @NonNull OnPermissionsListener listener) {
        //大于等于 Android 11 设备上
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            requestStorageManageAboveAndroid11(fragment, requestCode, listener);
        } else {
            //Android 11 以前的逻辑
            requestPermissions(fragment, GROUP_STORAGE, requestCode, listener);
        }
    }


    @RequiresApi(api = Build.VERSION_CODES.R)
    private void requestStorageManageAboveAndroid11(@NonNull Activity activity, int requestCode,
                                                    @NonNull OnPermissionsListener listener) {
        sRequestPermissions = MANAGE_EXTERNAL_STORAGE;
        sOnPermissionsListener = listener;
        sRequestCode = requestCode;

        if (sOnPermissionsListener == null) {
            throw new IllegalArgumentException("onPermissionsListener is null");
        }
        if (Environment.isExternalStorageManager()) {
            sRequestCode = REQUEST_NONE;
            listener.onPermissionGranted(requestCode);
        } else {
            startAppAllFilesManage(activity);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.R)
    private void requestStorageManageAboveAndroid11(@NonNull Fragment fragment, int requestCode,
                                                    @NonNull OnPermissionsListener listener) {
        sRequestPermissions = MANAGE_EXTERNAL_STORAGE;
        sOnPermissionsListener = listener;
        sRequestCode = requestCode;

        if (sOnPermissionsListener == null) {
            throw new IllegalArgumentException("onPermissionsListener is null");
        }
        if (Environment.isExternalStorageManager()) {
            sRequestCode = REQUEST_NONE;
            listener.onPermissionGranted(requestCode);
        } else {
            startAppAllFilesManage(fragment);
        }
    }


    /**
     * 请求权限
     */
    public void requestPermissions(@NonNull Activity activity, @NonNull String[] permissions,
                                   @NonNull OnPermissionsListener listener) {
        requestPermissions(activity, permissions, REQUEST_CODE, listener);
    }


    public void requestPermissions(@NonNull Activity activity, @NonNull String[] permissions, int requestCode,
                                   @NonNull OnPermissionsListener listener) {
        sRequestPermissions = permissions;
        sOnPermissionsListener = listener;
        sRequestCode = requestCode;

        if (sRequestPermissions == null || sOnPermissionsListener == null) {
            throw new IllegalArgumentException("permissions or onPermissionsListener is null");
        }

        if (hasPermissions(activity, permissions)) {
            sRequestCode = REQUEST_NONE;
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
        sRequestPermissions = permissions;
        sOnPermissionsListener = listener;
        sRequestCode = requestCode;

        if (sRequestPermissions == null || sOnPermissionsListener == null) {
            throw new IllegalArgumentException("permissions or onPermissionsListener is null");
        }

        if (hasPermissions(fragment.getContext(), permissions)) {
            sRequestCode = REQUEST_NONE;
            listener.onPermissionGranted(requestCode);
        } else {
            //没权限就去请求
            fragment.requestPermissions(permissions, requestCode);
        }
    }


//    public static boolean isStorageManagePermissions(String manageExternalStorage) {
//        return TextUtils.equals(manageExternalStorage, Manifest.permission.MANAGE_EXTERNAL_STORAGE)
//        for (String perms : permissions) {
//            if (TextUtils.equals(perms, Manifest.permission.MANAGE_EXTERNAL_STORAGE)
//                    || TextUtils.equals(perms, Manifest.permission.WRITE_EXTERNAL_STORAGE)
//                    || TextUtils.equals(perms, Manifest.permission.READ_EXTERNAL_STORAGE)) {
//                return true;
//            }
//        }
//        return false;
//    }

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
        if (requestCode == sRequestCode && grantResults != null && grantResults.length > 0) {
            boolean isGranted = true;
            for (int result : grantResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    isGranted = false;
                    break;
                }
            }

            if (sOnPermissionsListener != null) {
                sRequestCode = REQUEST_NONE;
                if (isGranted) {
                    sOnPermissionsListener.onPermissionGranted(requestCode);
                } else if (shouldRationale(activity, sRequestPermissions)) {
                    sOnPermissionsListener.onPermissionShowRationale(requestCode, sRequestPermissions);
                } else {
                    sOnPermissionsListener.onPermissionDenied(requestCode);
                }
            }
        }
    }

    public void onRequestPermissionsResult(Fragment fragment, int requestCode, String[] permissions, int[] grantResults) {
        //有可能会返回空数组,做下判断
        if (requestCode == sRequestCode && grantResults != null && grantResults.length > 0) {
            boolean isGranted = true;
            for (int result : grantResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    isGranted = false;
                    break;
                }
            }

            if (sOnPermissionsListener != null) {
                sRequestCode = REQUEST_NONE;
                if (isGranted) {
                    sOnPermissionsListener.onPermissionGranted(requestCode);
                } else if (shouldRationale(fragment, sRequestPermissions)) {
                    sOnPermissionsListener.onPermissionShowRationale(requestCode, sRequestPermissions);
                } else {
                    sOnPermissionsListener.onPermissionDenied(requestCode);
                }
            }
        }
    }


    public void onActivityResult(Activity activity, int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_STORAGE_MANAGE) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                if (sOnPermissionsListener != null) {
                    sRequestCode = REQUEST_NONE;
                    if (Environment.isExternalStorageManager()) {
                        sOnPermissionsListener.onPermissionGranted(requestCode);
                    } else {
                        sOnPermissionsListener.onPermissionDenied(requestCode);
                    }
                }
            }
        } else if (requestCode == REQUEST_CODE_DETAIL_SETTING) {
            if (sOnPermissionsListener != null) {
                requestPermissions(activity, sRequestPermissions, requestCode, sOnPermissionsListener);
            }
        }
    }

    public void onActivityResult(Fragment fragment, int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_STORAGE_MANAGE) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                if (sOnPermissionsListener != null) {
                    sRequestCode = REQUEST_NONE;
                    if (Environment.isExternalStorageManager()) {
                        sOnPermissionsListener.onPermissionGranted(requestCode);
                    } else {
                        sOnPermissionsListener.onPermissionDenied(requestCode);
                    }
                }
            }
        } else if (requestCode == REQUEST_CODE_DETAIL_SETTING) {
            if (sOnPermissionsListener != null) {
                requestPermissions(fragment, sRequestPermissions, requestCode, sOnPermissionsListener);
            }
        }
    }

    public void removeListener() {
        sRequestPermissions = null;
        sOnPermissionsListener = null;
    }

    @RequiresApi(api = Build.VERSION_CODES.R)
    public static void startAppAllFilesManage(Activity context) {
        Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
        intent.setData(Uri.parse("package:" + context.getPackageName()));
        context.startActivityForResult(intent, REQUEST_CODE_STORAGE_MANAGE);
    }

    public static void startAppAllFilesManage(Fragment fragment) {
        final Context context = fragment.getContext();
        Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
        intent.setData(Uri.parse("package:" + context.getPackageName()));
        fragment.startActivityForResult(intent, REQUEST_CODE_STORAGE_MANAGE);
    }

    /**
     * 前往应用设置详情界面
     *
     * @param activity
     */
    public static void startAppSettingNoResult(Activity activity) {
        AppSettingUtil.startAppSettingNoResult(activity);
    }

    public static boolean startAppSettingForResult(Activity activity) {
        return AppSettingUtil.startAppSettingForResult(activity, REQUEST_CODE_DETAIL_SETTING);
    }

    public static boolean startAppSettingForResult(Fragment fragment) {
        return AppSettingUtil.startAppSettingForResult(fragment, REQUEST_CODE_DETAIL_SETTING);
    }
}
