package com.mirkowu.lib_photo.utils;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.mirkowu.lib_photo.R;
import com.mirkowu.lib_util.FileUtils;
import com.mirkowu.lib_util.PermissionsUtils;

import java.io.File;
import java.io.IOException;

import static android.app.Activity.RESULT_OK;

/**
 * Created by donglua on 2016/10/19.
 */

public class CameraUtil {
    private static File sTmpFile;

    public static final int REQUEST_CAMERA = 0x3230;


    public static void startCameraAction(final Activity activity) {
        PermissionsUtils.getInstance().requestPermissions(activity, PermissionsUtils.GROUP_CAMERA,
                new PermissionsUtils.OnPermissionsListener() {
                    @Override
                    public void onPermissionGranted(int requestCode) {
                        Intent intent = createCameraIntent(activity);
                        if (intent != null) {
                            activity.startActivityForResult(intent, REQUEST_CAMERA);
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
                                        startCameraAction(activity); //如果想继续同意权限 就重新调用改方法
                                    }
                                })
                                .setNegativeButton(R.string.ivp_permission_dialog_cancel, null)
                                .create().show();
                    }

                    @Override
                    public void onPermissionDenied(int requestCode) {
                        new AlertDialog.Builder(activity)
                                .setTitle(R.string.ivp_error_no_permission)
                                .setMessage(R.string.ivp_lack_camera_permission)
                                .setPositiveButton(R.string.ivp_permission_dialog_ok, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        PermissionsUtils.startAppSettingForResult(activity);
                                    }
                                })
                                .setNegativeButton(R.string.ivp_permission_dialog_cancel, null)
                                .create().show();
                    }
                });
    }

    public static void startCameraAction(final Fragment fragment) {
        final Context context = fragment.getContext();
        PermissionsUtils.getInstance().requestPermissions(fragment, PermissionsUtils.GROUP_CAMERA,
                new PermissionsUtils.OnPermissionsListener() {

                    @Override
                    public void onPermissionGranted(int requestCode) {
                        Intent intent = createCameraIntent(context);
                        if (intent != null) {
                            fragment.startActivityForResult(intent, REQUEST_CAMERA);
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
                                        startCameraAction(fragment); //如果想继续同意权限 就重新调用改方法
                                    }
                                })
                                .setNegativeButton(R.string.ivp_permission_dialog_cancel, null)
                                .create().show();
                    }

                    @Override
                    public void onPermissionDenied(int requestCode) {
                        new AlertDialog.Builder(context)
                                .setTitle(R.string.ivp_error_no_permission)
                                .setMessage(R.string.ivp_lack_camera_permission)
                                .setPositiveButton(R.string.ivp_permission_dialog_ok, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        PermissionsUtils.startAppSettingForResult(fragment);
                                    }
                                })
                                .setNegativeButton(R.string.ivp_permission_dialog_cancel, null)
                                .create().show();
                    }
                });

    }


    private static Intent createCameraIntent(Context context) {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (intent.resolveActivity(context.getPackageManager()) != null) {
            try {
                sTmpFile = FileUtils.createCameraTmpFile(context);
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (sTmpFile != null && sTmpFile.exists()) {
                Uri imgUri = com.mirkowu.lib_photo.utils.FileUtils.createFileUri(context, sTmpFile);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, imgUri);
                return intent;
            } else {
                Toast.makeText(context, R.string.ivp_error_image_not_exist, Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(context, R.string.ivp_msg_no_camera, Toast.LENGTH_SHORT).show();
        }
        return null;
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
                if (sTmpFile != null) {
                    return sTmpFile;
                }
            } else {
                // delete tmp file
                if (sTmpFile != null && sTmpFile.exists()) {
                    boolean success = sTmpFile.delete();
                    if (success) {
                        sTmpFile = null;
                    }
                }
            }
        }
        return null;
    }
}
