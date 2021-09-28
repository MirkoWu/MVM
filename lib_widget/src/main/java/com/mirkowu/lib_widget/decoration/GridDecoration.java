package com.mirkowu.lib_widget.decoration;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.View;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.mirkowu.lib_util.utilcode.util.SizeUtils;


/**
 * 仅对 GridLayoutManager 样式的列表显示分割线
 */
public class GridDecoration extends RecyclerView.ItemDecoration {
    private final Paint mDividerPaint = new Paint();
    private final Rect mBounds = new Rect();
    private int mSpace;
    private int mTopSpace;
    private int mBottomSpace;
    private int mSideSpace; //二边的间隔

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

    /**
     * 设置头尾二端间隔 优先级EdgeSpace > TopSpace/BottomSpace 。一旦设置此属性则覆盖其他二端属性
     *
     * @param edgeSpace 间隔，单位dp
     */
    public GridDecoration setEdgeSpace(float edgeSpace) {
        setTopSpace(edgeSpace);
        setBottomSpace(edgeSpace);
        return this;
    }

//    /**
//     * 设置左右二边的分割线
//     *
//     * @param edgeSpace
//     * @return
//     */
//    public GridDecoration setSideSpace(float edgeSpace) {
//        mSideSpace = SizeUtils.dp2px(edgeSpace);
//        return this;
//    }

//    /**
//     * 设置四边的分割线
//     *
//     * @param edgeSpace
//     * @return
//     */
//    public GridDecoration setRectSpace(float edgeSpace) {
//        setEdgeSpace(edgeSpace);
//        setSideSpace(edgeSpace);
//        return this;
//    }


    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        final RecyclerView.LayoutManager manager = parent.getLayoutManager();
        final int childPosition = parent.getChildAdapterPosition(view);
        final int itemCount = parent.getAdapter().getItemCount();
        if (manager != null) {
            if (manager instanceof GridLayoutManager) {
                GridLayoutManager gridLayoutManager = (GridLayoutManager) manager;
                setGrid(gridLayoutManager.getOrientation(), outRect, gridLayoutManager.getSpanCount(), childPosition, itemCount);
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

    private boolean isFirstRow(int spanCount, int childPosition, int itemCount) {
        return childPosition < spanCount;
    }

    private boolean isLastRow(int spanCount, int childPosition, int itemCount) {
        int totalRow = itemCount / spanCount + (itemCount % spanCount > 0 ? 1 : 0);
        return childPosition >= spanCount * (totalRow - 1);
    }

    private boolean isFirstColumn(int spanCount, int childPosition, int itemCount) {
        return childPosition % spanCount == 0;
    }

    private boolean isLastColumn(int spanCount, int childPosition, int itemCount) {
        return childPosition % spanCount == spanCount - 1;
    }

    private void setGrid(int orientation, Rect outRect, int spanCount, int childPosition, int itemCount) {
        float eachSpace = 1f * mSpace * (spanCount - 1) / spanCount;
        int column = childPosition % spanCount;
        float left = 0;
        float right;
        float top = 0;
        float bottom;

        if (orientation == GridLayoutManager.VERTICAL) {
            left = 1f * column * mSpace / spanCount;
            right = eachSpace - left;
            bottom = mSpace;

            if (isFirstRow(spanCount, childPosition, itemCount)) {
                top = mTopSpace;
            }
            if (isLastRow(spanCount, childPosition, itemCount)) {
                bottom = mBottomSpace;
            }
//            if (isFirstColumn(spanCount, childPosition, itemCount)) {
//                left = mSideSpace;
//            }
//            if (isLastColumn(spanCount, childPosition, itemCount)) {
//                right = mSideSpace;
//            }
        } else {
            top = 1f * column * mSpace / spanCount;
            bottom = eachSpace - top;
            right = mSpace;

            if (isFirstRow(spanCount, childPosition, itemCount)) {
                left = mTopSpace;
            }
            if (isLastRow(spanCount, childPosition, itemCount)) {
                right = mBottomSpace;
            }
//            if (isFirstColumn(spanCount, childPosition, itemCount)) {
//                top = mSideSpace;
//            }
//            if (isLastColumn(spanCount, childPosition, itemCount)) {
//                bottom = mSideSpace;
//            }
        }
        outRect.set((int) left, (int) top, (int) right, (int) bottom);
    }

    private void drawGrid(int orientation, int spanCount, Canvas canvas, RecyclerView parent) {
        final int topSpace = mTopSpace;
        final int bottomSpace = mBottomSpace;
        final int childCount = parent.getChildCount();
        int totalRow = childCount / spanCount + (childCount % spanCount == 0 ? 0 : 1);
        //最后一行起点下标
        int rowNum = (totalRow - 1) * spanCount;
        if (orientation == GridLayoutManager.VERTICAL) {
            canvas.save();
            //裁剪绘制区域
            if (parent.getClipToPadding()) {
                canvas.clipRect(parent.getPaddingLeft(), parent.getPaddingTop(),
                        parent.getWidth() - parent.getPaddingRight(),
                        parent.getHeight() - parent.getPaddingBottom());
            }

            for (int i = 0; i < childCount; i++) {
                final View child = parent.getChildAt(i);
                int tx = Math.round(child.getTranslationX());
                int ty = Math.round(child.getTranslationY());
                parent.getDecoratedBoundsWithMargins(child, mBounds);
                final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child
                        .getLayoutParams();

                //横线
                int bottom = mBounds.bottom + ty;
                int top = bottom - mSpace;
                int left = mBounds.left + tx;
                int right = mBounds.right + tx;
                if (i < rowNum) {
                    canvas.drawRect(left, top, right, bottom, mDividerPaint);
                }

                //顶部和尾部
                if (i == 0) {
                    left = parent.getPaddingLeft() + tx;
                    right = parent.getWidth() - parent.getPaddingRight() + tx;
                    bottom = child.getTop() - params.topMargin + ty;
                    top = bottom - topSpace;
                    canvas.drawRect(left, top, right, bottom, mDividerPaint);
                } else if (i == childCount - 1) {
                    left = parent.getPaddingLeft() + tx;
                    right = parent.getWidth() - parent.getPaddingRight() + tx;
                    top = child.getBottom() + params.bottomMargin + ty;
                    bottom = top + bottomSpace;
                    canvas.drawRect(left, top, right, bottom, mDividerPaint);
                }

                //竖线
                top = child.getTop() + params.topMargin + ty;
                bottom = child.getBottom() + params.bottomMargin + ty;
                left = child.getRight() + params.rightMargin + tx;
                right = left + mSpace;
                canvas.drawRect(left, top, right, bottom, mDividerPaint);
            }
            canvas.restore();
        } else {
            canvas.save();
            //裁剪绘制区域
            if (parent.getClipToPadding()) {
                canvas.clipRect(parent.getPaddingLeft(), parent.getPaddingTop(),
                        parent.getWidth() - parent.getPaddingRight(),
                        parent.getHeight() - parent.getPaddingBottom());
            }

            for (int i = 0; i < childCount; i++) {
                final View child = parent.getChildAt(i);
                final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child
                        .getLayoutParams();
                int tx = Math.round(child.getTranslationX());
                int ty = Math.round(child.getTranslationY());
                parent.getDecoratedBoundsWithMargins(child, mBounds);

                //竖线
                int right = mBounds.right + ty;
                int left = right - mSpace;
                int bottom = mBounds.bottom + tx;
                int top = mBounds.top + tx;
                if (i < rowNum) {
                    canvas.drawRect(left, top, right, bottom, mDividerPaint);
                }

                //顶部和尾部
                if (i == 0) {
                    top = parent.getPaddingTop() + tx;
                    bottom = parent.getHeight() - parent.getPaddingBottom() + tx;
                    right = child.getLeft() - params.leftMargin + ty;
                    left = right - topSpace;
                    canvas.drawRect(left, top, right, bottom, mDividerPaint);
                } else if (i == childCount - 1) {
                    top = parent.getPaddingTop() + tx;
                    bottom = parent.getHeight() - parent.getPaddingBottom() + tx;
                    left = child.getRight() + params.rightMargin + ty;
                    right = left + bottomSpace;
                    canvas.drawRect(left, top, right, bottom, mDividerPaint);
                }

                //横线
                left = child.getLeft() - params.leftMargin + ty;
                right = child.getRight() + params.rightMargin + ty;
                top = child.getBottom() + params.bottomMargin + tx;
                bottom = top + mSpace;
                canvas.drawRect(left, top, right, bottom, mDividerPaint);
            }
            canvas.restore();
        }
    }
}
