package com.mirkowu.lib_photo.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mirkowu.lib_photo.ImagePicker;
import com.mirkowu.lib_photo.R;
import com.mirkowu.lib_photo.bean.MediaBean;
import com.mirkowu.lib_photo.bean.MineType;
import com.mirkowu.lib_photo.engine.ILoader;
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


    private boolean mIsShowCamera = false;
    private boolean mIsShowVideo = false;
    //    private boolean multiSelect = true;
    private ILoader mILoader;

    private List<MediaBean> mData = new ArrayList<>();
    //   private List<MediaBean> mSelectedData = new ArrayList<>();

    final int mGridWidth;

    public ImageGridAdapter(boolean showCamera, boolean showVideo, int spanCount) {
        this.mILoader = ImagePicker.getInstance().getPickerConfig().getILoader();
        this.mIsShowCamera = showCamera;
        this.mIsShowVideo = showVideo;
        int width = ScreenUtils.getScreenWidth();
        //int spacing = context.getResources().getDimensionPixelSize(R.dimen.ivp_space_size);
        mGridWidth = (int) (width * 1f / spanCount); //取整
    }
//
//    /**
//     * 显示选择指示器
//     *
//     * @param b
//     */
//    public void setMultiSelect(boolean b) {
//        multiSelect = b;
//    }

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

    /**
     * 选择某个图片，改变选择状态
     *
     * @param mediaBean
     */
    public void select(int position, MediaBean mediaBean) {
//        if (multiSelect) {
//            if (mSelectedData.contains(mediaBean)) {
//                mSelectedData.remove(mediaBean);
//            } else {
//                mSelectedData.add(mediaBean);
//            }
//        } else {
//            mSelectedData.clear();
//            mSelectedData.add(mediaBean);
//        }
        notifyItemChanged(position);
        compatibilityDataSizeChanged(0);
    }

//    /**
//     * 通过图片路径设置默认选择
//     *
//     * @param selectList
//     */
//    public void setDefaultSelected(ArrayList<MediaBean> selectList) {
//
//
////        for (MediaBean bean : resultList) {
////            MediaBean mediaBean = getImageByPath(path);
////            if (mediaBean != null) {
//        mSelectedData.addAll(selectList);
////            }
////        }
//        if (mSelectedData.size() > 0) {
//            notifyDataSetChanged();
//        }
//    }

    private MediaBean getImageByPath(String path) {
        if (mData != null && mData.size() > 0) {
            for (MediaBean mediaBean : mData) {
                if (mediaBean.path.equalsIgnoreCase(path)) {
                    return mediaBean;
                }
            }
        }
        return null;
    }

    /**
     * 设置数据集
     *
     * @param list
     */
    public void setData(List<MediaBean> list) {
        this.mData = list == null ? new ArrayList<>() : list;
//        mSelectedData.clear();
        notifyDataSetChanged();
    }

    /**
     * 获取
     *
     * @return
     */
    public List<MediaBean> getData() {
        return mData;
    }

    /**
     * 防止未被刷新
     *
     * @param size 新加的数量
     */
    private void compatibilityDataSizeChanged(int size) {
        int dataSize = this.mData == null ? 0 : this.mData.size();
        if (dataSize == size) {
            this.notifyDataSetChanged();
        }
    }

    @NonNull
    @Override
    public ImageHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        if (mIsShowCamera && viewType == TYPE_CAMERA) {
            return new ImageHolder(layoutInflater.inflate(R.layout.ivp_list_item_camera, parent, false));
        } else if (mIsShowVideo && viewType == TYPE_VIDEO) {
            return new VideoHolder(layoutInflater.inflate(R.layout.ivp_list_item_video, parent, false));
        }
        return new ImageHolder(layoutInflater.inflate(R.layout.ivp_list_item_image, parent, false));
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
                holder.ivSelect.setOnClickListener(new View.OnClickListener() {
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


    public MediaBean getItem(int i) {
        if (mIsShowCamera) {
            if (i == 0) {
                return null;
            }
            return mData.get(i - 1);
        } else {
            return mData.get(i);
        }
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public int getItemCount() {
        return mIsShowCamera ? mData.size() + 1 : mData.size();
    }

    class ImageHolder extends RecyclerView.ViewHolder {
        ImageView ivThumb;
        ImageView ivSelect;
        View mask;


        ImageHolder(View view) {
            super(view);
            ivThumb = view.findViewById(R.id.ivThumb);
            ivSelect = view.findViewById(R.id.ivSelect);
            mask = view.findViewById(R.id.mask);
        }

        void bindData(final MediaBean data) {
            if (data == null) return;

            // 处理单选和多选状态
            if (ResultModel.contains(data)) {
                // 设置选中状态
                ivSelect.setImageResource(R.drawable.ivp_btn_selected);
                mask.setVisibility(View.VISIBLE);
            } else {
                // 未选择
                ivSelect.setImageResource(R.drawable.ivp_btn_unselected);
                mask.setVisibility(View.GONE);
            }


            // 显示图片
            mILoader.loadThumbnail(ivThumb.getContext(), ivThumb, data.path, mGridWidth);
        }
    }

    class VideoHolder extends ImageHolder {
        TextView tvDuration;


        VideoHolder(View view) {
            super(view);
            tvDuration = view.findViewById(R.id.tvDuration);

            mask = view.findViewById(R.id.mask);
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
