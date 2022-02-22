package com.mirkowu.lib_camera;

import androidx.annotation.FloatRange;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageCapture;

import java.io.File;

/**
 * @author <a href="mailto:jenly1314@gmail.com">Jenly</a>
 */
public interface ICameraControl {
    /**
     * 拍照
     */
    void takePhoto(File file, ImageCapture.OnImageSavedCallback callBack);

    /**
     * 放大
     */
    void zoomIn();

    /**
     * 缩小
     */
    void zoomOut();

    /**
     * 缩放到指定比例
     *
     * @param ratio
     */
    void zoomTo(float ratio);

    /**
     * 线性放大
     */
    void lineZoomIn();

    /**
     * 线性缩小
     */
    void lineZoomOut();

    /**
     * 线性缩放到指定比例
     *
     * @param linearZoom
     */
    void lineZoomTo(@FloatRange(from = 0.0, to = 1.0) float linearZoom);

    /**
     * 设置闪光灯（手电筒）是否开启
     *
     * @param torch
     */
    void enableTorch(boolean torch);

    /**
     * 闪光灯（手电筒）是否开启
     *
     * @return
     */
    boolean isTorchEnabled();

    /**
     * 是否支持闪光灯
     *
     * @return
     */
    boolean hasFlashUnit();

    void setFlashMode(@ImageCapture.FlashMode int flashMode);

    @ImageCapture.FlashMode
    int getFlashMode();


    @CameraSelector.LensFacing
    int getCameraId();

    void setCameraId(@CameraSelector.LensFacing int lensFacing);

    //    void setAspectRatio(@AspectRatio.Ratio int aspectRatio);
    void setCaptureMode(@ImageCapture.CaptureMode int captureMode);

}
