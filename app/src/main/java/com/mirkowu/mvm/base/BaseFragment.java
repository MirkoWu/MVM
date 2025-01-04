package com.mirkowu.mvm.base;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.mirkowu.lib_base.fragment.BaseMVMFragment;
import com.mirkowu.lib_base.mediator.BaseMediator;
import com.mirkowu.lib_base.util.InstanceFactory;

public abstract class BaseFragment<M extends BaseMediator> extends BaseMVMFragment<M> {
    @Override
    protected M initMediator() {
        if (mMediator == null) {
            mMediator = InstanceFactory.newMediator(this, getClass());
        }
        return mMediator;
    }

    @Override
    protected View bindContentView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container) {
        return inflater.inflate(getLayoutId(), container, false);
    }

    @LayoutRes
    protected abstract int getLayoutId();

//    @Override
//    public void showStateView(StateBean bean) {
//
//    }
//
//    @Override
//    public void hideStateView() {
//
//    }
//
//    @Override
//    public void onFailure() {
//
//    }
}
