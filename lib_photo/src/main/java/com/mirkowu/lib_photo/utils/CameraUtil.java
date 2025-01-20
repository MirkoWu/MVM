package com.mirkowu.lib_photo.utils;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.mirkowu.lib_photo.R;
import com.mirkowu.lib_util.FileUtils;
import com.mirkowu.lib_util.permission.PermissionCallback;
import com.mirkowu.lib_util.permission.Permissions;
import com.mirkowu.lib_util.permission.SmartPermissions;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static android.app.Activity.RESULT_OK;

/**
 * Created by donglua on 2016/10/19.
 */

public class CameraUtil {
    private static File sTmpFile;

    public static final int REQUEST_CAMERA = 0x3230;


    public static void startCameraAction(final FragmentActivity activity) {
        SmartPermissions.with(Permissions.getGROUP_CAMERA()).requestAuto(activity, new PermissionCallback() {
            @Override
            public void onGranted(@NonNull List<String> permissions) {
                Intent intent = createCameraIntent(activity);
                if (intent != null) {
                    activity.startActivityForResult(intent, REQUEST_CAMERA);
                }
            }

            @Override
            public void onDenied(@NonNull List<String> permissions, boolean hasPermissionForeverDenied) {

            }
        });
    }

    public static void startCameraAction(final Fragment fragment) {
        final Context context = fragment.getContext();
        SmartPermissions.with(Permissions.getGROUP_CAMERA()).requestAuto(fragment.getActivity(), new PermissionCallback() {
            @Override
            public void onGranted(@NonNull List<String> permissions) {
                Intent intent = createCameraIntent(context);
                if (intent != null) {
                    fragment.startActivityForResult(intent, REQUEST_CAMERA);
                }
            }

            @Override
            public void onDenied(@NonNull List<String> permissions, boolean hasPermissionForeverDenied) {

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
