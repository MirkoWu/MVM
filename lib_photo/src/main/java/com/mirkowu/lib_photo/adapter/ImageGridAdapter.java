package com.mirkowu.lib_photo.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mirkowu.lib_photo.ImagePicker;
import com.mirkowu.lib_photo.R;
import com.mirkowu.lib_photo.bean.MediaBean;
import com.mirkowu.lib_photo.utils.ScreenUtils;

import java.util.ArrayList;
import java.util.List;


/**
 * 图片Adapter
 */
public class ImageGridAdapter extends RecyclerView.Adapter<ImageGridAdapter.ViewHolder> {

    private static final int TYPE_CAMERA = 0;
    private static final int TYPE_IMAGE = 1;
    private static final int TYPE_VIDEO = 2;

    private Context mContext;

    private LayoutInflater mInflater;
    private boolean showCamera = true;
    private boolean multiSelect = true;

    private List<MediaBean> mData = new ArrayList<>();
    private List<MediaBean> mSelectedData = new ArrayList<>();

    final int mGridWidth;

    public ImageGridAdapter(Context context, boolean showCamera, int spanCount) {
        mContext = context;
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.showCamera = showCamera;
        int width = ScreenUtils.getScreenWidth(context);
        //int spacing = context.getResources().getDimensionPixelSize(R.dimen.ivp_space_size);
        mGridWidth = (int) (width * 1f / spanCount);//取整
    }

    /**
     * 显示选择指示器
     *
     * @param b
     */
    public void setMultiSelect(boolean b) {
        multiSelect = b;
    }

    public void setShowCamera(boolean b) {
        if (showCamera == b) return;

        showCamera = b;
        notifyDataSetChanged();
    }

    public boolean isShowCamera() {
        return showCamera;
    }

    /**
     * 选择某个图片，改变选择状态
     *
     * @param mediaBean
     */
    public void select(MediaBean mediaBean) {
        if (multiSelect) {
            if (mSelectedData.contains(mediaBean)) {
                mSelectedData.remove(mediaBean);
            } else {
                mSelectedData.add(mediaBean);
            }
        } else {
            mSelectedData.clear();
            mSelectedData.add(mediaBean);
        }
        notifyDataSetChanged();
    }

    /**
     * 通过图片路径设置默认选择
     *
     * @param resultList
     */
    public void setDefaultSelected(ArrayList<String> resultList) {
        for (String path : resultList) {
            MediaBean mediaBean = getImageByPath(path);
            if (mediaBean != null) {
                mSelectedData.add(mediaBean);
            }
        }
        if (mSelectedData.size() > 0) {
            notifyDataSetChanged();
        }
    }

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
     * @param mediaBeans
     */
    public void setData(List<MediaBean> mediaBeans) {
        mSelectedData.clear();

        if (mediaBeans != null && mediaBeans.size() > 0) {
            mData = mediaBeans;
        } else {
            mData.clear();
        }
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


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (isShowCamera() && viewType == TYPE_CAMERA) {
            return new ViewHolder(mInflater.inflate(R.layout.ivp_list_item_camera, parent, false));
        }
        return new ViewHolder(mInflater.inflate(R.layout.ivp_list_item_image, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        if (onItemClickListener != null) {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onItemClickListener.onItemClick(ImageGridAdapter.this, v, position);
                }
            });

            if (getItemViewType(position) != TYPE_CAMERA)
                holder.ivSelect.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onItemClickListener.onItemViewClick(ImageGridAdapter.this, v, position);
                    }
                });
        }

        holder.bindData(getItem(position));
    }

    @Override
    public int getItemViewType(int position) {
        if (showCamera) {
            return position == 0 ? TYPE_CAMERA : TYPE_IMAGE;
        }
        return TYPE_IMAGE;
    }


    public MediaBean getItem(int i) {
        if (showCamera) {
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
        return showCamera ? mData.size() + 1 : mData.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivThumb;
        ImageView ivSelect;
        View mask;

        ViewHolder(View view) {
            super(view);
            ivThumb = view.findViewById(R.id.ivThumb);
            ivSelect = view.findViewById(R.id.ivSelect);
            mask = view.findViewById(R.id.mask);
        }

        void bindData(final MediaBean data) {
            if (data == null) return;

            // 处理单选和多选状态
            //  if (showSelectIndicator) {
            // ivSelect.setVisibility(View.VISIBLE);
            if (mSelectedData.contains(data)) {
                // 设置选中状态
                ivSelect.setImageResource(R.drawable.ivp_btn_selected);
                mask.setVisibility(View.VISIBLE);
            } else {
                // 未选择
                ivSelect.setImageResource(R.drawable.ivp_btn_unselected);
                mask.setVisibility(View.GONE);
            }
//            } else {
//                ivSelect.setVisibility(View.GONE);
//            }

            // 显示图片
            ImagePicker.getInstance().getImageEngine().loadThumbnail(mContext, ivThumb, data.path, mGridWidth);
        }
    }


    protected OnItemClickListener onItemClickListener;

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public interface OnItemClickListener {
        void onItemClick(ImageGridAdapter adapter, View itemView, int position);

        void onItemViewClick(ImageGridAdapter adapter, View view, int position);
    }

}
