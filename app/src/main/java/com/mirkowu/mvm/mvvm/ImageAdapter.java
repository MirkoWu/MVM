package com.mirkowu.mvm.mvvm;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.mirkowu.lib_base.adapter.BaseAdapter;
import com.mirkowu.lib_image.ImageLoader;
import com.mirkowu.mvm.R;
import com.mirkowu.mvm.bean.GankImageBean;
import com.mirkowu.mvm.databinding.ItemGankImageBinding;

public class ImageAdapter extends BaseAdapter<GankImageBean, ImageAdapter.Holder> {
    private RequestOptions glideOptions = RequestOptions.placeholderOf(R.mipmap.ic_launcher).diskCacheStrategy(DiskCacheStrategy.AUTOMATIC);

    @Override
    public void onBindHolder(@NonNull Holder holder, GankImageBean item, int position) {
        ImageLoader.load(holder.binding.ivImage, item.url, glideOptions);
        addOnItemChildClickListener(holder.binding.ivImage, position);

    }


    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new Holder(ItemGankImageBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    class Holder extends RecyclerView.ViewHolder {
        ItemGankImageBinding binding;

        public Holder(@NonNull ItemGankImageBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
