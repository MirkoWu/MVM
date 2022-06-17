package com.mirkowu.lib_qr;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

import com.google.zxing.Result;
import com.king.zxing.CaptureActivity;
import com.king.zxing.util.CodeUtils;
import com.mirkowu.lib_photo.ImagePicker;
import com.mirkowu.lib_photo.PickerConfig;
import com.mirkowu.lib_photo.bean.MediaBean;
import com.mirkowu.lib_photo.callback.OnPickResultListener;
import com.mirkowu.lib_util.PermissionsUtil;
import com.mirkowu.lib_util.utilcode.util.BarUtils;
import com.mirkowu.lib_widget.Toolbar;

import java.util.ArrayList;

public class QrScanActivity extends CaptureActivity implements View.OnClickListener {

    public static void start(Context context) {
        Intent starter = new Intent(context, QrScanActivity.class);
        context.startActivity(starter);
    }

    @Override
    public int getLayoutId() {
        return R.layout.qr_activity_qr_scan;
    }

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        BarUtils.transparentStatusBar(this);

        Toolbar toolbar = findViewById(R.id.mToolbar);
        ImageView ivFlashlight = findViewById(R.id.ivFlashlight);
        ImageView ivAlbum = findViewById(R.id.ivAlbum);
        ivFlashlight.setOnClickListener(this);
        ivAlbum.setOnClickListener(this);


        ScanConfig config = QRScanner.getInstance().getScanConfig();

        if (!TextUtils.isEmpty(config.getTitle())) {
            toolbar.setTitle(config.getTitle());
        }

        ivFlashlight.setVisibility(config.isShowFlashlight() ? View.VISIBLE : View.GONE);
        ivAlbum.setVisibility(config.isShowAlbumPick() ? View.VISIBLE : View.GONE);

        checkPermission();
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
                        new AlertDialog.Builder(QrScanActivity.this)
                                .setTitle(R.string.qr_permission_dialog_title)
                                .setMessage(R.string.qr_permission_rationale_storage_for_photo)
                                .setPositiveButton(R.string.qr_permission_dialog_ok, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        checkPermission(); //如果想继续同意权限 就重新调用改方法
                                    }
                                })
                                .setNegativeButton(R.string.qr_permission_dialog_cancel, null)
                                .create().show();
                    }

                    @Override
                    public void onPermissionDenied(int requestCode) {
                        new AlertDialog.Builder(QrScanActivity.this)
                                .setTitle(R.string.qr_error_no_permission)
                                .setMessage(R.string.qr_lack_storage_permission)
                                .setPositiveButton(R.string.qr_permission_dialog_ok, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        PermissionsUtil.startAppSettingForResult(QrScanActivity.this);
                                    }
                                })
                                .setNegativeButton(R.string.qr_permission_dialog_cancel, null)
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

    @Override
    public void initCameraScan() {
        super.initCameraScan();
        ScanConfig config = QRScanner.getInstance().getScanConfig();
        getCameraScan()
                .setVibrate(config.isVibrate())
                .setPlayBeep(config.isPlayBeep())
                .setNeedTouchZoom(config.isTouchZoom())
                .setNeedAutoZoom(config.isCameraAutoZoom());

    }

    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.ivFlashlight) {
            toggleTorchState();
        } else if (id == R.id.ivAlbum) {
            parseCodeFromAlbum();
        }
    }

    /**
     * 解析相册的二维码图片
     */
    private void parseCodeFromAlbum() {
        ImagePicker.getInstance()
                .setPickerConfig(new PickerConfig()
                        .setMaxPickCount(1)
                        .setShowCamera(false)
                        .setShowGif(false)
                        .setShowVideo(false))
                .setOnPickResultListener(new OnPickResultListener() {
                    @Override
                    public void onPickResult(@NonNull ArrayList<MediaBean> imageList) {
                        if (!imageList.isEmpty()) {
                            String path = imageList.get(0).path;
                            if (!TextUtils.isEmpty(path)) {
                                try {
                                    //解析条形码/二维码
                                    String result = CodeUtils.parseCode(path);
                                    onSuccess(result);
                                } catch (Exception e) {
                                    onFailure();
                                } finally {
                                    finish();
                                }
                            }
                        }
                    }
                }).start(this);
    }

    private void onSuccess(String result) {
        OnScanResultListener resultListener = QRScanner.getInstance().getOnScanResultListener();
        if (resultListener != null) {
            resultListener.onSuccess(result);
        }
    }

    private void onFailure() {
        OnScanResultListener resultListener = QRScanner.getInstance().getOnScanResultListener();
        if (resultListener != null) {
            resultListener.onFailure();
        }
    }

    @Override
    public boolean onScanResultCallback(Result result) {
        onSuccess(result.getText());
        return false;
    }

    @Override
    public void onScanResultFailure() {
        onFailure();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ImagePicker.getInstance().removeAllListener();
    }
}