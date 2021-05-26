package com.mirkowu.lib_webview.callback;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

import com.mirkowu.lib_util.FileUtil;
import com.mirkowu.lib_util.LogUtil;
import com.mirkowu.lib_util.PermissionsUtil;
import com.mirkowu.lib_webview.CommonWebView;
import com.tencent.smtt.sdk.WebChromeClient;

import java.io.File;

public class DefaultWebViewFileChooser implements IWebViewFileChooser {
    public static final String[] selectTypeArray = new String[]{"拍摄", "从相册中选择"};
    public static final String ACCEPT_TYPE_IMAGE = "image/*";
    public static final String ACCEPT_TYPE_VIDEO = "video/*";
    public static final int REQ_CAMERA = 0x001010;
    public static final int REQ_FILES = 0x001012;
    private android.webkit.ValueCallback<Uri[]> mFilePathCallback;
    private Activity context;
    private File mTempFle;

    public DefaultWebViewFileChooser(Activity activity) {
        this.context = activity;
    }


    @Override
    public boolean onShowFileChooser(CommonWebView webView, com.tencent.smtt.sdk.ValueCallback<Uri[]> valueCallback, WebChromeClient.FileChooserParams fileChooserParams) {
        mFilePathCallback = valueCallback;
        String[] types = fileChooserParams.getAcceptTypes();
        String type = ((types != null) && (types.length > 0)) ? types[0] : null;

        checkPermission(type);

        //这里要返回true 不然选择文件时会crash
        return true;
    }

    private void checkPermission(String type) {
        PermissionsUtil.getInstance().requestPermissions(context, PermissionsUtil.PERMISSION_CAMERA, new PermissionsUtil.OnPermissionsListener() {
            @Override
            public void onPermissionGranted(int requestCode) {
                fileChooser(type);
            }

            @Override
            public void onPermissionShowRationale(int requestCode, String[] permissions) {

            }

            @Override
            public void onPermissionDenied(int requestCode) {

            }
        });
    }

    @Override
    public void onActivityResult(CommonWebView webView, int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK) {
            onReceiveValue(null);
            return;
        }
        switch (requestCode) {
            case REQ_CAMERA:
                onReceiveValue(mTempFle);
                break;
            case REQ_FILES:
                Uri uri = data.getData();
                if (uri != null) {
                    String absolutePath = FileUtil.getRealFilePath(context, uri);
                    if (absolutePath != null) {
                        File file = new File(absolutePath);
                        onReceiveValue(file);
                    } else {
                        LogUtil.e("选取失败");
                        onReceiveValue(null);
                    }
                } else {
                    LogUtil.e("选取失败");
                    onReceiveValue(null);
                }
        }
    }

    /**
     * 根据类型选取文件
     * 不需要动态权限申请 如果加了要处理被拒绝的情况
     */
    public void fileChooser(@Nullable String acceptType) {
        switch (acceptType) {
            case ACCEPT_TYPE_IMAGE:
            case ACCEPT_TYPE_VIDEO:
                new AlertDialog.Builder(context)
                        .setItems(selectTypeArray, (dialogInterface, index) -> {
                            if (index == 0) {
                                openCamera(acceptType);
                            } else {
                                selectAlbum(acceptType);
                            }
                        })
                        .setOnCancelListener(dialogInterface -> onReceiveValue(null)
                        ).show();
                break;
            default:
                selectFiles(REQ_FILES);
        }

    }

    private void openCamera(String type) {
        switch (type) {
            case ACCEPT_TYPE_IMAGE:
                takePhoto();
                break;
            case ACCEPT_TYPE_VIDEO:

                takeVideo();
                break;
        }
    }


    /**
     * 打开摄像头拍照
     */
    void takePhoto() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        String fileName = String.format("IMG_%d.png", System.currentTimeMillis());
        String path = Environment.getExternalStorageDirectory() + "/DCIM";
        mTempFle = new File(path, fileName);
        File file = new File(path);
        if (!file.exists()) {
            file.mkdirs();
        }
        intent.putExtra(MediaStore.EXTRA_OUTPUT,
                FileUtil.createUri(context, mTempFle));
        context.startActivityForResult(intent, REQ_CAMERA);
    }

    /**
     * 打开摄像头拍摄视频
     */
    void takeVideo() {
        Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        String fileName = String.format("VID_%d.mp4", System.currentTimeMillis());
        String path = Environment.getExternalStorageDirectory() + "/DCIM";
        mTempFle = new File(path, fileName);
        File file = new File(path);
        if (!file.exists()) {
            file.mkdirs();
        }
        intent.putExtra(MediaStore.EXTRA_OUTPUT,
                FileUtil.createUri(context, mTempFle));
        context.startActivityForResult(intent, REQ_CAMERA);
    }

    /**
     * 选择图片/视频
     */
    void selectAlbum(String type) {
        if (!FileUtil.hasSDCard()) {
            onReceiveValue(null);
            return;
        }
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_PICK);
        //使用以上这种模式，并添加以上两句
        intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, type);
        context.startActivityForResult(intent, REQ_FILES);
    }


    /**
     * 为空的时候就 就统一选择文件
     */
    void selectFiles(int requestCode) {
        if (!FileUtil.hasSDCard()) {
            onReceiveValue(null);
            return;
        }
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_PICK);
        intent.setType("*/*");
        context.startActivityForResult(Intent.createChooser(intent, "选择要导入的文件"), requestCode);
    }

    /**
     * 接受参数结束
     */
    void onReceiveValue(File file) {
        if (mFilePathCallback != null) {
            if (file != null) {
                Uri uri = Uri.fromFile(file);
                Uri[] uriArray = new Uri[]{uri};
                mFilePathCallback.onReceiveValue(uriArray);
                mFilePathCallback = null;
            } else {
                mFilePathCallback.onReceiveValue(null);
            }
        }
    }
}
