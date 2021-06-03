package com.mirkowu.lib_photo.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.mirkowu.lib_photo.ImagePicker;
import com.mirkowu.lib_photo.R;
import com.mirkowu.lib_photo.bean.Folder;
import com.mirkowu.lib_photo.engine.ILoader;

import java.util.ArrayList;
import java.util.List;


/**
 * 文件夹Adapter
 */
public class FolderAdapter extends BaseAdapter {

    private Context mContext;
    private LayoutInflater mInflater;

    private List<Folder> mFolders = new ArrayList<>();

    int mImageSize;

    int lastSelected = 0;
    private ILoader iLoader;

    public FolderAdapter(Context context) {
        mContext = context;
        iLoader = ImagePicker.getInstance().getPickerConfig().getILoader();
        mImageSize = mContext.getResources().getDimensionPixelOffset(R.dimen.ivp_folder_cover_size);
    }

    /**
     * 设置数据集
     *
     * @param folders
     */
    public void setData(List<Folder> folders) {
        if (folders != null && folders.size() > 0) {
            mFolders = folders;
        } else {
            mFolders.clear();
        }
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mFolders.size();
    }

    @Override
    public Folder getItem(int i) {
        return mFolders.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder holder;
        if (view == null) {
            view = mInflater.inflate(R.layout.ivp_list_item_folder, viewGroup, false);
            holder = new ViewHolder(view);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        if (holder != null) {
            holder.bindData(getItem(i));
            //  }
            if (lastSelected == i) {
                holder.indicator.setVisibility(View.VISIBLE);
            } else {
                holder.indicator.setVisibility(View.INVISIBLE);
            }
        }
        return view;
    }

    private int getTotalImageSize() {
        int result = 0;
        if (mFolders != null && mFolders.size() > 0) {
            for (Folder f : mFolders) {
                result += f.mediaBeans.size();
            }
        }
        return result;
    }

    public void setSelectIndex(int i) {
        if (lastSelected == i) return;

        lastSelected = i;
        notifyDataSetChanged();
    }

    public int getSelectIndex() {
        return lastSelected;
    }

    class ViewHolder {
        ImageView cover;
        TextView name;
        TextView path;
        TextView size;
        ImageView indicator;

        ViewHolder(View view) {
            cover = (ImageView) view.findViewById(R.id.cover);
            name = (TextView) view.findViewById(R.id.name);
            path = (TextView) view.findViewById(R.id.path);
            size = (TextView) view.findViewById(R.id.size);
            indicator = (ImageView) view.findViewById(R.id.indicator);
            view.setTag(this);
        }

        void bindData(Folder data) {
            if (data == null) {
                return;
            }
            name.setText(data.name);
            path.setText(data.path);
            if (data.mediaBeans != null) {
                size.setText(String.format("%d%s", data.mediaBeans.size(), mContext.getResources().getString(R.string.ivp_photo_unit)));
            } else {
                size.setText("*" + mContext.getResources().getString(R.string.ivp_photo_unit));
            }
            if (data.cover != null) {
                // 显示图片
                iLoader.loadThumbnail(mContext, cover, data.cover.path, mImageSize);
            } else {
                cover.setImageResource(R.drawable.ivp_default_error);
            }
        }
    }

}
