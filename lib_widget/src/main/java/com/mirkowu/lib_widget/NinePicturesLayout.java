package com.mirkowu.lib_widget;

import android.content.Context;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.SparseArray;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.mirkowu.lib_util.LogUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * 九宫格图片
 * 至尊流畅
 */
public class NinePicturesLayout extends FrameLayout implements View.OnClickListener {

    public static final int MAX_DISPLAY_COUNT = 9;
    private final LayoutParams lpChildImage = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
    private final int mSingleMaxSize;
    private final int mSpace;
    private final List<ImageView> iPictureList = new ArrayList<>();
    private final SparseArray<ImageView> mVisiblePictureList = new SparseArray<>();
    private final TextView tOverflowCount;

    private OnItemClickListener mOnItemClickListener;
    private boolean isInit = true;
    private List<?> mDataList;

    public NinePicturesLayout(@NonNull Context context) {
        this(context, null);
    }

    public NinePicturesLayout(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public NinePicturesLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        DisplayMetrics mDisplayMetrics = context.getResources().getDisplayMetrics();
        mSingleMaxSize = (int) (TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 216, mDisplayMetrics) + 0.5f);
        mSpace = (int) (TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 2, mDisplayMetrics) + 0.5f);

        for (int i = 0; i < MAX_DISPLAY_COUNT; i++) {
            ImageView squareImageView = new SquareImageView(context);
            squareImageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            squareImageView.setVisibility(View.GONE);
            squareImageView.setOnClickListener(this);
            addView(squareImageView);
            iPictureList.add(squareImageView);
        }

        tOverflowCount = new TextView(context);
        tOverflowCount.setTextColor(0xFFFFFFFF);
        tOverflowCount.setTextSize(24f);
        tOverflowCount.setGravity(Gravity.CENTER);
        tOverflowCount.setBackgroundColor(0x66000000);
        tOverflowCount.setVisibility(View.GONE);
        addView(tOverflowCount);
    }

    public void setData(List<?> urlList) {
        mDataList = urlList;
        if (isInit) {
            notifyDataChanged();
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        isInit = true;
        notifyDataChanged();
    }

    private void notifyDataChanged() {
        final List<?> thumbList = mDataList;
        final int urlListSize = thumbList != null ? mDataList.size() : 0;

        if (thumbList == null || thumbList.size() < 1) {
            setVisibility(View.GONE);
            return;
        } else {
            setVisibility(View.VISIBLE);
        }

//        if (thumbList.size() > mDataList.size()) {
//            throw new IllegalArgumentException("dataList.size(" + mDataList.size() + ") > thumbDataList.size(" + thumbList.size() + ")");
//        }

        int column = 3;
        if (urlListSize == 1) {
            column = 1;
        } else if (urlListSize == 4) {
            column = 2;
        }

        int row = 0;
        if (urlListSize > 6) {
            row = 3;
        } else if (urlListSize > 3) {
            row = 2;
        } else if (urlListSize > 0) {
            row = 1;
        }

        final int imageSize = urlListSize == 1 ? mSingleMaxSize :
                (int) ((getWidth() * 1f - mSpace * (column - 1)) / column);

        lpChildImage.width = imageSize;
        lpChildImage.height = lpChildImage.width;

        tOverflowCount.setVisibility(urlListSize > MAX_DISPLAY_COUNT ? View.VISIBLE : View.GONE);
        tOverflowCount.setText("+ " + (urlListSize - MAX_DISPLAY_COUNT));
        tOverflowCount.setLayoutParams(lpChildImage);

        mVisiblePictureList.clear();
        for (int i = 0; i < iPictureList.size(); i++) {
            final ImageView iPicture = iPictureList.get(i);
            if (i < urlListSize) {
                iPicture.setVisibility(View.VISIBLE);
                mVisiblePictureList.put(i, iPicture);
                iPicture.setLayoutParams(lpChildImage);
                loadPictures(iPicture, thumbList.get(i));

                iPicture.setTranslationX((i % column) * (imageSize + mSpace));
                iPicture.setTranslationY((i / column) * (imageSize + mSpace));
            } else {
                iPicture.setVisibility(View.GONE);
            }

            if (i == MAX_DISPLAY_COUNT - 1) {
                tOverflowCount.setTranslationX((i % column) * (imageSize + mSpace));
                tOverflowCount.setTranslationY((i / column) * (imageSize + mSpace));
            }
        }
        getLayoutParams().height = imageSize * row + mSpace * (row - 1);
    }

    /**
     * 加载图片 ，需要重写
     *
     * @param iPicture
     * @param url
     */
    private void loadPictures(ImageView iPicture, Object url) {
        if (mImageLoader != null) {
            mImageLoader.load(iPicture, url);
        } else {
            LogUtil.e("NinePicturesLayout：IImageLoader is null !!!");
        }
    }

    @Override
    public void onClick(View v) {
        if (mOnItemClickListener != null) {
            int index = indexOfChild(v);
            mOnItemClickListener.onItemClick((ImageView) v, index);
        }
    }

    public interface OnItemClickListener {
        void onItemClick(ImageView i, int index);
    }

    public void setOnItemClickListener(NinePicturesLayout.OnItemClickListener listener) {
        mOnItemClickListener = listener;
    }


    private IImageLoader mImageLoader;

    public void setImageLoader(IImageLoader imageLoader) {
        mImageLoader = imageLoader;
    }

    public interface IImageLoader {
        void load(ImageView view, Object url);
    }
}
