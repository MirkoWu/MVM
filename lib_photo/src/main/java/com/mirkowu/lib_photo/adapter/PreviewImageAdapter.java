package com.mirkowu.lib_photo.adapter;

import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;


import com.mirkowu.lib_photo.ImagePicker;
import com.mirkowu.lib_photo.bean.MediaBean;
import com.mirkowu.lib_photo.engine.ILoader;
import com.mirkowu.lib_photo.photoview.PhotoView;

import java.util.ArrayList;

/**
 * @author by DELL
 * @date on 2018/8/21
 * @describe
 */
public class PreviewImageAdapter extends PagerAdapter {
    private View.OnClickListener mOnClickListener;
    private ArrayList<MediaBean> mData;
    private int size;
    private ILoader iLoader;

    public PreviewImageAdapter(View.OnClickListener onClickListener, ArrayList<MediaBean> mData) {
        this.mData = mData;
        iLoader = ImagePicker.getInstance().getPickerConfig().getILoader();
        this.mOnClickListener = onClickListener;
        setData(mData);
    }

    public void setData(ArrayList<MediaBean> mData) {
        this.mData = mData;
        size = mData == null ? 0 : mData.size();
    }

    public int getCount() {
        return size;
    }

    @NonNull
    @Override
    public View instantiateItem(@NonNull ViewGroup container, int position) {
        PhotoView imageView = new PhotoView(container.getContext());
        imageView.setOnClickListener(mOnClickListener);
        iLoader.load(container.getContext(), imageView, mData.get(position).path);
        container.addView(imageView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        return imageView;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }

    public boolean isViewFromObject(View arg0, Object arg1) {
        return arg0 == arg1;
    }

}
