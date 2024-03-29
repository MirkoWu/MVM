package com.mirkowu.lib_widget.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mirkowu.lib_widget.R;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import static androidx.recyclerview.widget.RecyclerView.NO_POSITION;

public abstract class BaseRVAdapter<T, VH extends BaseRVHolder> extends RecyclerView.Adapter<VH> {
    private List<T> mData = new ArrayList<>();
    protected Context mContext;
    protected LayoutInflater mLayoutInflater;

    public BaseRVAdapter() {
    }

    public BaseRVAdapter(List<T> list) {
        this.mData = list == null ? new ArrayList<>() : list;
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
        if (position < 0 || position > this.mData.size()) {
            return;
        }
        this.mData.add(position, item);
        notifyItemInserted(position);
        notifyItemRangeChanged(position, this.mData.size() - position);
        compatibilityDataSizeChanged(1);
    }

    /**
     * 更新指定位置的数据
     *
     * @param position
     * @param data
     */
    public void updateData(int position, T data) {
        if (position < 0 || position >= this.mData.size()) {
            return;
        }
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
        if (position < 0 || position >= this.mData.size()) {
            return null;
        }
        T data = this.mData.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, this.mData.size() - position);
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
            notifyItemRangeChanged(position, this.mData.size() - position);
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
            notifyItemRangeChanged(position, this.mData.size() - position);
            compatibilityDataSizeChanged(0);
            return true;
        }
        return false;
    }

    /**
     * 清空所有数据
     */
    public void clearAll() {
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

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        mContext = parent.getContext();
        mLayoutInflater = LayoutInflater.from(mContext);
        VH holder = onCreateHolder(parent, viewType);
        holder.setAdapter(this);
        return holder;
    }

    @NonNull
    public abstract VH onCreateHolder(@NonNull ViewGroup parent, int viewType);

    protected View getHolderView(@NonNull ViewGroup parent, @LayoutRes int layoutResId) {
        return mLayoutInflater.inflate(layoutResId, parent, false);
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        T item = this.mData.get(position);
        //添加Item点击事件
        addOnItemClickListener(holder);
        onBindHolder(holder, item, position);
    }

    public abstract void onBindHolder(@NonNull VH holder, T item, int position);


