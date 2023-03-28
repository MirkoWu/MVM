package com.mirkowu.lib_util.livedata;



import androidx.annotation.MainThread;
import androidx.annotation.Nullable;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import com.mirkowu.lib_util.LogUtil;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * use
 * @see FixedLiveData
 * @param <T>
 */
@Deprecated
public class SingleLiveData<T> extends MutableLiveData<T> {

    private static final String TAG = "SingleLiveEvent";

    private final AtomicBoolean mPending = new AtomicBoolean(false);

    public SingleLiveData() {
        super();
    }

    public SingleLiveData(T value) {
        super(value);
    }

    @MainThread
    public void observe(LifecycleOwner owner, final Observer<? super T> observer) {

        if (hasActiveObservers()) {
            LogUtil.w(TAG, "Multiple observers registered but only one will be notified of changes.");
        }

        // Observe the internal MutableLiveData
        super.observe(owner, new Observer<T>() {
            @Override
            public void onChanged(@Nullable T t) {
                if (mPending.compareAndSet(true, false)) {  //
                    observer.onChanged(t);
                }
            }
        });
    }

    /**
     * 当 setValue时 mPending.set(true) compareAndSet(true,false)
     * （true(set(true)后的“原值”) == true(except 值)）=true  -> mPending.set(false（update值）)
     * 所以 再次 observer时 该值为false 所以不会执行 onChanged（t）方法
     *
     * @param t
     */
    @MainThread
    public void setValue(@Nullable T t) {
        mPending.set(true);
        super.setValue(t);
    }

    @Override
    public void postValue(T value) {
        super.postValue(value);
    }

    /**
     * Used for cases where T is Void, to make calls cleaner.
     */
    @MainThread
    public void call() {
        setValue(null);
    }
}