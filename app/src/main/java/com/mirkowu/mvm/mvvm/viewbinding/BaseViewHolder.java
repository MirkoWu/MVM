package com.mirkowu.mvm.mvvm.viewbinding;


import androidx.recyclerview.widget.RecyclerView;
import androidx.viewbinding.ViewBinding;

public class BaseViewHolder<T extends ViewBinding> extends RecyclerView.ViewHolder {


    public BaseViewHolder(T binding) {
        super(binding.getRoot());
    }
}
