package com.mirkowu.lib_widget.stateview;

import android.content.Context;

import androidx.annotation.NonNull;

public abstract class DefaultInitializer {
    public abstract void init(@NonNull Context context, @NonNull StateView stateView);
}