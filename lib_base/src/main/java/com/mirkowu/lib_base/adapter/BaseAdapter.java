package com.mirkowu.lib_base.adapter;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public abstract class BaseAdapter<T, VH extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<VH> {
    private List<T> mData = new ArrayList<>();

    public BaseAdapter() {
    }

    public BaseAdapter(List<T> list) {
        this.mData = list == null ? new ArrayList<>() : list;
        ;
    }

    public List<T> getData() {
        return this.mData;
    }

    /**
     * 设置数据
     *
     * @param list
     */
    public void setData(List<T> list) {
        this.mData = list == null ? new ArrayList<>() : list;
        notifyDataSetChanged();
    }

    /**
     * 添加列表数据
     *
     * @param list
     */
    public void addData(List<T> list) {
        list = list == null ? new ArrayList<>() : list;
        int size = this.mData.size();
        this.mData.addAll(list);
        notifyItemRangeInserted(size, list.size());
        compatibilityDataSizeChanged(list.size());
    }

    /**
     * 添加单个数据
     *
     * @param item
     */
    public void addData(T item) {
        int size = this.mData.size();
        this.mData.add(item);
        notifyItemInserted(size);
        compatibilityDataSizeChanged(1);
    }

    /**
     * 添加指定位置的数据
     *
     * @param position
     * @param item
     */
    public void addData(int position, T item) {
        this.mData.add(position, item);
        notifyItemInserted(position);
        compatibilityDataSizeChanged(1);
    }

    /**
     * 更新指定位置的数据
     *
     * @param position
     * @param data
     */
    public void updateData(int position, T data) {
        this.mData.set(position, data);
        notifyItemChanged(position);
        compatibilityDataSizeChanged(0);
    }

    /**
     * 移除指定位置的数据
     *
     * @param position
     */
    public T removeData(int position) {
        T data = this.mData.remove(position);
        notifyItemRemoved(position);
        compatibilityDataSizeChanged(0);
        return data;
    }

    /**
     * 移除指定数据 （顺序）
     *
     * @param data
     */
    public boolean removeData(T data) {
        int position = this.mData.indexOf(data);
        if (position > -1) {
            this.mData.remove(position);
            notifyItemRemoved(position);
            compatibilityDataSizeChanged(0);
            return true;
        }
        return false;
    }

    /**
     * 移除列表中最后一个指定的数据 （逆序）
     *
     * @param data
     * @return
     */
    public boolean removeLastData(T data) {
        int position = this.mData.lastIndexOf(data);
        if (position > -1) {
            this.mData.remove(position);
            notifyItemRemoved(position);
            compatibilityDataSizeChanged(0);
            return true;
        }
        return false;
    }

    /**
     * 情况所有数据
     *
     * @param data
     */
    public void clearAll(T data) {
        this.mData.clear();
        notifyDataSetChanged();
    }


    /**
     * 防止未被刷新
     *
     * @param size 新加的数量
     */
    private void compatibilityDataSizeChanged(int size) {
        int dataSize = this.mData == null ? 0 : this.mData.size();
        if (dataSize == size) {
            this.notifyDataSetChanged();
        }

    }

    public T getItem(int position) {
        return this.mData.get(position);
    }

    @Override
    public int getItemCount() {
        return this.mData.size();
    }


    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        T item = this.mData.get(position);
        holder.itemView.setOnClickListener(v -> {
            if (mItemClickListener != null) {
                mItemClickListener.onItemClick(v, item, position);
            }
        });

        onBindHolder(holder, item, position);
    }

    public abstract void onBindHolder(@NonNull VH holder, T item, int position);

    /**
     * item 点击事件
     */
    public interface OnItemClickListener<T> {
        void onItemClick(View view, T item, int position);
    }

    protected OnItemClickListener mItemClickListener;

    public void setOnItemClickListener(OnItemClickListener itemClickListener) {
        mItemClickListener = itemClickListener;
    }

    /**
     * childView 点击事件
     */
    public interface OnItemChildClickListener<T> {
        void onItemChildClick(View view, T item, int position);
    }

    protected OnItemChildClickListener mItemChildClickListener;

    public void setOnItemChildClickListener(OnItemChildClickListener itemChildClickListener) {
        mItemChildClickListener = itemChildClickListener;
    }
}
