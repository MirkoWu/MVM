package com.mirkowu.lib_photo.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.mirkowu.lib_photo.ImagePicker;
import com.mirkowu.lib_photo.PickerConfig;
import com.mirkowu.lib_photo.R;
import com.mirkowu.lib_photo.bean.MediaBean;
import com.mirkowu.lib_photo.bean.MineType;
import com.mirkowu.lib_photo.engine.IImageEngine;
import com.mirkowu.lib_photo.mediaLoader.ResultModel;
import com.mirkowu.lib_util.utilcode.util.ScreenUtils;
import com.mirkowu.lib_util.utilcode.util.TimeUtils;

import java.util.ArrayList;
import java.util.List;


/**
 * 图片Adapter
 */
public class ImageGridAdapter extends RecyclerView.Adapter<ImageGridAdapter.ImageHolder> {

    private static final int TYPE_CAMERA = 0;
    private static final int TYPE_IMAGE = 1;
    private static final int TYPE_VIDEO = 2;

    private List<MediaBean> mData = new ArrayList<>();


    private boolean mIsShowCamera;
    private boolean mIsShowVideo;
    private IImageEngine mILoader;
    private LayoutInflater mLayoutInflater;
    final int mGridWidth;

    public ImageGridAdapter(Context context) {
        mLayoutInflater = LayoutInflater.from(context);
        PickerConfig config = ImagePicker.getInstance().getPickerConfig();
        this.mILoader = ImagePicker.getInstance().getImageEngine();
        this.mIsShowCamera = config.isShowCamera();
        this.mIsShowVideo = config.isShowVideo();
        int spanCount = config.getSpanCount();
        int width = ScreenUtils.getScreenWidth();
        mGridWidth = (int) (width * 1f / spanCount); //取整
    }

    public void setIsShowCamera(boolean showCamera) {
        if (mIsShowCamera == showCamera) return;
        mIsShowCamera = showCamera;
        if (mIsShowCamera) {
            notifyItemInserted(0);
        } else {
            notifyItemRemoved(0);
        }
    }

    public boolean isIsShowCamera() {
        return mIsShowCamera;
    }

//    /**
//     * 选择某个图片，改变选择状态
//     *
//     * @param mediaBean
//     */
//    public void select(int position, MediaBean mediaBean) {
//        notifyItemChanged(position);
//    }

    /**
     * 设置数据集
     *
     * @param list
     */
    public void setData(List<MediaBean> list) {
        int oldSize = getItemCount();
        list = list == null ? new ArrayList<>() : list;
        this.mData.clear();
        notifyItemRangeRemoved(0, oldSize);
        this.mData.addAll(list);
        notifyItemRangeInserted(0, getItemCount());
    }

//    /**
//     * 获取
//     *
//     * @return
//     */
//    public List<MediaBean> getData() {
//        return mData;
//    }

    @NonNull
    @Override
    public ImageHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (mIsShowCamera && viewType == TYPE_CAMERA) {
            return new ImageHolder(mLayoutInflater.inflate(R.layout.ivp_list_item_camera, parent, false));
        } else if (mIsShowVideo && viewType == TYPE_VIDEO) {
            return new VideoHolder(mLayoutInflater.inflate(R.layout.ivp_list_item_video, parent, false));
        }
        return new ImageHolder(mLayoutInflater.inflate(R.layout.ivp_list_item_image, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull final ImageHolder holder, final int position) {
        if (mOnItemClickListener != null) {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOnItemClickListener.onItemClick(ImageGridAdapter.this, v, position);
                }
            });

            if (getItemViewType(position) != TYPE_CAMERA) {
                holder.flSelect.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mOnItemClickListener.onItemViewClick(ImageGridAdapter.this, v, position);
                    }
                });
            }
        }

        holder.bindData(getItem(position));
    }

    @Override
    public int getItemViewType(int position) {
        if (mIsShowCamera && position == 0) {
            return TYPE_CAMERA;
        }
        if (mIsShowVideo) {
            MediaBean bean = getItem(position);
            if (bean != null && bean.type.contains(MineType.VIDEO)) {
                return TYPE_VIDEO;
            }
        }
        return TYPE_IMAGE;
    }


    public MediaBean getItem(int position) {
        if (mIsShowCamera) {
            if (position == 0) {
                return null;
            }
            return mData.get(position - 1);
        } else {
            return mData.get(position);
        }
    }

    @Override
    public int getItemCount() {
        return mIsShowCamera ? mData.size() + 1 : mData.size();
    }

    class ImageHolder extends RecyclerView.ViewHolder {
        final FrameLayout flSelect;
        final TextView tvNumber;
//        final ImageView ivSelect;
        final ImageView ivThumb;
        final View mask;

        ImageHolder(View view) {
            super(view);
            flSelect = view.findViewById(R.id.flSelect);
            tvNumber = view.findViewById(R.id.tvNumber);
//            ivSelect = view.findViewById(R.id.ivSelect);
            ivThumb = view.findViewById(R.id.ivThumb);
            mask = view.findViewById(R.id.mask);
        }

        void bindData(final MediaBean data) {
            if (data == null) return;

            // 处理单选和多选状态
            if (ResultModel.contains(data)) {
                int number = ResultModel.getNumber(data);

                // 设置选中状态
//                ivSelect.setImageResource(R.drawable.ivp_btn_selected);
                flSelect.setSelected(true);
                tvNumber.setText(String.valueOf(number));
//                mask.setVisibility(View.VISIBLE);
                mask.setBackgroundColor(ContextCompat.getColor(mask.getContext(),R.color.ivp_mask_select_color));
            } else {
                // 未选择
//                ivSelect.setImageResource(R.drawable.ivp_btn_unselected);
                flSelect.setSelected(false);
                tvNumber.setText(null);
//                mask.setVisibility(View.GONE);
                mask.setBackgroundColor(ContextCompat.getColor(mask.getContext(),R.color.ivp_mask_normal_color));

            }

            // 显示图片
            mILoader.loadThumbnail(ivThumb.getContext(), ivThumb, data.uri, mGridWidth);
        }
    }

    class VideoHolder extends ImageHolder {
        final TextView tvDuration;

        VideoHolder(View view) {
            super(view);
            tvDuration = view.findViewById(R.id.tvDuration);
        }

        void bindData(final MediaBean data) {
            super.bindData(data);
            tvDuration.setText(TimeUtils.millis2String(data.duration, "mm:ss"));
        }
    }


    protected OnItemClickListener mOnItemClickListener;

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.mOnItemClickListener = onItemClickListener;
    }

    public interface OnItemClickListener {
        void onItemClick(ImageGridAdapter adapter, View itemView, int position);

        void onItemViewClick(ImageGridAdapter adapter, View view, int position);
    }

}
