package com.mirkowu.lib_base.util

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import autodispose2.AutoDispose
import autodispose2.ObservableSubscribeProxy
import autodispose2.androidx.lifecycle.AndroidLifecycleScopeProvider
import com.mirkowu.lib_base.view.ILifecycleBinder
import io.reactivex.rxjava3.core.Observable

/**
 * Lifecycle自动解绑
 * eg.
 * Observable.bindLifecycle(lifecycle)
 *
 */

fun <R : Any> Observable<R>.bindLifecycle(binder: ILifecycleBinder): ObservableSubscribeProxy<R> {
    return bindLifecycle(binder.lifecycleOwner, binder.bindLifecycleUntil())
}

fun <R : Any> Observable<R>.bindLifecycle(owner: LifecycleOwner, untilEvent: Lifecycle.Event): ObservableSubscribeProxy<R> {
    return to(AutoDispose.autoDisposable(AndroidLifecycleScopeProvider.from(owner, untilEvent)))
}

fun <R : Any> Observable<R>.bindLifecycleAuto(owner: LifecycleOwner): ObservableSubscribeProxy<R> {
    return to(AutoDispose.autoDisposable(AndroidLifecycleScopeProvider.from(owner)))
}
