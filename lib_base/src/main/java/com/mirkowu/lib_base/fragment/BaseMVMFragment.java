package com.mirkowu.lib_base.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleOwner;

import com.mirkowu.lib_base.activity.BaseMVMActivity;
import com.mirkowu.lib_base.mediator.BaseMediator;
import com.mirkowu.lib_base.view.IBaseView;
import com.mirkowu.lib_util.PermissionsUtils;

import org.jetbrains.annotations.NotNull;


public abstract class BaseMVMFragment<M extends BaseMediator> extends Fragment implements IBaseView {
    protected M mMediator;

    private int position;
    private CharSequence title;

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public CharSequence getTitle() {
        return title;
    }

    public void setTitle(CharSequence title) {
        this.title = title;
    }

    public <T extends View> T findViewById(@IdRes int id) {
        return getView().findViewById(id);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View mView = inflater.inflate(getLayoutId(), container, false);
        return mView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        resetFirstLoad();
        bindMediator();
        initialize();
    }

    protected abstract int getLayoutId();

    protected abstract void initialize();

    protected abstract M initMediator();


    private void bindMediator() {
        //实现方式交给子类
        mMediator = initMediator();

        if (mMediator != null) {
            mMediator.attachView(this);
            //让Mediator拥有View的生命周期感应
            getLifecycle().addObserver(mMediator);
        }
    }


    @Override
    public void onDestroyView() {
        if (mMediator != null) {
            mMediator.detachView();
            mMediator = null;
        }
        super.onDestroyView();
        resetFirstLoad();
    }

    @Override
    public LifecycleOwner getLifecycleOwner() {
        return this;
    }

    @Override
    public Lifecycle.Event bindLifecycleUntil() {
        return Lifecycle.Event.ON_DESTROY;
    }


    @Override
    public void showLoadingDialog() {
        if (getActivity() instanceof BaseMVMActivity) {
            ((BaseMVMActivity) getActivity()).showLoadingDialog();
        }
    }

    @Override
    public void showLoadingDialog(final String msg) {
        if (getActivity() instanceof BaseMVMActivity) {
            ((BaseMVMActivity) getActivity()).showLoadingDialog(msg);
        }
    }

    @Override
    public void hideLoadingDialog() {
        if (getActivity() instanceof BaseMVMActivity) {
            ((BaseMVMActivity) getActivity()).hideLoadingDialog();
        }
    }

    /**
     * 权限回调
     *
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        PermissionsUtils.getInstance().onRequestPermissionsResult(this, requestCode, permissions, grantResults);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        PermissionsUtils.getInstance().onActivityResult(this, requestCode, resultCode, data);
    }


    /*** >>>>>>>>>>>>>>>>>>>> 懒加载 >>>>>>>>>>>>>>>>>>>> */

    protected boolean isFirstLoad = false; //是否第一次加载
    private boolean isSupportLazyLoad = true; //是否支持懒加载

    public boolean isSupportLazyLoad() {
        return isSupportLazyLoad;
    }

    public void setSupportLazyLoad(boolean supportLazyLoad) {
        isSupportLazyLoad = supportLazyLoad;
    }

    @Override
    public void onResume() {
        super.onResume();
        doOnLazyLoad();
    }

    private void doOnLazyLoad() {
        if (isSupportLazyLoad) {
            if (isFirstLoad) {
                isFirstLoad = false;
                onLazyLoad();
            }
        }
    }

    protected void resetFirstLoad() {
        isFirstLoad = true;
    }
    /**
     * 懒加载 需要时可重写
     */
    public void onLazyLoad() {
        //override do something...
    }

    /*** <<<<<<<<<<<<<<<<<<<<<< 懒加载 <<<<<<<<<<<<<<<<<<<<<< */
}
