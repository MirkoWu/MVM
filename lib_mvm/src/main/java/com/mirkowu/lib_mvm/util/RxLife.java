package com.mirkowu.lib_mvm.util;

import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleOwner;

import com.mirkowu.lib_mvm.core.ILifecycleBinder;

import autodispose2.AutoDispose;
import autodispose2.AutoDisposeConverter;
import autodispose2.androidx.lifecycle.AndroidLifecycleScopeProvider;

/***
 * 绑定生命周期
 */
public class RxLife {

    public static <R> AutoDisposeConverter<R> bindLifecycle(ILifecycleBinder binder) {
        return bindLifecycle(binder.getLifecycleOwner(), binder.bindLifecycleUntil());
    }

    public static <R> AutoDisposeConverter<R> bindLifecycle(LifecycleOwner owner, Lifecycle.Event untilEvent) {
        return AutoDispose.autoDisposable(AndroidLifecycleScopeProvider.from(owner, untilEvent));
    }

    public static <R> AutoDisposeConverter<R> bindLifecycleAuto(LifecycleOwner owner) {
        return AutoDispose.autoDisposable(AndroidLifecycleScopeProvider.from(owner));
    }

}
