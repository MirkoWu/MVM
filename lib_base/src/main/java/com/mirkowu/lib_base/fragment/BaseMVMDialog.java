package com.mirkowu.lib_base.fragment;

import android.view.View;

import androidx.annotation.IdRes;
import androidx.annotation.LayoutRes;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleOwner;

import com.mirkowu.lib_base.activity.BaseMVMActivity;
import com.mirkowu.lib_base.mediator.BaseMediator;
import com.mirkowu.lib_base.view.IBaseView;
import com.mirkowu.lib_widget.dialog.BaseDialog;

public abstract class BaseMVMDialog<M extends BaseMediator> extends BaseDialog implements IBaseView {
    protected M mMediator;

    public <T extends View> T findViewById(@IdRes int id) {
        return getView().findViewById(id);
    }

//    @Override
//    protected void convertView(ViewHolder viewHolder, BaseDialog baseDialog) {
//        initialize();
//    }


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
    }


    @Override
    public Lifecycle.Event bindLifecycleUntil() {
        return Lifecycle.Event.ON_DESTROY;
    }

    @Override
    public LifecycleOwner getLifecycleOwner() {
        return this;
    }

    @Override
    public void showLoadingDialog() {
        if (getActivity() instanceof BaseMVMActivity) {
            ((BaseMVMActivity) getActivity()).showLoadingDialog();
        }
    }

    /**
     * 显示加载框
     */
    @Override
    public void showLoadingDialog(String msg) {
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

    public BaseMVMDialog show(FragmentManager manager) {
        show(manager, getClass().getName());
        return this;
    }

    //    public void show(FragmentManager manager, String tag) {
//        Fragment prev = manager.findFragmentByTag(tag);
//        FragmentTransaction ft = manager.beginTransaction();
//        if (prev != null) {
//            ft.remove(prev);
//        }
//        //不添加到回退栈
//        // ft.addToBackStack(null)//不添加到回退栈
//        super.show(manager, tag);
//    }
    protected abstract void initialize();
}