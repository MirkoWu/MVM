package com.mirkowu.lib_photo.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.mirkowu.lib_photo.ImagePicker;
import com.mirkowu.lib_photo.R;
import com.mirkowu.lib_photo.bean.MediaBean;
import com.mirkowu.lib_photo.engine.IImageEngine;
import com.mirkowu.lib_photo.photoview.OnViewTapListener;
import com.mirkowu.lib_photo.photoview.PhotoView;
import com.mirkowu.lib_util.utilcode.util.SizeUtils;

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
    private IImageEngine iLoader;
    private int width;

    public PreviewImageAdapter(ArrayList<MediaBean> mData, View.OnClickListener onClickListener) {
        this.mData = mData;
        iLoader = ImagePicker.getInstance().getImageEngine();
        this.mOnClickListener = onClickListener;
        width = SizeUtils.dp2px(56f);
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
        Context context = container.getContext();
        MediaBean bean = mData.get(position);

        PhotoView photoView = new PhotoView(container.getContext());
        photoView.setOnClickListener(mOnClickListener);
        if (bean.uri != null) {
            iLoader.load(container.getContext(), photoView, bean.uri);
        } else {
            iLoader.load(container.getContext(), photoView, bean.path);
        }

        if (bean.isVideo()) {
            FrameLayout rootView = new FrameLayout(context);
            rootView.addView(photoView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            photoView.setZoomable(false);
            ImageView ivPlay = new ImageView(context);
            ivPlay.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
            ivPlay.setImageResource(R.drawable.ivp_video);
            ivPlay.setTag(bean);
            ivPlay.setOnClickListener(mOClickListener);
            rootView.addView(ivPlay, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            container.addView(rootView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            return rootView;
        } else {
            container.addView(photoView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            return photoView;
        }

    }

    private View.OnClickListener mOClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            MediaBean bean = (MediaBean) view.getTag();
            if (bean != null) {
                toPlayVideo(view, bean.uri, bean.type);
            }
        }
    };

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }

    public boolean isViewFromObject(View arg0, Object arg1) {
        return arg0 == arg1;
    }


    private void toPlayVideo(View v, Uri uri, String type) {
        Context context = v.getContext();
        Intent intent = new Intent(Intent.ACTION_VIEW);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        }
        intent.setDataAndType(uri, type);
        context.startActivity(intent);
    }

}
