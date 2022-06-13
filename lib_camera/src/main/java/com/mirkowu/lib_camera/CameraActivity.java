package com.mirkowu.lib_camera;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.DrawableRes;
import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.view.PreviewView;

import com.mirkowu.lib_util.FileUtil;
import com.mirkowu.lib_util.LogUtil;
import com.mirkowu.lib_util.PermissionsUtil;
import com.mirkowu.lib_util.utilcode.util.BarUtils;

import java.io.File;

public class CameraActivity extends AppCompatActivity {
    private static final int CAMERA_PERMISSION_REQUEST_CODE = 0X86;
    public static final String TAG = CameraActivity.class.getSimpleName();
    protected PreviewView mPreviewView;
    protected CameraScan mCameraScan;
    protected ImageView mTakePhoto;
    protected ImageView mFlashlight;
    protected View mBlackMask;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        int layoutId = getLayoutId();
        if (isContentView(layoutId)) {
            setContentView(layoutId);
        }
        initUI();

        initCameraScan();

        checkPermission();
    }

    /**
     * 初始化
     */
    public void initUI() {
        BarUtils.transparentStatusBar(this);

        mPreviewView = findViewById(R.id.mPreviewView);
        mBlackMask = findViewById(R.id.mBlackMask);
        ImageView mSwitchCamera = findViewById(R.id.mSwitchCamera);
        mSwitchCamera.setOnClickListener(v -> switchCamera());

        int takePhotoId = getTakePhotoId();
        if (takePhotoId != 0) {
            mTakePhoto = findViewById(takePhotoId);
            if (mTakePhoto != null) {
                mTakePhoto.setOnClickListener(v -> onClickTakePhoto());
            }
        }
        int ivFlashlightId = getFlashlightId();
        if (ivFlashlightId != 0) {
            mFlashlight = findViewById(ivFlashlightId);
            if (mFlashlight != null) {
                mFlashlight.setOnClickListener(v -> onClickFlashlight());
            }
        }
    }


    private void checkPermission() {
        PermissionsUtil.getInstance().requestPermissions(this, PermissionsUtil.GROUP_CAMERA,
                new PermissionsUtil.OnPermissionsListener() {
                    @Override
                    public void onPermissionGranted(int requestCode) {
                        startCamera();
                    }

                    @Override
                    public void onPermissionShowRationale(int requestCode, String[] permissions) {
                        new AlertDialog.Builder(CameraActivity.this)
                                .setTitle(R.string.camera_permission_dialog_title)
                                .setMessage(R.string.camera_permission_rationale_camera)
                                .setPositiveButton(R.string.camera_permission_dialog_ok, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        checkPermission(); //如果想继续同意权限 就重新调用该方法
                                    }
                                })
                                .setNegativeButton(R.string.camera_permission_dialog_cancel, null)
                                .create().show();
                    }

                    @Override
                    public void onPermissionDenied(int requestCode) {
                        new AlertDialog.Builder(CameraActivity.this)
                                .setTitle(R.string.camera_error_no_permission)
                                .setMessage(R.string.camera_lack_camera_permission)
                                .setPositiveButton(R.string.camera_permission_dialog_ok, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        PermissionsUtil.startAppSettingForResult(CameraActivity.this);
                                    }
                                })
                                .setNegativeButton(R.string.camera_permission_dialog_cancel, null)
                                .create().show();
                    }
                });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        PermissionsUtil.getInstance().onRequestPermissionsResult(this, requestCode, permissions, grantResults);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        PermissionsUtil.getInstance().onActivityResult(this, requestCode, resultCode, data);
    }

    public void onClickTakePhoto() {
        takePhoto();
    }

    /**
     * 点击手电筒
     */
    public void onClickFlashlight() {
        toggleFlashMode();
    }

    /**
     * 初始化CameraScan
     */
    public void initCameraScan() {
        mCameraScan = new DefaultCameraScan(this, mPreviewView);
//        mCameraScan.setOnScanResultCallback(this);
    }

    /**
     * 启动相机预览
     */
    public void startCamera() {
        if (mCameraScan != null) {
            mCameraScan.startCamera();
        }
    }

    public void takePhoto() {
        shotAnim();
        mTakePhoto.setEnabled(false);
        File photo = FileUtil.createCameraFile(this,"IMG_" + System.currentTimeMillis() + ".jpg");
        mCameraScan.takePhoto(photo, new ImageCapture.OnImageSavedCallback() {
            @Override
            public void onImageSaved(@NonNull ImageCapture.OutputFileResults outputFileResults) {
                mTakePhoto.setEnabled(true);

//                Uri uri = outputFileResults.getSavedUri();
                boolean result = FileUtil.addGraphToGallery(CameraActivity.this, photo);
                LogUtil.e(TAG, "onImageSaved : " + result);
            }

            @Override
            public void onError(@NonNull ImageCaptureException exception) {
                LogUtil.e(TAG, "ImageCaptureException " + exception.toString());
                mTakePhoto.setEnabled(true);
            }
        });
    }


    /**
     * 释放相机
     */
    public void releaseCamera() {
        if (mCameraScan != null) {
            mCameraScan.release();
        }
    }

    /**
     * 切换闪光灯状态（开启/关闭）
     */
    public void toggleTorchState() {
        if (mCameraScan != null) {
            boolean isTorch = mCameraScan.isTorchEnabled();
            mCameraScan.enableTorch(!isTorch);
//            if (mFlashlight != null) {
//                mFlashlight.setSelected(!isTorch);
//            }
        }
    }

    /**
     * 切换闪光灯状态（开启/关闭）
     */
    public void toggleFlashMode() {
        if (mCameraScan != null) {
            if (mCameraScan instanceof DefaultCameraScan) {
                int mode = mCameraScan.getFlashMode();
                if (mode == ImageCapture.FLASH_MODE_OFF) {
                    mCameraScan.setFlashMode(ImageCapture.FLASH_MODE_ON);
                    updateFlashlightUI(R.drawable.camera_svg_flash_light_on);
                    mCameraScan.enableTorch(true);
                } else {
                    mCameraScan.enableTorch(false);
                    if (mode == ImageCapture.FLASH_MODE_ON) {
                        mCameraScan.setFlashMode(ImageCapture.FLASH_MODE_AUTO);
                        updateFlashlightUI(R.drawable.camera_svg_flash_light_auto);
                    } else {
                        mCameraScan.setFlashMode(ImageCapture.FLASH_MODE_OFF);
                        updateFlashlightUI(R.drawable.camera_svg_flash_light_off);
                    }
                }
            }
        }
    }

    private void updateFlashlightUI(@DrawableRes int res) {
        if (mFlashlight != null) {
            mFlashlight.setImageResource(res);
        }
    }

    public void switchCamera() {
        if (mCameraScan != null) {
            int cameraId = mCameraScan.getCameraId();
            if (cameraId == CameraSelector.LENS_FACING_BACK) {
                mCameraScan.setCameraId(CameraSelector.LENS_FACING_FRONT);
            } else {
                mCameraScan.setCameraId(CameraSelector.LENS_FACING_BACK);
            }
        }
    }

    /**
     * 模拟快门动画
     */
    public void shotAnim() {
        mBlackMask.setVisibility(View.VISIBLE);
        mBlackMask.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (mBlackMask != null) {
                    mBlackMask.setVisibility(View.GONE);
                }
            }
        }, 100);
    }

