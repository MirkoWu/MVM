package com.mirkowu.lib_photo.view;

import android.content.Context;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatImageView;

/**
 * An image view which always remains square with respect to its width.
 */
public class SquaredImageView extends AppCompatImageView {
    public SquaredImageView(Context context) {
        super(context);
    }

    public SquaredImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    boolean isSquare = true;

    public void setSquare(boolean isSquare) {
        this.isSquare = isSquare;
       // requestLayout();
    }

    public boolean isSquare() {
        return isSquare;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (isSquare) {
            setMeasuredDimension(getMeasuredWidth(), getMeasuredWidth());
        }
    }
}