//    protected VH createBaseRVHolder(@NonNull ViewGroup parent, @LayoutRes int layoutResId) {
//        return this.createBaseRVHolder(getHolderView(parent, layoutResId));
//    }

    protected VH createBaseRVHolder(View view) {
        Class temp = this.getClass();

        Class z;
        for (z = null; z == null && null != temp; temp = temp.getSuperclass()) {
            z = this.getInstancedGenericKClass(temp);
        }

        VH k;
        if (z == null) {
            k = (VH) new BaseRVHolder(view);
        } else {
            k = this.createGenericKInstance(z, view);
        }

        return k != null ? k : (VH) new BaseRVHolder(view);
    }

    private VH createGenericKInstance(Class z, View view) {
        try {
            Constructor constructor;
            if (z.isMemberClass() && !Modifier.isStatic(z.getModifiers())) {
                constructor = z.getDeclaredConstructor(this.getClass(), View.class);
                constructor.setAccessible(true);
                return (VH) constructor.newInstance(this, view);
            }

            constructor = z.getDeclaredConstructor(View.class);
            constructor.setAccessible(true);
            return (VH) constructor.newInstance(view);
        } catch (NoSuchMethodException | IllegalAccessException | InstantiationException | InvocationTargetException var4) {
        }

        return null;
    }

    private Class getInstancedGenericKClass(Class z) {
        Type type = z.getGenericSuperclass();
        if (type instanceof ParameterizedType) {
            Type[] types = ((ParameterizedType) type).getActualTypeArguments();
            Type[] var4 = types;
            int var5 = types.length;

            for (int var6 = 0; var6 < var5; ++var6) {
                Type temp = var4[var6];
                if (temp instanceof Class) {
                    Class tempClass = (Class) temp;
                    if (BaseRVHolder.class.isAssignableFrom(tempClass)) {
                        return tempClass;
                    }
                } else if (temp instanceof ParameterizedType) {
                    Type rawType = ((ParameterizedType) temp).getRawType();
                    if (rawType instanceof Class && BaseRVHolder.class.isAssignableFrom((Class) rawType)) {
                        return (Class) rawType;
                    }
                }
            }
        }

        return null;
    }

    /**
     * Item添加点击事件
     *
     * @param holder
     */
    protected void addOnItemClickListener(VH holder) {
        if (mOnItemClickListener != null) {
            holder.itemView.setTag(R.id.tag_rv_holder, holder);
            holder.itemView.setOnClickListener(getDelegateOnItemClickListener());
        }
    }

    protected View.OnClickListener mDelegateOnItemClickListener;

    protected View.OnClickListener getDelegateOnItemClickListener() {
        if (mDelegateOnItemClickListener == null) {
            mDelegateOnItemClickListener = new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //兼容NotifyItemRemoved()
                    VH holder = (VH) view.getTag(R.id.tag_rv_holder);
                    if (holder == null) {
                        return;
                    }
                    int position = holder.getAdapterPosition();
                    if (position == NO_POSITION) {
                        return;
                    }
                    if (mOnItemClickListener != null) {
                        mOnItemClickListener.onItemClick(view, getItem(position), position);
                    }
                }
            };
        }
        return mDelegateOnItemClickListener;
    }


    /**
     * Child添加点击事件
     *
     * @param views
     * @return
     */
    public void addOnClickListener(VH holder, View... views) {
        if (mOnItemChildClickListener != null) {
            if (views != null && views.length > 0) {
                for (View view : views) {
                    view.setTag(R.id.tag_rv_holder, holder);
                    view.setOnClickListener(getDelegateOnItemChildClickListener());
                }
            }
        }
    }

    public void addOnClickListener(VH holder, View view) {
        if (mOnItemChildClickListener != null) {
            view.setTag(R.id.tag_rv_holder, holder);
            view.setOnClickListener(getDelegateOnItemChildClickListener());
        }
    }

    protected View.OnClickListener mDelegateOnItemChildClickListener;

    protected View.OnClickListener getDelegateOnItemChildClickListener() {
        if (mDelegateOnItemChildClickListener == null) {
            mDelegateOnItemChildClickListener = new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //兼容NotifyItemRemoved()
                    VH holder = (VH) view.getTag(R.id.tag_rv_holder);
                    if (holder == null) {
                        return;
                    }
                    int position = holder.getAdapterPosition();
                    if (position == NO_POSITION) {
                        return;
                    }
                    if (mOnItemChildClickListener != null) {
                        mOnItemChildClickListener.onItemChildClick(view, getItem(position), position);
                    }
                }
            };
        }
        return mDelegateOnItemChildClickListener;
    }


    /**
     * Child添加长按事件
     *
     * @param views
     * @return
     */
    public void addOnLongClickListener(VH holder, View... views) {
        if (mOnItemChildClickListener != null) {
            if (views != null && views.length > 0) {
                for (View view : views) {
                    view.setTag(R.id.tag_rv_holder, holder);
                    view.setOnLongClickListener(getDelegateOnItemChildLongClickListener());
                }
            }
        }
    }

    public void addOnLongClickListener(VH holder, View view) {
        if (mOnItemChildClickListener != null) {
            view.setTag(R.id.tag_rv_holder, holder);
            view.setOnLongClickListener(getDelegateOnItemChildLongClickListener());
        }
    }

    protected View.OnLongClickListener mDelegateOnItemChildLongClickListener;

    protected View.OnLongClickListener getDelegateOnItemChildLongClickListener() {
        if (mDelegateOnItemChildLongClickListener == null) {
            mDelegateOnItemChildLongClickListener = new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    //兼容NotifyItemRemoved()
                    VH holder = (VH) view.getTag(R.id.tag_rv_holder);
                    if (holder == null) {
                        return false;
                    }
                    int position = holder.getAdapterPosition();
                    if (position == NO_POSITION) {
                        return false;
                    }
                    if (mOnItemChildLongClickListener != null) {
                        return mOnItemChildLongClickListener.onItemChildLongClick(view, getItem(position), position);
                    }
                    return false;
                }
            };
        }
        return mDelegateOnItemChildLongClickListener;
    }


    /**
     * item 点击事件
     */
    public interface OnItemClickListener<T> {
        void onItemClick(View view, T item, int position);
    }

    protected OnItemClickListener<T> mOnItemClickListener;

    public void setOnItemClickListener(OnItemClickListener<T> itemClickListener) {
        mOnItemClickListener = itemClickListener;
    }

    public OnItemClickListener<T> getOnItemClickListener() {
        return mOnItemClickListener;
    }

    /**
     * childView 点击事件
     */
    public interface OnItemChildClickListener<T> {
        void onItemChildClick(View view, T item, int position);
    }

    protected OnItemChildClickListener<T> mOnItemChildClickListener;

    public void setOnItemChildClickListener(OnItemChildClickListener<T> itemChildClickListener) {
        mOnItemChildClickListener = itemChildClickListener;
    }

    public OnItemChildClickListener<T> getOnItemChildClickListener() {
        return mOnItemChildClickListener;
    }

    /**
     * childView 长按事件
     */
    public interface OnItemChildLongClickListener<T> {
        boolean onItemChildLongClick(View view, T item, int position);
    }

    protected OnItemChildLongClickListener<T> mOnItemChildLongClickListener;

    public void setOnItemChildLongClickListener(OnItemChildLongClickListener<T> itemChildLongClickListener) {
        mOnItemChildLongClickListener = itemChildLongClickListener;
    }

    public OnItemChildLongClickListener<T> getOnItemChildLongClickListener() {
        return mOnItemChildLongClickListener;
    }
}