//
//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//        if (requestCode == CAMERA_PERMISSION_REQUEST_CODE) {
//            requestCameraPermissionResult(permissions, grantResults);
//        }
//    }
//
//    /**
//     * 请求Camera权限回调结果
//     *
//     * @param permissions
//     * @param grantResults
//     */
//    public void requestCameraPermissionResult(@NonNull String[] permissions, @NonNull int[] grantResults) {
//        if (PermissionUtils.requestPermissionsResult(Manifest.permission.CAMERA, permissions, grantResults)) {
//            startCamera();
//        } else {
//            finish();
//        }
//    }

    @Override
    protected void onDestroy() {
        releaseCamera();
        super.onDestroy();
    }

    /**
     * 返回true时会自动初始化{@link #setContentView(int)}，返回为false是需自己去初始化{@link #setContentView(int)}
     *
     * @param layoutId
     * @return 默认返回true
     */
    public boolean isContentView(@LayoutRes int layoutId) {
        return true;
    }

    /**
     * 布局id
     *
     * @return
     */
    public int getLayoutId() {
        return R.layout.camera_layout_preview;
    }

    /**
     * {@link #mTakePhoto} 的 ID
     *
     * @return 默认返回{@code R.id.viewfinderView}, 如果不需要扫码框可以返回0
     */
    public int getTakePhotoId() {
        return R.id.mTakePhoto;
    }


    /**
     * 预览界面{@link #mPreviewView} 的ID
     *
     * @return
     */
    public int getPreviewViewId() {
        return R.id.mPreviewView;
    }

    /**
     * 获取 {@link #mFlashlight} 的ID
     *
     * @return 默认返回{@code R.id.ivFlashlight}, 如果不需要手电筒按钮可以返回0
     */
    public int getFlashlightId() {
        return R.id.mFlashlight;
    }

    /**
     * Get {@link CameraScan}
     *
     * @return {@link #mCameraScan}
     */
    public CameraScan getCameraScan() {
        return mCameraScan;
    }


}