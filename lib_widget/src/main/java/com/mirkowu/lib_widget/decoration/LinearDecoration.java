package com.mirkowu.lib_widget.decoration;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.View;

import androidx.annotation.ColorInt;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.mirkowu.lib_util.utilcode.util.SizeUtils;


/**
 * 仅对 LinearLayoutManager 样式的列表显示分割线
 */
public class LinearDecoration extends RecyclerView.ItemDecoration {
    private final Paint mDividerPaint = new Paint();
    private final Rect mBounds = new Rect();
    private int mSpace;
    private int mTopSpace;
    private int mBottomSpace;
    private int mLeftPadding;
    private int mRightPadding;

    public LinearDecoration(Context context) {
        mDividerPaint.setColor(Color.TRANSPARENT);
    }

    /**
     * 设置间隔  （此间隔不包含 头、尾 二个分割线，如果需要可以单独）
     *
     * @param space 单位dp
     * @return
     */
    public LinearDecoration setSpace(float space) {
        mSpace = SizeUtils.dp2px(space);
        return this;
    }

    /**
     * 设置间隔颜色
     *
     * @param spaceColor 默认透明
     * @return
     */
    public LinearDecoration setSpaceColor(@ColorInt int spaceColor) {
        mDividerPaint.setColor(spaceColor);
        return this;
    }

    /**
     * 设置起始端间隔
     *
     * @param edgeSpace 间隔，单位dp
     * @return
     */
    public LinearDecoration setTopSpace(float edgeSpace) {
        mTopSpace = SizeUtils.dp2px(edgeSpace);
        return this;
    }

    /**
     * 设置尾端间隔
     *
     * @param edgeSpace 间隔，单位dp
     * @return
     */
    public LinearDecoration setBottomSpace(float edgeSpace) {
        mBottomSpace = SizeUtils.dp2px(edgeSpace);
        return this;
    }

    /**
     * 设置头尾二端间隔 优先级EdgeSpace > TopSpace/BottomSpace 。一旦设置此属性则覆盖其他二端属性
     *
     * @param edgeSpace 间隔，单位dp
     */
    public LinearDecoration setEdgeSpace(float edgeSpace) {
        setTopSpace(edgeSpace);
        setBottomSpace(edgeSpace);
        return this;
    }

    /**
     * 设置左边间隔 仅支持LinearLayoutManager
     *
     * @param edgeSpace 间隔，单位dp
     * @return
     */
    public LinearDecoration setLeftPadding(float edgeSpace) {
        mLeftPadding = SizeUtils.dp2px(edgeSpace);
        return this;
    }

    /**
     * 设置右边间隔 仅支持LinearLayoutManager
     *
     * @param edgeSpace 间隔，单位dp
     * @return
     */
    public LinearDecoration setRightPadding(float edgeSpace) {
        mRightPadding = SizeUtils.dp2px(edgeSpace);
        return this;
    }

