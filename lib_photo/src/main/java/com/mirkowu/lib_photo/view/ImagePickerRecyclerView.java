package com.mirkowu.lib_photo.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.annotation.DrawableRes;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.mirkowu.lib_photo.ImagePicker;
import com.mirkowu.lib_photo.R;
import com.mirkowu.lib_util.utilcode.util.ScreenUtils;
import com.mirkowu.lib_util.utilcode.util.SizeUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by MirkoWu on 2017/4/6 0006.
 */

public class ImagePickerRecyclerView extends RecyclerView {
    private static final String TAG = "ImagePickerRecyclerView";
    private int mMaxCount = 9;
    private int mGridWidth;
    private int mSpanCount;
    private int mSpacing;//间隔
    private boolean mShowDelete;
    private boolean mShowAddImage;
    private Drawable mAddImageSrc;
    private Drawable mDeleteImageSrc;
    private ImagePickedAdapter mAdapter;
    private MediaGridDivider gridDivider;
    private ImageView.ScaleType mScaleType = ImageView.ScaleType.CENTER_CROP;

    public static final int ITEM_SHAPE_SQUARE = 0;//正方形
    public static final int ITEM_SHAPE_SIZE = 1;//指定宽高
    private int mItemShape;
    private int mItemWidth;
    private int mItemHeight;
    private FrameLayout.LayoutParams mItemLayoutParams;

    private static final ImageView.ScaleType[] sScaleTypeArray = new ImageView.ScaleType[]{
            ImageView.ScaleType.MATRIX,
            ImageView.ScaleType.FIT_XY,
            ImageView.ScaleType.FIT_START,
            ImageView.ScaleType.FIT_CENTER,
            ImageView.ScaleType.FIT_END,
            ImageView.ScaleType.CENTER,
            ImageView.ScaleType.CENTER_CROP,
            ImageView.ScaleType.CENTER_INSIDE
    };

    public ImagePickerRecyclerView(Context context) {
        this(context, null);
    }

