package com.mirkowu.lib_qr;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;

import com.google.zxing.Result;
import com.king.zxing.CaptureActivity;
import com.mirkowu.lib_util.utilcode.util.BarUtils;
import com.mirkowu.lib_widget.Toolbar;

public class QrScanActivity extends CaptureActivity implements View.OnClickListener {

    public static void start(Context context) {
        Intent starter = new Intent(context, QrScanActivity.class);
        context.startActivity(starter);
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_qr_scan;
    }

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
//        BarUtils.setStatusBarLightMode(this, false);
//        BarUtils.transparentStatusBar(this);
        BarUtils.setStatusBarColor(this, Color.TRANSPARENT);

        Toolbar toolbar = findViewById(R.id.mToolbar);
        ImageView ivFlashlight = findViewById(R.id.ivFlashlight);
        ImageView ivAlbum = findViewById(R.id.ivAlbum);
        ivFlashlight.setOnClickListener(this);
        ivAlbum.setOnClickListener(this);

        toolbar.setTitleColor(Color.WHITE);
        toolbar.setBackIcon(R.drawable.widget_ic_back_white);

        ScanConfig config = QRScanner.getInstance().getScanConfig();

        if (!TextUtils.isEmpty(config.getTitle())) {
            toolbar.setTitle(config.getTitle());
        }

        ivFlashlight.setVisibility(config.isShowFlashlight() ? View.VISIBLE : View.GONE);
        ivAlbum.setVisibility(config.isShowAlbumPick() ? View.VISIBLE : View.GONE);

    }

    @Override
    public void initCameraScan() {
        super.initCameraScan();
        getCameraScan()
                .setPlayBeep(true)
                .setVibrate(true);
    }

    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.ivFlashlight) {
            toggleTorchState();
        } else if (id == R.id.ivAlbum) {
            onBackPressed();
        }
    }

    @Override
    public boolean onScanResultCallback(Result result) {
        OnScanResultListener resultListener = QRScanner.getInstance().getOnScanResultListener();
        if (resultListener != null) {
            resultListener.onSuccess(result.getText());
        }
        return false;
    }

    @Override
    public void onScanResultFailure() {
        OnScanResultListener resultListener = QRScanner.getInstance().getOnScanResultListener();
        if (resultListener != null) {
            resultListener.onFailure();
        }
    }
}