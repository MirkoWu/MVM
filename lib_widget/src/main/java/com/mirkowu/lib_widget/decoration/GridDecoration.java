package com.mirkowu.lib_widget.decoration;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.View;

import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.mirkowu.lib_util.utilcode.util.SizeUtils;


/**
 * 仅对 GridLayoutManager 样式的列表显示分割线
 */
public class GridDecoration extends RecyclerView.ItemDecoration {
    private Paint mDividerPaint = new Paint();
    private int mSpace;
    private int mEdgeSpace;
    private int mTopSpace;
    private int mBottomSpace;

    public GridDecoration(Context context) {
        mDividerPaint.setColor(Color.TRANSPARENT);
    }

    /**
     * 设置间隔  （此间隔不包含 头、尾 二个分割线，如果需要可以单独）
     *
     * @param space 单位dp
     * @return
     */
    public GridDecoration setSpace(float space) {
        mSpace = SizeUtils.dp2px(space);
        return this;
    }

    /**
     * 设置间隔颜色
     *
     * @param spaceColor 默认透明
     * @return
     */
    public GridDecoration setSpaceColor(int spaceColor) {
        mDividerPaint.setColor(spaceColor);
        return this;
    }

    /**
     * 设置头尾二端间隔 优先级EdgeSpace > TopSpace/BottomSpace 。一旦设置此属性则覆盖其他二端属性
     *
     * @param edgeSpace 间隔，单位dp
     */
    public GridDecoration setEdgeSpace(float edgeSpace) {
        mEdgeSpace = SizeUtils.dp2px(edgeSpace);
        return this;
    }

    /**
     * 设置起始端间隔
     *
     * @param edgeSpace 间隔，单位dp
     * @return
     */
    public GridDecoration setTopSpace(float edgeSpace) {
        mTopSpace = SizeUtils.dp2px(edgeSpace);
        return this;
    }