    public ImagePickerRecyclerView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ImagePickerRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ImagePickerRecyclerView, defStyle, 0);
        mMaxCount = a.getInteger(R.styleable.ImagePickerRecyclerView_ivp_maxCount, 9);
        mSpanCount = a.getInteger(R.styleable.ImagePickerRecyclerView_ivp_spanCount, 4);
        mSpacing = a.getDimensionPixelSize(R.styleable.ImagePickerRecyclerView_ivp_spacing, SizeUtils.dp2px(2));
        mAddImageSrc = a.getDrawable(R.styleable.ImagePickerRecyclerView_ivp_addImageSrc);
        mDeleteImageSrc = a.getDrawable(R.styleable.ImagePickerRecyclerView_ivp_deleteImageSrc);
        mShowDelete = a.getBoolean(R.styleable.ImagePickerRecyclerView_ivp_showDelete, true);
        mShowAddImage = a.getBoolean(R.styleable.ImagePickerRecyclerView_ivp_showAddImage, true);
        final int index = a.getInteger(R.styleable.ImagePickerRecyclerView_ivp_imageScaleType, 6);
        mItemShape = a.getInteger(R.styleable.ImagePickerRecyclerView_ivp_itemShape, ITEM_SHAPE_SQUARE);
        a.recycle();

        if (index >= 0) {
            mScaleType = (sScaleTypeArray[index]);
        }

        if (mShowAddImage && mAddImageSrc == null) {
            mAddImageSrc = ContextCompat.getDrawable(context, R.drawable.ivp_addimage);
        }
        if (mShowDelete && mDeleteImageSrc == null) {
            mDeleteImageSrc = ContextCompat.getDrawable(context, R.drawable.ivp_delete);
        }


        init(context);
    }

    private void init(Context context) {
        int width = ScreenUtils.getScreenWidth();
        mGridWidth = (int) (width * 1f / mSpanCount);

        if (mItemShape == ITEM_SHAPE_SQUARE) {
            mItemLayoutParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        } else if (mItemShape == ITEM_SHAPE_SIZE) {
            setItemShape(mItemShape, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }

        setLayoutManager(new GridLayoutManager(getContext(), mSpanCount));
        if (gridDivider != null) removeItemDecoration(gridDivider);
        gridDivider = new MediaGridDivider(mSpanCount, mSpacing, false);
        addItemDecoration(gridDivider);

        mAdapter = new ImagePickedAdapter(context);
        setAdapter(mAdapter);

        // setItemShape(mItemShape);
    }

    /**
     * 设置数据
     *
     * @param data
     */
    public void setData(List<String> data) {
        mAdapter.setData(data);
    }

    /**
     * 是否显示删除按钮
     *
     * @param mShowDelete
     */
    public void setShowDelete(boolean mShowDelete) {
        if (this.mShowDelete == mShowDelete) return;

        this.mShowDelete = mShowDelete;
        notifyDataSetChanged();
    }

    /***
     * 设置列
     * @param mSpanCount
     */
    public void setSpanCount(int mSpanCount) {
        if (this.mSpanCount == mSpanCount) return;

        this.mSpanCount = mSpanCount;
        setLayoutManager(new GridLayoutManager(getContext(), mSpanCount));
        if (gridDivider != null) removeItemDecoration(gridDivider);
        gridDivider = new MediaGridDivider(mSpanCount, mSpacing, false);
        addItemDecoration(gridDivider);

        int width = ScreenUtils.getScreenWidth();
        mGridWidth = (int) (width * 1f / mSpanCount);

        notifyDataSetChanged();


    }

    /**
     * 设置Item 的宽高
     *
     * @param mItemShape
     */
    public void setItemShape(int mItemShape) {
        if (this.mItemShape == mItemShape) return;
        this.mItemShape = mItemShape;
        if (mItemShape == ITEM_SHAPE_SQUARE) {
            mItemLayoutParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        } else if (mItemShape == ITEM_SHAPE_SIZE) {
            setItemShape(mItemShape, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }

    }

    /**
     * @param mItemShape
     * @param mItemWidth
     * @param mItemHeight
     */
    public void setItemShape(int mItemShape, int mItemWidth, int mItemHeight) {
        this.mItemShape = mItemShape;
        this.mItemWidth = mItemWidth;
        this.mItemHeight = mItemHeight;
        mItemLayoutParams = new FrameLayout.LayoutParams(mItemWidth, mItemHeight);
    }

    /**
     *
     */
    public void notifyDataSetChanged() {
        if (mAdapter != null) {
            mAdapter.notifyDataSetChanged();
        }
    }

    /**
     * 设置最大数量
     *
     * @param mMaxCount
     */
    public void setMaxCount(int mMaxCount) {
        if (this.mMaxCount == mMaxCount) return;

        this.mMaxCount = mMaxCount;
        notifyDataSetChanged();
    }

    public void setImageScaleType(ImageView.ScaleType mScaleType) {
        if (this.mScaleType == mScaleType) return;

        this.mScaleType = mScaleType;
        notifyDataSetChanged();
    }

    /**
     * 设置添加的 图片
     *
     * @param addImageRes
     */
    public void setAddImageSrc(@DrawableRes int addImageRes) {
        this.mAddImageSrc = getResources().getDrawable(addImageRes);
        notifyDataSetChanged();
    }

    /**
     * 设置删除 的图片
     *
     * @param delImageRes
     */
    public void setDeleteImageSrc(@DrawableRes int delImageRes) {
        this.mDeleteImageSrc = getResources().getDrawable(delImageRes);
        notifyDataSetChanged();
    }


    public ImagePickedAdapter getAdapter() {
        return mAdapter;
    }


    public class ImagePickedAdapter extends RecyclerView.Adapter<ImagePickedAdapter.ImgViewHolder> {

        Context context;
        List<String> mData = new ArrayList<>();

        public ImagePickedAdapter(Context context) {
            this.context = context;
        }

        public void setData(List<String> data) {
            if (data == null) data = new ArrayList<>();
            mData = data;
            notifyDataSetChanged();
        }

        public List<String> getData() {
            return mData;
        }

        public void remove(int index) {
            mData.remove(index);
            notifyDataSetChanged();
        }

        public void remove(String data) {
            mData.remove(data);
            notifyDataSetChanged();
        }


        @Override
        public int getItemCount() {
            return Math.min(mData.size() + (mShowAddImage ? 1 : 0), mMaxCount);
        }

        @Override
        public int getItemViewType(int position) {
            if (mShowAddImage && (position) == mData.size() && mData.size() < mMaxCount) {
                return 0;
            } else {
                return R.layout.ivp_list_item_image;
            }
        }

        @Override
        public ImgViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new ImgViewHolder(LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.ivp_list_item_selected_image, parent, false));
        }

        @Override
        public void onBindViewHolder(ImgViewHolder holder, final int position) {
            holder.ivThumb.setSquare(mItemShape == ITEM_SHAPE_SQUARE);
            holder.itemView.setLayoutParams(mItemLayoutParams);
            holder.ivThumb.setScaleType(mScaleType);

            holder.ivSelect.setVisibility(mShowDelete ? VISIBLE : GONE);
            if (mShowDelete && getItemViewType(position) != 0) {
                holder.ivSelect.setVisibility(VISIBLE);
                holder.ivSelect.setImageDrawable(mDeleteImageSrc);
            } else holder.ivSelect.setVisibility(GONE);

            //点击事件 交给外界处理
            if (onImagePickEventListener != null) {
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onImagePickEventListener.onItemClick(position, getItemViewType(position) == 0);
                    }
                });
                holder.ivSelect.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onImagePickEventListener.onItemDelete(position);
                    }
                });
            }

            if (mShowAddImage && getItemViewType(position) == 0) {//// 最后一项显示一个＋按钮
                holder.ivThumb.setImageDrawable(mAddImageSrc);
            } else {
                ImagePicker.getInstance().getImageEngine().loadPicked(context, holder.ivThumb, mData.get(position), mGridWidth, mGridWidth);
            }

        }


        public class ImgViewHolder extends RecyclerView.ViewHolder {
            SquaredImageView ivThumb;
            ImageView ivSelect;

            public ImgViewHolder(View itemView) {
                super(itemView);
                ivThumb = itemView.findViewById(R.id.ivThumb);
                ivSelect = itemView.findViewById(R.id.ivSelect);
                ivThumb.setAdjustViewBounds(true);
            }
        }


    }


    protected OnImagePickEventListener onImagePickEventListener;

    public void setOnImagePickEventListener(OnImagePickEventListener onImagePickEventListener) {
        this.onImagePickEventListener = onImagePickEventListener;
    }

    public interface OnImagePickEventListener {
        void onItemClick(int position, boolean isAddImage);

        void onItemDelete(int position);
    }

}
