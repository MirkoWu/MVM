package com.mirkowu.lib_widget.adapter;

import android.util.SparseIntArray;
import android.view.ViewGroup;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;

/**
 * 多类型Item Adapter
 */
public abstract class MultiTypeRVAdapter<T extends IMultiType, VH extends BaseRVHolder> extends BaseRVAdapter<T, VH> {
    public static final int TYPE_NOT_FOUND = -404;
    private SparseIntArray mLayoutArray;

    @NonNull
    @Override
    public VH onCreateHolder(@NonNull ViewGroup parent, int viewType) {
        return createBaseRVHolder(getHolderView(parent, getLayoutId(viewType)));
    }

    @Override
    public int getItemViewType(int position) {
        T item = getItem(position);
        if (item != null) {
            return item.getItemViewType();
        } else {
            return super.getItemViewType(position);
        }
    }

    public void addItemViewType(int type, @LayoutRes int layoutResId) {
        if (mLayoutArray == null) {
            mLayoutArray = new SparseIntArray();
        }
        mLayoutArray.put(type, layoutResId);
    }

    private int getLayoutId(int type) {
        return mLayoutArray.get(type, TYPE_NOT_FOUND);
    }


}
