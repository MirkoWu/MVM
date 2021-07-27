package com.mirkowu.lib_widget.adapter;

import android.view.ViewGroup;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;

public abstract class SimpleRVAdapter<T> extends BaseRVAdapter<T, BaseRVHolder> {
    protected int mLayoutResId;

    public SimpleRVAdapter(@LayoutRes int layoutResId) {
        mLayoutResId = layoutResId;
    }

    @NonNull
    @Override
    public BaseRVHolder onCreateHolder(@NonNull ViewGroup parent, int viewType) {
        return new BaseRVHolder(getHolderView(parent, mLayoutResId));
    }
}
