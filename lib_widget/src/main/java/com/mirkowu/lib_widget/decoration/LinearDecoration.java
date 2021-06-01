package com.mirkowu.lib_widget.decoration;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.View;

import androidx.annotation.ColorInt;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.mirkowu.lib_util.utilcode.util.SizeUtils;


/**
 * 仅对 LinearLayoutManager 样式的列表显示分割线
 */
public class LinearDecoration extends RecyclerView.ItemDecoration {
    private Paint mDividerPaint = new Paint();
    private int mSpace;
    private int mEdgeSpace;
    private int mTopSpace;
    private int mBottomSpace;

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
     * 设置头尾二端间隔 优先级EdgeSpace > TopSpace/BottomSpace 。一旦设置此属性则覆盖其他二端属性
     *
     * @param edgeSpace 间隔，单位dp
     */
    public LinearDecoration setEdgeSpace(float edgeSpace) {
        mEdgeSpace = SizeUtils.dp2px(edgeSpace);
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
                outRect.set(0, mEdgeSpace != 0 ? mEdgeSpace : mTopSpace, 0, mSpace);
            } else if (childPosition == itemCount - 1) {
                outRect.set(0, 0, 0, mEdgeSpace != 0 ? mEdgeSpace : mBottomSpace);
            } else {
                outRect.set(0, 0, 0, mSpace);
            }
        } else if (orientation == LinearLayoutManager.HORIZONTAL) {
            if (childPosition == 0) {
                outRect.set(mEdgeSpace != 0 ? mEdgeSpace : mTopSpace, 0, mSpace, 0);
            } else if (childPosition == itemCount - 1) {
                outRect.set(0, 0, mEdgeSpace != 0 ? mEdgeSpace : mBottomSpace, 0);
            } else {
                outRect.set(0, 0, mSpace, 0);
            }
        }
    }

    private void drawLinear(int orientation, Canvas c, RecyclerView parent) {
        final int topSpace = mEdgeSpace != 0 ? mEdgeSpace : mTopSpace;
        final int bottomSpace = mEdgeSpace != 0 ? mEdgeSpace : mBottomSpace;

        if (orientation == LinearLayoutManager.VERTICAL) {
            final int childCount = parent.getChildCount();
            for (int i = 0; i < childCount; i++) {
                final View child = parent.getChildAt(i);
                final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child
                        .getLayoutParams();
                int tx = Math.round(ViewCompat.getTranslationX(child));
                int ty = Math.round(ViewCompat.getTranslationY(child));
                final int left = parent.getPaddingLeft() + tx;
                final int right = parent.getWidth() - parent.getPaddingRight() + tx;

                final int top = child.getBottom() + params.bottomMargin + ty;
                final int bottom = top + mSpace;
                if (i != childCount - 1) {
                    c.drawRect(left, top, right, bottom, mDividerPaint);
                }

                //顶部和底部
                if (i == 0) {
                    if (topSpace != 0) {
                        final int firstBottom = child.getTop() - params.topMargin + ty;
                        final int firstTop = firstBottom - topSpace;
                        c.drawRect(left, firstTop, right, firstBottom, mDividerPaint);
                    }
                } else if (i == childCount - 1) {
                    if (bottomSpace != 0) {
                        final int lastTop = child.getBottom() + params.bottomMargin + ty;
                        final int lastBottom = lastTop + bottomSpace;
                        c.drawRect(left, lastTop, right, lastBottom, mDividerPaint);
                    }
                }
            }

        } else if (orientation == LinearLayoutManager.HORIZONTAL) {

            final int childCount = parent.getChildCount();
            for (int i = 0; i < childCount; i++) {
                final View child = parent.getChildAt(i);
                final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child
                        .getLayoutParams();
                int tx = Math.round(ViewCompat.getTranslationX(child));
                int ty = Math.round(ViewCompat.getTranslationY(child));
                final int top = parent.getPaddingTop() + ty;
                final int bottom = parent.getHeight() - parent.getPaddingBottom() + ty;

                final int left = child.getRight() + params.rightMargin + tx;
                final int right = left + mSpace;
                if (i != childCount - 1) {
                    c.drawRect(left, top, right, bottom, mDividerPaint);
                }
                //顶部和底部
                if (i == 0) {
                    if (topSpace != 0) {
                        final int firstRight = child.getLeft() - params.leftMargin + tx;
                        final int firstLeft = firstRight - topSpace;
                        c.drawRect(firstLeft, top, firstRight, bottom, mDividerPaint);
                    }
                } else if (i == childCount - 1) {
                    if (bottomSpace != 0) {
                        final int lastLeft = child.getRight() + params.rightMargin + tx;
                        final int lastRight = lastLeft + bottomSpace;
                        c.drawRect(lastLeft, top, lastRight, bottom, mDividerPaint);
                    }
                }
            }
        }
    }
}
