package com.mirkowu.lib_image;

import android.net.Uri;
import android.widget.ImageView;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RawRes;

import java.io.File;

public interface ILoader {
    void load(@NonNull ImageView view, @Nullable String url);

    void load(@NonNull ImageView view, @RawRes @DrawableRes @Nullable Integer resourceId);

    void load(@NonNull ImageView view, @Nullable File file);

    void load(@NonNull ImageView view, @Nullable Uri uri);

    void load(@NonNull ImageView view, @Nullable Object model);
}
