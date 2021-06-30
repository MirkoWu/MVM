package com.mirkowu.lib_widget;


import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;

public class BoldTextView extends AppCompatTextView {
    public static final float STYLE_BOLD = 2f;//粗
    public static final float STYLE_MEDIUM = 1f;//中等粗
    private float mBoldWith = STYLE_MEDIUM;//默认

    public BoldTextView(@NonNull Context context) {
        this(context, null);
    }

    public BoldTextView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BoldTextView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.BoldTextView);
        mBoldWith = array.getFloat(R.styleable.BoldTextView_boldStyle, STYLE_MEDIUM);
        array.recycle();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        Paint paint = getPaint();
        paint.setStrokeWidth(mBoldWith);
        paint.setStyle(Paint.Style.FILL_AND_STROKE);
        super.onDraw(canvas);
    }

    /**
     * 自定义粗度
     *
     * @param width
     */
    public void setBoldWith(float width) {
        this.mBoldWith = width;
        invalidate();
    }

    /**
     * 加粗
     */
    public void setBoldStyle() {
        setBoldWith(STYLE_BOLD);
    }

    /**
     * 中等
     */
    public void setMediumStyle() {
        setBoldWith(STYLE_MEDIUM);
    }

//    @Override
//    public boolean isFocused() {
//        if (getEllipsize() == TextUtils.TruncateAt.MARQUEE) {
//            return true;
//        } else {
//            return super.isFocused();
//        }
//    }
}
