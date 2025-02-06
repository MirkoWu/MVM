package com.mirkowu.lib_util.permission

import android.Manifest
import android.os.Build

object Permissions {
    /**
     * 通知栏权限（虚构权限，需要 Android 6.0 及以上，注意此权限不需要在清单文件中注册也能申请）
     */
    const val NOTIFICATION_SERVICE = "android.permission.NOTIFICATION_SERVICE"

    /**
     * 特殊权限，必须去设置页面申请
     */
    @JvmStatic
    val SPECIAL_PERMISSION = arrayOf(
        Manifest.permission.MANAGE_EXTERNAL_STORAGE,
        Manifest.permission.REQUEST_INSTALL_PACKAGES,
        Manifest.permission.SYSTEM_ALERT_WINDOW,
        Manifest.permission.WRITE_SETTINGS,
        NOTIFICATION_SERVICE,
    )

    /**
     * 摄像头，文件存储
     */
    @JvmStatic
    val GROUP_CAMERA =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            arrayOf(
                Manifest.permission.CAMERA,
                Manifest.permission.READ_MEDIA_IMAGES,
                Manifest.permission.READ_MEDIA_VIDEO
            )
        } else {
            arrayOf(
                Manifest.permission.CAMERA,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
        }


    /**
     * 文件存储
     */
    @JvmStatic
    val GROUP_STORAGE =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            arrayOf(
                Manifest.permission.READ_MEDIA_IMAGES,
                Manifest.permission.READ_MEDIA_VIDEO
            )
        } else {
            arrayOf(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
        }


    @JvmStatic
    val MANAGE_EXTERNAL_STORAGE = arrayOf(
        Manifest.permission.MANAGE_EXTERNAL_STORAGE
    )

    /**
     * 蓝牙权限（需要粗略定位权限 ）
     * 编译版本10及以上 需要 ACCESS_FINE_LOCATION
     * 9以下 有 ACCESS_COARSE_LOCATION 或 ACCESS_FINE_LOCATION 任一个都行
     */
    @JvmStatic
    val GROUP_BLUETOOTH = arrayOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.BLUETOOTH
    )

    /**
     * 定位权限
     */
    @JvmStatic
    val GROUP_LOCATION_ALL = arrayOf(
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.ACCESS_FINE_LOCATION
    )

    /**
     * 手机信息
     */
    @JvmStatic
    val GROUP_PHONE = arrayOf(
        Manifest.permission.READ_PHONE_STATE
    )

    @JvmStatic
    private val PERMISSION_NAME = mapOf(
        Manifest.permission.MANAGE_EXTERNAL_STORAGE to "文件管理权限",
        Manifest.permission.READ_MEDIA_IMAGES to "文件管理权限",
        Manifest.permission.READ_MEDIA_VIDEO to "文件管理权限",
        Manifest.permission.READ_MEDIA_AUDIO to "文件管理权限",
        Manifest.permission.REQUEST_INSTALL_PACKAGES to "安装应用权限",
        Manifest.permission.SYSTEM_ALERT_WINDOW to "悬浮窗权限",
        Manifest.permission.WRITE_SETTINGS to "修改系统设置权限",
        NOTIFICATION_SERVICE to "通知权限",
        Manifest.permission.CAMERA to "相机权限",
        Manifest.permission.WRITE_EXTERNAL_STORAGE to "文件存储权限",
        Manifest.permission.READ_EXTERNAL_STORAGE to "文件存储权限",
        Manifest.permission.BLUETOOTH to "蓝牙权限",
        Manifest.permission.ACCESS_COARSE_LOCATION to "定位权限",
        Manifest.permission.ACCESS_FINE_LOCATION to "定位权限",
        Manifest.permission.READ_PHONE_STATE to "获取手机信息权限",
    )

    @JvmStatic
    fun getDeniedPermissionName(list: List<String>): String {
        return PERMISSION_NAME.filter { list.contains(it.key) }.values.toSet().joinToString("、")
    }
}