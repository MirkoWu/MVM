package com.mirkowu.lib_widget.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.IdRes;
import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;

public abstract class SimpleRVAdapter<T> extends BaseRVAdapter<T, BaseRVHolder> {
    private int mLayoutResId;


    public SimpleRVAdapter(@LayoutRes int layoutResId) {
        mLayoutResId = layoutResId;
    }


    @NonNull
    @Override
    public BaseRVHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new BaseRVHolder(LayoutInflater.from(parent.getContext()).inflate(mLayoutResId, parent, false));
    }

    /**
     * Child添加点击事件
     *
     * @param resIds
     * @return
     */
    protected void addOnClickListener(BaseRVHolder holder, @IdRes int... resIds) {
        if (mOnItemChildClickListener != null) {
            if (resIds != null && resIds.length > 0) {
                for (int id : resIds) {
                    View view = holder.getView(id);
                    if (view != null) {
                        addOnClickListener(holder, view);
                    }
                }
            }
        }
    }

    protected void addOnClickListener(BaseRVHolder holder, @IdRes int resId) {
        if (mOnItemChildClickListener != null) {
            View view = holder.getView(resId);
            if (view != null) {
                addOnClickListener(holder, view);
            }
        }
    }

    /**
     * Child添加点击事件
     *
     * @param resIds
     * @return
     */
    protected void addOnLongClickListener(BaseRVHolder holder, @IdRes int... resIds) {
        if (mOnItemChildLongClickListener != null) {
            if (resIds != null && resIds.length > 0) {
                for (int id : resIds) {
                    View view = holder.getView(id);
                    if (view != null) {
                        addOnLongClickListener(holder, view);
                    }
                }
            }
        }
    }

    protected void addOnLongClickListener(BaseRVHolder holder, @IdRes int resId) {
        if (mOnItemChildLongClickListener != null) {
            View view = holder.getView(resId);
            if (view != null) {
                addOnLongClickListener(holder, view);
            }
        }
    }
}
