package com.mirkowu.lib_photo.adapter;

import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.github.chrisbanes.photoview.PhotoView;
import com.mirkowu.imagepicker.ImagePicker;

import java.util.ArrayList;

/**
 * @author by DELL
 * @date on 2018/8/21
 * @describe
 */
public class PreviewImageAdapter extends PagerAdapter {
    private View.OnClickListener mOnClickListener;
    private ArrayList<String> mData;
    private int size;

    public PreviewImageAdapter(View.OnClickListener onClickListener,ArrayList<String> mData) {
        this.mData = mData;
        this.mOnClickListener = onClickListener;
        setData(mData);
    }

    public void setData(ArrayList<String> mData) {
        this.mData = mData;
        size = mData == null ? 0 : mData.size();
    }

    public int getCount() {// 返回数量
        return size;
    }

    @NonNull
    @Override
    public View instantiateItem(@NonNull ViewGroup container, int position) {
        PhotoView imageView = new PhotoView(container.getContext());
        imageView.setOnClickListener(mOnClickListener);
        ImagePicker.getInstance().getImageEngine().load(container.getContext(), imageView, mData.get(position));
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
