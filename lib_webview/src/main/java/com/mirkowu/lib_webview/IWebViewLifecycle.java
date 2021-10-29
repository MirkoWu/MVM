package com.mirkowu.lib_webview;

import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.OnLifecycleEvent;

public interface IWebViewLifecycle extends LifecycleObserver {
    @OnLifecycleEvent(Lifecycle.Event.ON_ANY)
    void onAnyEvent(LifecycleOwner owner, Lifecycle.Event event);

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    void onCreateEvent();

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    void onStartEvent();

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    void onResumeEvent();

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    void onPauseEvent();

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    void onStopEvent();

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    void onDestroyEvent();
}
