package com.mirkowu.lib_base.view;

import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleOwner;

/**
 * Lifecycle 绑定接口
 */
public interface ILifecycleBinder {
    LifecycleOwner getLifecycleOwner();

    Lifecycle.Event bindLifecycleUntil();
}
