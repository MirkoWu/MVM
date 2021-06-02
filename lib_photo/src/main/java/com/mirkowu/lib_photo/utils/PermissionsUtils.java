package com.mirkowu.lib_photo.utils;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Observable;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.provider.Settings;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.mirkowu.lib_photo.R;
import com.mirkowu.lib_util.PermissionsUtil;

import java.io.File;
import java.io.IOException;

import static android.app.Activity.RESULT_OK;

/**
 * Created by donglua on 2016/10/19.
 */

public class PermissionsUtils {
    private static File mTmpFile;

    public static final int REQUEST_CAMERA = 0x3230;
    public static final int REQUEST_ALBUM = 0x3234;
    public static final int REQUEST_CAMERA_PERMISSION = 1111;
    public static final int REQUEST_EXTERNAL_READ_PERMISSION = 2222;
    public static final int REQUEST_EXTERNAL_WRITE_PERMISSION = 3333;

    public static final String[] PERMISSIONS_CAMERA_STORAGE = {
            Manifest.permission.CAMERA,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public static final String[] PERMISSIONS_EXTERNAL_READ = {
            Manifest.permission.READ_EXTERNAL_STORAGE
    };


    public static void showCameraAction(final Activity activity) {
        PermissionsUtil.getInstance().requestPermissions(activity, PermissionsUtil.PERMISSION_CAMERA,
                new PermissionsUtil.OnPermissionsListener() {
                    @Override
                    public void onPermissionGranted(int requestCode) {
                        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        if (intent.resolveActivity(activity.getPackageManager()) != null) {
                            try {
                                mTmpFile = FileUtils.createTmpFile(activity);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            if (mTmpFile != null && mTmpFile.exists()) {
                                Uri imgUri = FileUtils.createFileUri(activity, mTmpFile);
                                intent.putExtra(MediaStore.EXTRA_OUTPUT, imgUri);
                                activity.startActivityForResult(intent, REQUEST_CAMERA);
                            } else {
                                Toast.makeText(activity, R.string.ivp_error_image_not_exist, Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(activity, R.string.ivp_msg_no_camera, Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onPermissionShowRationale(int requestCode, String[] permissions) {
                        new AlertDialog.Builder(activity)
                                .setTitle(R.string.ivp_permission_dialog_title)
                                .setMessage(R.string.ivp_permission_rationale_camera)
                                .setPositiveButton(R.string.ivp_permission_dialog_ok, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        showCameraAction(activity);//如果想继续同意权限 就重新调用改方法
                                    }
                                })
                                .setNegativeButton(R.string.ivp_permission_dialog_cancel, null)
                                .create().show();
                    }

                    @Override
                    public void onPermissionDenied(int requestCode) {
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
                });
    }

    public static void showCameraAction(final Fragment fragment) {
        final Activity activity = fragment.getActivity();
        final Context context = fragment.getContext();
        PermissionsUtil.getInstance().requestPermissions(activity, PermissionsUtil.PERMISSION_CAMERA,
                new PermissionsUtil.OnPermissionsListener(){

                    @Override
                    public void onPermissionGranted(int requestCode) {
                        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        if (intent.resolveActivity(context.getPackageManager()) != null) {
                            try {
                                mTmpFile = FileUtils.createTmpFile(context);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            if (mTmpFile != null && mTmpFile.exists()) {
                                Uri imgUri = FileUtils.createFileUri(activity, mTmpFile);
                                intent.putExtra(MediaStore.EXTRA_OUTPUT, imgUri);
                                fragment.startActivityForResult(intent, REQUEST_CAMERA);
                            } else {
                                Toast.makeText(context, R.string.ivp_error_image_not_exist, Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(context, R.string.ivp_msg_no_camera, Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onPermissionShowRationale(int requestCode, String[] permissions) {
                        new AlertDialog.Builder(context)
                                .setTitle(R.string.ivp_permission_dialog_title)
                                .setMessage(R.string.ivp_permission_rationale_camera)
                                .setPositiveButton(R.string.ivp_permission_dialog_ok, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        showCameraAction(fragment);//如果想继续同意权限 就重新调用改方法
                                    }
                                })
                                .setNegativeButton(R.string.ivp_permission_dialog_cancel, null)
                                .create().show();
                    }

                    @Override
                    public void onPermissionDenied(int requestCode) {
                        new AlertDialog.Builder(context)
                                .setTitle(R.string.ivp_error_no_permission)
                                .setMessage(R.string.ivp_permission_lack)
                                .setPositiveButton(R.string.ivp_permission_dialog_ok, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                        intent.setData(Uri.parse("package:" + context.getPackageName()));
                                        fragment.startActivity(intent);
                                    }
                                })
                                .setNegativeButton(R.string.ivp_permission_dialog_cancel, null)
                                .create().show();
                    }
                });

    }

    /**
     * 拍照返回数据
     *
     * @param requestCode
     * @param resultCode
     * @param data
     * @return
     */
    public static File onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CAMERA) {
            if (resultCode == RESULT_OK) {
                if (mTmpFile != null) {
                    return mTmpFile;
                }
            } else {
                // delete tmp file
                while (mTmpFile != null && mTmpFile.exists()) {
                    boolean success = mTmpFile.delete();
                    if (success) {
                        mTmpFile = null;
                    }
                }
            }
        }
        return null;
    }
}
