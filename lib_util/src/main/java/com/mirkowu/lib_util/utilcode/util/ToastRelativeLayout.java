package com.mirkowu.lib_util.utilcode.util;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

public final class ToastRelativeLayout extends RelativeLayout {

    private static final int SPACING = UtilsBridge.dp2px(80);

    public ToastRelativeLayout(Context context) {
        super(context);
    }

    public ToastRelativeLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ToastRelativeLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthMaxSpec = MeasureSpec.makeMeasureSpec(UtilsBridge.getAppScreenWidth() - SPACING, MeasureSpec.AT_MOST);
        super.onMeasure(widthMaxSpec, heightMeasureSpec);
    }
}