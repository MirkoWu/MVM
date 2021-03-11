package com.mirkowu.lib_mvm.core;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleOwner;


public abstract class BaseMVMFragment<M extends BaseMediator> extends Fragment implements IBaseView {
    protected M mediator;

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


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View mView = inflater.inflate(setLayoutId(), container, false);
        return mView;
    }

    protected abstract int setLayoutId();

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        createMediator();

        bindMediator();

        initialize();

//        bindCommonObserver();
//        bindObserver();
    }


//    protected abstract void bindObserver();
//
//    private void bindCommonObserver() {
//        mViewModel.mCommonError.observe(this, new Observer<ApiException>() {
//            @Override
//            public void onChanged(ApiException api) {
//                onCommonError(api);
//            }
//        });
//    }
//
//    protected void onCommonError(@NonNull ApiException api) {
////        ToastUtil.showToast(String.format("%s(%d)", api.getMessage(), api.code));
//        ToastUtil.showToast(String.format("%s", api.getMessage()));
//    }

    protected abstract void initialize();


    protected void createMediator() {
        if (mediator == null) {
            mediator = InstanceFactory.newMediator(this, getClass());
        }
    }

    private void bindMediator() {
        if (mediator != null) {
            mediator.attachView(this);
            //让Mediator拥有View的生命周期感应
            getLifecycle().addObserver(mediator);
        }

    }

    @Override
    public void onDestroyView() {
        if (mediator != null) {
            mediator.detachView();
            mediator = null;
        }
        super.onDestroyView();
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
    public void showLoading(String msg) {
        if (getActivity() instanceof BaseMVMActivity) {
            ((BaseMVMActivity) getActivity()).showLoading(msg);
        }
    }

    @Override
    public void hideLoading() {
        if (getActivity() instanceof BaseMVMActivity) {
            ((BaseMVMActivity) getActivity()).hideLoading();
        }
    }

    /*** >>>>>>>>>>>>>>>>>>>> 懒加载 >>>>>>>>>>>>>>>>>>>> */

    protected boolean isFirstLoad = true;//是否第一次加载
    private boolean isSupportLazyLoad = true;//是否支持懒加载

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

    /**
     * 懒加载
     */
    protected void onLazyLoad() {

    }
}
