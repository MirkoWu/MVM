package com.mirkowu.lib_util.permission;

import androidx.annotation.NonNull;

import java.util.List;

public interface PermissionCallback {
    /**
     * 有权限被同意授予时回调
     *
     * @param permissions 请求成功的权限组
     */
    void onGranted(@NonNull List<String> permissions);

    /**
     * 有权限被拒绝授予时回调
     *
     * @param permissions                请求失败的权限组
     * @param hasPermissionForeverDenied 是否有某个权限被永久拒绝了
     */
    void onDenied(@NonNull List<String> permissions, boolean hasPermissionForeverDenied);

}
