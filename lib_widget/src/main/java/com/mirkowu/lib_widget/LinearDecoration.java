package com.mirkowu.lib_widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;

import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class LinearDecoration extends RecyclerView.ItemDecoration {
    private Paint mDividerPaint = new Paint();
    private DisplayMetrics mDisplayMetrics;
    private int mSpace;
    private int mEdgeSpace;
    private int mTopSpace;
    private int mBottomSpace;

    public LinearDecoration(Context context) {
        mDisplayMetrics = context.getResources().getDisplayMetrics();
        mDividerPaint.setColor(Color.TRANSPARENT);
    }

    /**
     * 设置间隔
     *
     * @param space 单位dp
     * @return
     */
    public LinearDecoration setSpace(float space) {
        mSpace = (int) (TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, space, mDisplayMetrics) + 0.5f);
        return this;
    }

    /**
     * 设置间隔颜色
     *
     * @param spaceColor 默认透明
     * @return
     */
    public LinearDecoration setSpaceColor(int spaceColor) {
        mDividerPaint.setColor(spaceColor);
        return this;
    }

    /**
     * 设置二端间隔 优先级EdgeSpace > TopSpace/BottomSpace 。一旦设置此属性则覆盖其他二端属性
     */
    public LinearDecoration setEdgeSpace(float edgeSpace) {
        mEdgeSpace = (int) (TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, edgeSpace, mDisplayMetrics) + 0.5f);
        return this;
    }

    public LinearDecoration setTopSpace(float edgeSpace) {
        mTopSpace = (int) (TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, edgeSpace, mDisplayMetrics) + 0.5f);
        return this;
    }

    public LinearDecoration setBottomSpace(float edgeSpace) {
        mBottomSpace = (int) (TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, edgeSpace, mDisplayMetrics) + 0.5f);
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
            final int left = parent.getPaddingLeft();
            final int right = parent.getWidth() - parent.getPaddingRight();

            final int childCount = parent.getChildCount();
            for (int i = 0; i < childCount; i++) {
                final View child = parent.getChildAt(i);
                final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child
                        .getLayoutParams();
                int tx = Math.round(ViewCompat.getTranslationX(child));
                int ty = Math.round(ViewCompat.getTranslationY(child));

                final int top = child.getBottom() + params.bottomMargin + ty;
                final int bottom = top + mSpace;
                c.drawRect(left, top, right, bottom, mDividerPaint);

                //顶部和底部
                if (i == 0) {

                    if (topSpace != 0) {
                        final int firstBottom = child.getTop();
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
            final int top = parent.getPaddingTop();
            final int bottom = parent.getHeight() - parent.getPaddingBottom();
            final int childCount = parent.getChildCount();
            for (int i = 0; i < childCount; i++) {
                final View child = parent.getChildAt(i);
                final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child
                        .getLayoutParams();
                int tx = Math.round(ViewCompat.getTranslationX(child));
                int ty = Math.round(ViewCompat.getTranslationY(child));


                final int left = child.getRight() + params.rightMargin + tx;
                final int right = left + mSpace;
                c.drawRect(left, top, right, bottom, mDividerPaint);

                //顶部和底部
                if (i == 0) {
                    if (topSpace != 0) {
                        final int firstRight = child.getLeft();
                        final int firstLeft = firstRight - topSpace;
                        c.drawRect(firstLeft, top, firstRight, bottom, mDividerPaint);
                    }
                } else if (i == childCount - 1) {
                    if (bottomSpace != 0) {
                        final int lastLeft = child.getRight() + params.rightMargin +
                                Math.round(ViewCompat.getTranslationX(child));
                        final int lastRight = lastLeft + bottomSpace;
                        c.drawRect(lastLeft, top, lastRight, bottom, mDividerPaint);
                    }
                }
            }
        }
    }
}
