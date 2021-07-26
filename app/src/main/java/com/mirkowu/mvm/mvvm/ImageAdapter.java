package com.mirkowu.mvm.mvvm;

import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.mirkowu.lib_image.ImageLoader;
import com.mirkowu.lib_widget.adapter.BaseRVAdapter;
import com.mirkowu.lib_widget.adapter.BaseRVHolder;
import com.mirkowu.mvm.R;
import com.mirkowu.mvm.bean.GankImageBean;
import com.mirkowu.mvm.databinding.ItemGankImageBinding;

public class ImageAdapter extends BaseRVAdapter<GankImageBean, ImageAdapter.Holder> {
    private RequestOptions glideOptions = RequestOptions.placeholderOf(R.mipmap.ic_launcher)
            .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC);

    @Override
    public void onBindHolder(@NonNull Holder holder, GankImageBean item, int position) {
        ImageLoader.load(holder.binding.ivImage, item.url, glideOptions);
        addOnClickListener(holder, holder.binding.ivImage);
        addOnLongClickListener(holder, holder.binding.ivImage);

    }


    @NonNull
    @Override
    public Holder onCreateHolder(@NonNull ViewGroup parent, int viewType) {
        return new Holder(ItemGankImageBinding.inflate(mLayoutInflater, parent, false));
    }

    class Holder extends BaseRVHolder {
        ItemGankImageBinding binding;

        public Holder(@NonNull ItemGankImageBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