    /**
     * 设置尾端间隔
     *
     * @param edgeSpace 间隔，单位dp
     * @return
     */
    public GridDecoration setBottomSpace(float edgeSpace) {
        mBottomSpace = SizeUtils.dp2px(edgeSpace);
        return this;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        final RecyclerView.LayoutManager manager = parent.getLayoutManager();
        final int childPosition = parent.getChildAdapterPosition(view);
        final int itemCount = parent.getAdapter().getItemCount();
        if (manager != null) {
            if (manager instanceof GridLayoutManager) {
                GridLayoutManager gridLayoutManager = (GridLayoutManager) manager;
                setGrid(gridLayoutManager.getOrientation(), gridLayoutManager.getSpanCount(), outRect, childPosition, itemCount);
            }
        }
    }

    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
        final RecyclerView.LayoutManager manager = parent.getLayoutManager();
        if (manager != null) {
            if (manager instanceof GridLayoutManager) {
                GridLayoutManager gridLayoutManager = (GridLayoutManager) manager;
                drawGrid(gridLayoutManager.getOrientation(), gridLayoutManager.getSpanCount(), c, parent);
            }
        }
    }

    private void setGrid(int orientation, int spanCount, Rect outRect, int childPosition, int itemCount) {
        float totalSpace = mSpace * (spanCount - 1) /*+ mEdgeSpace * 2*/;
        float eachSpace = totalSpace / spanCount;
        int column = childPosition % spanCount;
        int row = childPosition / spanCount;

        float left;
        float right;
        float top;
        float bottom;

        //最后一行（未充满和充满状态）
        boolean isLastRow = (itemCount % spanCount != 0 && itemCount / spanCount == row) ||
                (itemCount % spanCount == 0 && itemCount / spanCount == row + 1);
        final int topSpace = mEdgeSpace != 0 ? mEdgeSpace : mTopSpace;
        final int bottomSpace = mEdgeSpace != 0 ? mEdgeSpace : mBottomSpace;

        if (orientation == GridLayoutManager.VERTICAL) {
            top = 0;
            bottom = mSpace;

            //第一排 顶部
            if (childPosition < spanCount) {
                top = topSpace;
            }
            //最后一排 底部
            if (isLastRow) {
                bottom = bottomSpace;
            }

            if (spanCount == 1) {
                left = 0;
                right = 0;
            } else {

                left = column * (eachSpace /*- mEdgeSpace *2*/) / (spanCount - 1) /*+ mEdgeSpace*/;
                right = eachSpace - left;
//                //取消最二边的间隔
//                if (column == 0) {
//                    left = 0;
//                } else if (column == spanCount - 1) {
//                    right = 0;
//                }
            }
        } else {
            left = 0;
            right = mSpace;

            if (childPosition < spanCount) {
                left = topSpace;
            }
            if (isLastRow) {
                right = bottomSpace;
            }

            if (spanCount == 1) {
                top = 0;
                bottom = 0;
            } else {
                top = column * (eachSpace /*- mEdgeSpace *2*/) / (spanCount - 1) /*+ mEdgeSpace*/;
                bottom = eachSpace - top;
//                //取消最二边的间隔
//                if (column == 0) {
//                    top = 0;
//                } else if (column == spanCount - 1) {
//                    bottom = 0;
//                }
            }
        }
        outRect.set((int) left, (int) top, (int) right, (int) bottom);
    }

    private void drawGrid(int orientation, int spanCount, Canvas c, RecyclerView parent) {

        final int topSpace = mEdgeSpace != 0 ? mEdgeSpace : mTopSpace;
        final int bottomSpace = mEdgeSpace != 0 ? mEdgeSpace : mBottomSpace;
        final int childCount = parent.getChildCount();
        int totalRow = childCount / spanCount + (childCount % spanCount == 0 ? 0 : 1);
        //最后一行起点下标
        int rowNum = (totalRow - 1) * spanCount;
        if (orientation == GridLayoutManager.VERTICAL) {

            for (int i = 0; i < childCount; i++) {
                final View child = parent.getChildAt(i);
                final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child
                        .getLayoutParams();
                int tx = Math.round(ViewCompat.getTranslationX(child));
                int ty = Math.round(ViewCompat.getTranslationY(child));

                //横线
                int top = child.getBottom() + ty + params.bottomMargin;
                int bottom = top + mSpace;
                int left = child.getLeft() + tx - params.leftMargin;
                int right = child.getRight() + tx + params.rightMargin;
                if (i < rowNum) {
                    c.drawRect(left, top, right, bottom, mDividerPaint);
                }

                //顶部和尾部
                if (i == 0) {
                    if (topSpace != 0) {
                        left = parent.getPaddingLeft() + tx;
                        right = parent.getWidth() - parent.getPaddingRight() + tx;
                        top = child.getTop() - params.topMargin + ty;
                        bottom = top - topSpace;
                        c.drawRect(left, top, right, bottom, mDividerPaint);
                    }
                } else if (i == childCount - 1) {
                    if (bottomSpace != 0) {
                        left = parent.getPaddingLeft() + tx;
                        right = parent.getWidth() - parent.getPaddingRight() + tx;
                        top = child.getBottom() + params.bottomMargin + ty;
                        bottom = top + bottomSpace;
                        c.drawRect(left, top, right, bottom, mDividerPaint);
                    }
                }

                //竖线
                top = child.getTop() + ty - params.topMargin;
                if (i < rowNum) {
                    bottom = child.getBottom() + mSpace + ty + params.bottomMargin;
                } else {
                    bottom = child.getBottom() + ty + params.bottomMargin;
                }
                left = child.getRight() + tx + params.rightMargin;
                right = left + mSpace;
                c.drawRect(left, top, right, bottom, mDividerPaint);
            }
        } else {
            for (int i = 0; i < childCount; i++) {
                final View child = parent.getChildAt(i);
                final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child
                        .getLayoutParams();
                int tx = Math.round(ViewCompat.getTranslationX(child));
                int ty = Math.round(ViewCompat.getTranslationY(child));


                //竖线
                int left = child.getRight() + ty + params.rightMargin;
                int right = left + mSpace;
                int top = child.getTop() + tx - params.topMargin;
                int bottom = child.getBottom() + tx + params.rightMargin;
                if (i < rowNum) {
                    c.drawRect(left, top, right, bottom, mDividerPaint);
                }

                //顶部和尾部
                if (i == 0) {
                    if (topSpace != 0) {
                        top = parent.getPaddingTop() + tx;
                        bottom = parent.getHeight() - parent.getPaddingBottom() + tx;
                        left = child.getLeft() - params.leftMargin + ty;
                        right = left - topSpace;
                        c.drawRect(left, top, right, bottom, mDividerPaint);
                    }
                } else if (i == childCount - 1) {
                    if (bottomSpace != 0) {
                        top = parent.getPaddingTop() + tx;
                        bottom = parent.getHeight() - parent.getPaddingBottom() + tx;
                        left = child.getRight() + params.rightMargin + ty;
                        right = left + bottomSpace;
                        c.drawRect(left, top, right, bottom, mDividerPaint);
                    }
                }

                //横线
                left = child.getLeft() + ty - params.leftMargin;
                if (i < rowNum) {
                    right = child.getRight() + mSpace + ty + params.rightMargin;
                } else {
                    right = child.getRight() + ty + params.rightMargin;
                }
                top = child.getBottom() + tx + params.bottomMargin;
                bottom = top + mSpace;
                c.drawRect(left, top, right, bottom, mDividerPaint);


            }
        }
    }
}
