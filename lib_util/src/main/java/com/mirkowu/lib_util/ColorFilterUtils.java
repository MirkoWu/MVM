package com.mirkowu.lib_util;

import android.app.Activity;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.view.View;

public class ColorFilterUtils {

    /**
     * 灰色滤镜效果（eg.悼念日）
     *
     * @param view
     */
    public static void setGrayFilter(View view) {
        Paint paint = new Paint();
        ColorMatrix cm = new ColorMatrix();
        cm.setSaturation(0);//????
        paint.setColorFilter(new ColorMatrixColorFilter(cm));
        view.setLayerType(View.LAYER_TYPE_HARDWARE, paint);
    }

    /**
     * 移除滤镜
     *
     * @param view
     */
    public static void removeFilter(View view) {
        Paint paint = new Paint();
//        ColorMatrix cm = new ColorMatrix();
//        cm.setSaturation(0);//????
//        paint.setColorFilter(new ColorMatrixColorFilter(cm));
        view.setLayerType(View.LAYER_TYPE_HARDWARE, paint);
    }

    public static void setGrayFilter(Activity activity) {
        setGrayFilter(activity.getWindow().getDecorView());
    }

    public static void removeFilter(Activity activity) {
        removeFilter(activity.getWindow().getDecorView());
    }


}
