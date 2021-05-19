package com.mirkowu.lib_base.fragment;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleOwner;

import com.mirkowu.lib_base.activity.BaseMVMActivity;
import com.mirkowu.lib_base.view.IBaseView;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;

public abstract class BaseDialogFragment extends DialogFragment implements IBaseView {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View mView = inflater.inflate(getLayoutId(), container, false);
        return mView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setCancelable(false);//默认不能取消
        initialize();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (!isAdded()) return;
        setDialogWindow(getDialog().getWindow());
    }

    protected void setDialogWindow(Window window) {
        window.setBackgroundDrawable(new ColorDrawable());
//        setStyle(DialogFragment.STYLE_NO_INPUT, android.R.style.Theme_Light_NoTitleBar_Fullscreen)
//        val width = (ScreenUtil.getScreenWidth(context) * 0.75) as Int //设定宽度为屏幕宽度的0.75
        window.setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    @NonNull
    @Override
    public Context getContext() {
        if (super.getContext() == null) {
            throw new NullPointerException("getContext  == null");
        }
        return super.getContext();
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

    protected BaseMVMActivity getBaseActivity() {
        if (getActivity() instanceof BaseMVMActivity) {
            return (BaseMVMActivity) getActivity();
        }
        throw new NullPointerException("当前Fragment依赖的Activity不是 BaseActivity 的子类");
    }

//    /**
//     * 显示加载框
//     */
//    public void showLoadingDialog() {
//        getBaseActivity().showLoadingDialog();
//    }
//
//    public void showLoadingDialog(String msg) {
//        getBaseActivity().showLoadingDialog(msg);
//    }
//
//    public void dismissLoadingDialog() {
//        getBaseActivity().dismissLoadingDialog();
//    }


    public void show(FragmentManager manager) {
        show(manager, getClass().getName());
    }

    public void show(FragmentManager manager, String tag) {
        Fragment prev = manager.findFragmentByTag(tag);
        FragmentTransaction ft = manager.beginTransaction();
        if (prev != null) {
            ft.remove(prev);
        }
        //不添加到回退栈
        // ft.addToBackStack(null)//不添加到回退栈


        super.show(manager, tag);
    }

    /**
     * 防止Fragment在Activity 调用onSaveInstanceState() 后显示 造成的crash
     */
    public void showAllowingStateLoss(FragmentManager manager, String tag) {
        Fragment prev = manager.findFragmentByTag(tag);
        FragmentTransaction ft = manager.beginTransaction();
        if (prev != null) {
            ft.remove(prev);
        }
        //不添加到回退栈
        // ft.addToBackStack(null)


        try {
            Class c = Class.forName("androidx.fragment.app.DialogFragment");
            Constructor con = c.getConstructor();
            Object obj = con.newInstance();
            Field dismissed = c.getDeclaredField("mDismissed");
            dismissed.setAccessible(true);
            dismissed.set(obj, false);
            Field shownByMe = c.getDeclaredField("mShownByMe");
            shownByMe.setAccessible(true);
            shownByMe.set(obj, false);
        } catch (Exception e) {
            e.printStackTrace();
        }
        ft.add(this, tag);
        ft.commitAllowingStateLoss();
    }

    protected abstract int getLayoutId();

    protected abstract void initialize();
}