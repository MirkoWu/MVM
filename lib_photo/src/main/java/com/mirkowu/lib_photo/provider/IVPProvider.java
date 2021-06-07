package com.mirkowu.lib_photo.provider;

import androidx.core.content.FileProvider;

public class IVPProvider extends FileProvider {
    @Override
    public boolean onCreate() {
        return true;
    }

}