    /**
     * 设置分割线缩进
     *
     * @param edgeSpace
     * @return
     */
    public LinearDecoration setPadding(float edgeSpace) {
        setLeftPadding(edgeSpace);
        setRightPadding(edgeSpace);
        return this;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        final RecyclerView.LayoutManager manager = parent.getLayoutManager();
        final int childPosition = parent.getChildAdapterPosition(view);
        final int itemCount = parent.getAdapter().getItemCount();
        if (manager != null) {
            if (manager instanceof LinearLayoutManager) {
                setLinear(((LinearLayoutManager) manager).getOrientation(), outRect, childPosition, itemCount);
            }
        }
    }

    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
        final RecyclerView.LayoutManager manager = parent.getLayoutManager();
        if (manager != null) {
            if (manager instanceof LinearLayoutManager) {
                drawLinear(((LinearLayoutManager) manager).getOrientation(), c, parent);
            }
        }
    }

    private void setLinear(int orientation, Rect outRect, int childPosition, int itemCount) {
        if (orientation == LinearLayoutManager.VERTICAL) {
            if (childPosition == 0) {
                outRect.set(0, mTopSpace, 0, mSpace);
            } else if (childPosition == itemCount - 1) {
                outRect.set(0, 0, 0, mBottomSpace);
            } else {
                outRect.set(0, 0, 0, mSpace);
            }
        } else if (orientation == LinearLayoutManager.HORIZONTAL) {
            if (childPosition == 0) {
                outRect.set(mTopSpace, 0, mSpace, 0);
            } else if (childPosition == itemCount - 1) {
                outRect.set(0, 0, mBottomSpace, 0);
            } else {
                outRect.set(0, 0, mSpace, 0);
            }
        }
    }


    private void drawLinear(int orientation, Canvas canvas, RecyclerView parent) {
        if (orientation == LinearLayoutManager.VERTICAL) {
            canvas.save();
            final int left;
            final int right;
            //裁剪绘制区域
            if (parent.getClipToPadding()) {
                left = parent.getPaddingLeft();
                right = parent.getWidth() - parent.getPaddingRight();
                canvas.clipRect(left, parent.getPaddingTop(), right,
                        parent.getHeight() - parent.getPaddingBottom());
            } else {
                left = 0;
                right = parent.getWidth();
            }

            final int childCount = parent.getChildCount();
            for (int i = 0; i < childCount; i++) {
                final View child = parent.getChildAt(i);
                int tx = Math.round(child.getTranslationX());
                int ty = Math.round(child.getTranslationY());
                parent.getDecoratedBoundsWithMargins(child, mBounds);

                final int bottom = mBounds.bottom + ty;
                final int top = bottom - mSpace;
                final int finalLeft = left + mLeftPadding + tx;
                final int finalRight = right - mRightPadding + tx;

                if (i != childCount - 1) {
                    canvas.drawRect(finalLeft, top, finalRight, bottom, mDividerPaint);
                }

                //顶部和底部
                if (i == 0) {
                    if (mTopSpace != 0) {
                        final int firstTop = mBounds.top + ty;
                        final int firstBottom = firstTop + mTopSpace;
                        canvas.drawRect(finalLeft, firstTop, finalRight, firstBottom, mDividerPaint);
                    }
                } else if (i == childCount - 1) {
                    if (mBottomSpace != 0) {
                        final int lastTop = bottom - mBottomSpace;
                        canvas.drawRect(finalLeft, lastTop, finalRight, bottom, mDividerPaint);
                    }
                }
            }
            canvas.restore();
        } else if (orientation == LinearLayoutManager.HORIZONTAL) {
            canvas.save();
            final int top;
            final int bottom;
            //裁剪绘制区域
            if (parent.getClipToPadding()) {
                top = parent.getPaddingTop();
                bottom = parent.getHeight() - parent.getPaddingBottom();
                canvas.clipRect(parent.getPaddingLeft(), top,
                        parent.getWidth() - parent.getPaddingRight(), bottom);
            } else {
                top = 0;
                bottom = parent.getHeight();
            }

            final int childCount = parent.getChildCount();
            for (int i = 0; i < childCount; i++) {
                final View child = parent.getChildAt(i);
                int tx = Math.round(child.getTranslationX());
                int ty = Math.round(child.getTranslationY());
                parent.getDecoratedBoundsWithMargins(child, mBounds);

                final int right = mBounds.right + tx;
                final int left = right - mSpace;
                final int finalTop = top + mRightPadding + ty;
                final int finalBottom = bottom - mLeftPadding + ty;

                if (i != childCount - 1) {
                    canvas.drawRect(left, finalTop, right, finalBottom, mDividerPaint);
                }

                //顶部和底部
                if (i == 0) {
                    if (mTopSpace != 0) {
                        final int firstLeft = mBounds.left + tx;
                        final int firstRight = firstLeft + mTopSpace;
                        canvas.drawRect(firstLeft, finalTop, firstRight, finalBottom, mDividerPaint);
                    }
                } else if (i == childCount - 1) {
                    if (mBottomSpace != 0) {
                        final int lastLeft = right - mBottomSpace;
                        canvas.drawRect(lastLeft, finalTop, right, finalBottom, mDividerPaint);
                    }
                }
            }
            canvas.restore();
        }
    }
}
