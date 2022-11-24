package com.mirkowu.lib_camera.util;

import android.app.Activity;
import android.content.Context;
import android.graphics.ImageFormat;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.util.Size;
import android.view.Surface;

import com.mirkowu.lib_util.LogUtil;
import com.mirkowu.lib_util.utilcode.util.Utils;

import java.util.Arrays;
import java.util.Comparator;

public class CameraUtils {
    public static final String TAG = CameraUtils.class.getSimpleName();

    public static int getOrientation(Activity activity) {
        int rotation = activity.getWindowManager().getDefaultDisplay().getRotation();
        int degrees = 0;
        switch (rotation) {
            case Surface.ROTATION_0:
                degrees = 0;
                break;
            case Surface.ROTATION_90:
                degrees = 90;
                break;
            case Surface.ROTATION_180:
                degrees = 180;
                break;
            case Surface.ROTATION_270:
                degrees = 270;
                break;
        }
        return degrees;
    }

    public static Size getBestMatchingSize(int previewWidth, int previewHeight) {
        int targetWidth = Math.max(previewWidth, previewHeight);
        int targetHeight = Math.min(previewWidth, previewHeight);

        LogUtil.e(TAG, "getBestMatchingSize: 目标匹配宽高 = " + targetWidth + " x " + targetHeight);

        if (targetWidth == 0 || targetHeight == 0) return new Size(targetWidth, targetHeight);

        float targetRatio = targetWidth * 1f / targetHeight;
        Size selectSize = null;
        try {
            CameraManager mCameraManager = (CameraManager) Utils.getApp().getSystemService(Context.CAMERA_SERVICE);
            for (final String cameraId : mCameraManager.getCameraIdList()) {
                CameraCharacteristics cameraCharacteristics = mCameraManager.getCameraCharacteristics(cameraId);
                StreamConfigurationMap streamConfigurationMap = cameraCharacteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
                Size[] sizes = streamConfigurationMap.getOutputSizes(ImageFormat.JPEG);

                //按分辨率从大到小排序
                Arrays.sort(sizes, new Comparator<Size>() {
                    @Override
                    public int compare(Size o1, Size o2) {
                        return o2.getWidth() * o2.getHeight() - o1.getWidth() * o1.getHeight();
                    }
                });

                //首先选取宽高比与预览窗口高宽比一致且最大的输出尺寸,或者转int像素一样的尺寸
                LogUtil.e(TAG, "1.选择与其高宽比一致的输出尺寸>>>>>");
                for (int i = 0; i < sizes.length; i++) {
                    Size itemSize = sizes[i];
                    int width = Math.max(itemSize.getWidth(), itemSize.getHeight());
                    int height = Math.min(itemSize.getWidth(), itemSize.getHeight());
                    LogUtil.e(TAG, "当前itemSize 宽=" + width + "高=" + height);
                    float curRatio = ((float) width) / height;
                    if ((curRatio == targetRatio || (int) (width * targetRatio) == height)
                            && height >= height) {
                        selectSize = new Size(itemSize.getHeight(), itemSize.getWidth());
                        LogUtil.e(TAG, "1.找到尺寸 = " + selectSize.toString());
                        return selectSize;
                    }
                }
                //如果不存在宽高比与预览窗口高宽比一致的输出尺寸，则选择与其宽高比最接近的输出尺寸
                LogUtil.e(TAG, "2.选择与其宽高比最接近的输出尺寸>>>>>");
                float detRatioMin = Float.MAX_VALUE;
                for (int i = 0; i < sizes.length; i++) {
                    Size itemSize = sizes[i];
                    int width = Math.max(itemSize.getWidth(), itemSize.getHeight());
                    int height = Math.min(itemSize.getWidth(), itemSize.getHeight());
                    float curRatio = ((float) width) / height;
                    float ratioOffset = Math.abs(curRatio - targetRatio);
                    if (ratioOffset < detRatioMin) {
                        detRatioMin = ratioOffset;
                        selectSize = new Size(itemSize.getHeight(), itemSize.getWidth());
                    }
                }
                if (selectSize != null && selectSize.getWidth() * selectSize.getHeight() > targetWidth * targetHeight) {
                    return selectSize;
                }
                //如果宽高比最接近的输出尺寸太小，则选择与预览窗口面积最接近的输出尺寸
                LogUtil.e(TAG, "3.选择与预览窗口面积最接近的输出尺寸>>>>>");
                long area = targetWidth * targetHeight;
                long detAreaMin = Long.MAX_VALUE;
                for (int i = 0; i < sizes.length; i++) {
                    Size itemSize = sizes[i];
                    long areaOffset = Math.abs(itemSize.getWidth() * itemSize.getHeight() - area);
                    if (areaOffset < detAreaMin) {
                        detAreaMin = areaOffset;
                        selectSize = new Size(itemSize.getHeight(), itemSize.getWidth());
                    }
                }
            }
        } catch (CameraAccessException e) {
            LogUtil.e(e.toString());
        }
        if (selectSize == null) {
            selectSize = new Size(targetWidth, targetHeight);
        }
        return selectSize;
    }
}
