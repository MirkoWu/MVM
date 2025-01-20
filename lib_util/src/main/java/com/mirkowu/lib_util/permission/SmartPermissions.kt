package com.mirkowu.lib_util.permission

import android.Manifest
import android.os.Build
import androidx.fragment.app.FragmentActivity
import com.mirkowu.lib_util.R
import com.mirkowu.lib_util.utilcode.util.StringUtils

class SmartPermissions {

    companion object {
        @JvmStatic
        fun with(permissions: String): SmartPermissions {
            return SmartPermissions(arrayOf(permissions))
        }

        @JvmStatic
        fun with(permissions: Array<String>): SmartPermissions {
            return SmartPermissions(permissions)
        }

        @JvmStatic
        fun with(permissions: List<String>): SmartPermissions {
            return SmartPermissions(permissions.toTypedArray())
        }

    }

    private var permissions: Array<out String>
    private var options: DialogOptions? = null

    private constructor(permissions: Array<out String>) {
        this.permissions = permissions
    }

    fun setDialogOptions(options: DialogOptions): SmartPermissions {
        this.options = options
        return this
    }

    @JvmOverloads
    fun setDialogOptions(
        title: String,
        content: String,
        negativeButton: String = StringUtils.getString(R.string.util_permission_dialog_cancel)
    ): SmartPermissions {
        this.options = DialogOptions(title, content, negativeButton)
        return this
    }

    /**
     * 如果没配置弹窗提示，会自动弹窗提示
     */
    fun requestAuto(
        activity: FragmentActivity,
        callback: PermissionCallback
    ) {
        val list = ArrayList<String>()
        list.addAll(permissions)
        if (options == null) {
            this.options = DialogOptions("", "")
        }
        requestPermissions(activity, list, options, callback)
    }

    fun request(
        activity: FragmentActivity,
        callback: PermissionCallback
    ) {
        val list = ArrayList<String>()
        list.addAll(permissions)
        requestPermissions(activity, list, options, callback)
    }

    private fun requestPermissions(
        activity: FragmentActivity,
        permissions: List<String>,
        options: DialogOptions?,
        callback: PermissionCallback
    ) {
        val arrayList = permissions.toMutableList()
        //根据版本优化权限
        optimizeDeprecatedPermission(activity, arrayList)

        val deniedPermissions = PermissionUtils.findDeniedPermissions(activity, arrayList)
        //无拒绝，则表示全通过
        if (deniedPermissions.isNullOrEmpty()) {
            callback.onGranted(permissions)
            return
        }

        //尝试申请
        PermissionFragment.requestPermissions(activity, ArrayList(arrayList), options, callback)
    }

    /**
     * 处理和优化已经过时的权限
     *
     * @param requestPermissions 请求的权限组
     */

    private fun optimizeDeprecatedPermission(
        activity: FragmentActivity,
        requestPermissions: MutableList<String>
    ) {
        // 如果本次申请包含了 Android 11 存储权限
//            if (requestPermissions!!.contains(Manifest.permission.MANAGE_EXTERNAL_STORAGE)) {
//                if (requestPermissions.contains(Manifest.permission.READ_EXTERNAL_STORAGE) ||
//                    requestPermissions.contains(Manifest.permission.WRITE_EXTERNAL_STORAGE)
//                ) {
//                    // 检测是否有旧版的存储权限，有的话直接抛出异常，请不要自己动态申请这两个权限
//                    throw IllegalArgumentException("Please do not apply for these two permissions dynamically")
//                }
//            }
        if (!PermissionUtils.isAndroid8 &&
            requestPermissions.contains(Manifest.permission.READ_PHONE_NUMBERS) &&
            !requestPermissions.contains(Manifest.permission.READ_PHONE_STATE)
        ) {
            // 自动添加旧版的读取电话号码权限，因为旧版的系统不支持申请新版的权限
            requestPermissions.add(Manifest.permission.READ_PHONE_STATE)
        }
        requestPermissions.apply {
            // target33适配：
            // Android11及以上设备已经不需要WRITE_EXTERNAL_STORAGE权限，即使申请也是默认永久拒绝
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R && this.contains(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                && activity.applicationInfo.targetSdkVersion > 28
            ) {
                remove(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            }
            // Android13及以上设备，针对READ_EXTERNAL_STORAGE媒体权限拆解
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU
                && activity.applicationInfo.targetSdkVersion >= 33
                && this.contains(Manifest.permission.READ_EXTERNAL_STORAGE)
            ) {
                remove(Manifest.permission.READ_EXTERNAL_STORAGE)
                add(Manifest.permission.READ_MEDIA_IMAGES)
                add(Manifest.permission.READ_MEDIA_VIDEO)
                add(Manifest.permission.READ_MEDIA_AUDIO)
            }
        }
    }

}