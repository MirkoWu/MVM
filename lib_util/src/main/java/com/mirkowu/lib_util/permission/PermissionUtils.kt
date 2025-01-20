package com.mirkowu.lib_util.permission

import android.Manifest
import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.app.Activity
import android.app.AppOpsManager
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.AssetManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.text.TextUtils
import androidx.fragment.app.Fragment
import com.mirkowu.lib_util.utilcode.util.ArrayUtils.asArrayList
import java.lang.reflect.Field
import java.lang.reflect.InvocationTargetException
import java.lang.reflect.Method

object PermissionUtils {

    fun isSpecialPermission(permissions: ArrayList<String>): Boolean {
        for ((index, item) in permissions.withIndex()) {
            if (Permissions.SPECIAL_PERMISSION.contains(item)) {
                return true
            }
        }
        return false
    }


    @JvmStatic
    fun smartGotoAppSettingPage(context: Context, deniedPermissions: List<String>?) {
        try {
            context.startActivity(getSmartPermissionIntent(context, deniedPermissions))
        } catch (t: Throwable) {
            //do nothing
        }
    }

    fun smartGotoAppSettingPageForResult(
        context: Activity,
        requestCode: Int,
        deniedPermissions: List<String>?
    ) {
        try {
            context.startActivityForResult(
                getSmartPermissionIntent(context, deniedPermissions),
                requestCode
            )
        } catch (t: Throwable) {
            //do nothing
        }
    }


    fun smartGotoAppSettingPageForResult(
        fragment: Fragment,
        requestCode: Int,
        deniedPermissions: List<String>?
    ) {
        try {
            fragment.startActivityForResult(
                getSmartPermissionIntent(fragment.requireContext(), deniedPermissions),
                requestCode
            )
        } catch (t: Throwable) {
            t.printStackTrace()
            //do nothing
        }
    }

    /**
     * 根据传入的权限自动选择最合适的权限设置页
     */
    fun getSmartPermissionIntent(context: Context, deniedPermissions: List<String>?): Intent {
        // 如果失败的权限里面不包含特殊权限
        if (deniedPermissions == null || deniedPermissions.isEmpty()) {
            return getApplicationDetailsIntent(context)
        }

        // 如果当前只有一个权限被拒绝了
        if (deniedPermissions.size == 1) {
            val permission: String = deniedPermissions[0]
            if ((Manifest.permission.MANAGE_EXTERNAL_STORAGE == permission)) {
                return getStoragePermissionIntent(context)
            }
            if ((Manifest.permission.REQUEST_INSTALL_PACKAGES == permission)) {
                return getInstallPermissionIntent(context)
            }
            if ((Manifest.permission.SYSTEM_ALERT_WINDOW == permission)) {
                return getWindowPermissionIntent(context)
            }
            if ((Permissions.NOTIFICATION_SERVICE == permission)) {
                return getNotifyPermissionIntent(context)
            }
            if ((Manifest.permission.WRITE_SETTINGS == permission)) {
                return getSettingPermissionIntent(context)
            }
        }
        return getApplicationDetailsIntent(context)
    }

    /**
     * 获取应用详情界面意图
     */
    fun getApplicationDetailsIntent(context: Context): Intent {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        intent.data = getPackageNameUri(context)
        return intent
    }

    /**
     * 获取安装权限设置界面意图
     */
    fun getInstallPermissionIntent(context: Context): Intent {
        var intent: Intent? = null
        if (PermissionUtils.isAndroid8) {
            intent = Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES)
            intent.data = getPackageNameUri(context)
        }
        if (intent == null || !areActivityIntent(context, intent)) {
            intent = getApplicationDetailsIntent(context)
        }
        return intent
    }

    /**
     * 获取悬浮窗权限设置界面意图
     */
    fun getWindowPermissionIntent(context: Context): Intent {
        var intent: Intent? = null
        if (intent == null || !areActivityIntent(context, intent)) {
            intent = getApplicationDetailsIntent(context)
        }
        return intent
    }

    /**
     * 获取通知栏权限设置界面意图
     */
    fun getNotifyPermissionIntent(context: Context): Intent {
        var intent: Intent? = null
        if (PermissionUtils.isAndroid8) {
            intent = Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS)
            intent.putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)
            //intent.putExtra(Settings.EXTRA_CHANNEL_ID, context.getApplicationInfo().uid);
        }
        if (intent == null || !areActivityIntent(context, intent)) {
            intent = getApplicationDetailsIntent(context)
        }
        return intent
    }

    /**
     * 获取系统设置权限界面意图
     */
    fun getSettingPermissionIntent(context: Context): Intent {
        var intent: Intent? = null
        if (intent == null || !areActivityIntent(context, intent)) {
            intent = getApplicationDetailsIntent(context)
        }
        return intent
    }

    /**
     * 获取存储权限设置界面意图
     */
    fun getStoragePermissionIntent(context: Context): Intent {
        var intent: Intent? = null
        if (intent == null || !areActivityIntent(context, intent)) {
            intent = getApplicationDetailsIntent(context)
        }
        return intent
    }

    /**
     * 判断这个意图的 Activity 是否存在
     */
    @SuppressLint("QueryPermissionsNeeded")
    private fun areActivityIntent(context: Context, intent: Intent): Boolean {
        return context.packageManager
            .queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY).isNotEmpty()
    }

    /**
     * 获取包名 Uri 对象
     */
    private fun getPackageNameUri(context: Context): Uri {
        return Uri.parse("package:" + context.packageName)
    }

    /**
     * 是否是 Android 9.0 及以上版本
     */
    val isAndroid9: Boolean
        get() {
            return Build.VERSION.SDK_INT >= Build.VERSION_CODES.P
        }

    /**
     * 是否是 Android 8.0 及以上版本
     */
    val isAndroid8: Boolean
        get() {
            return Build.VERSION.SDK_INT >= Build.VERSION_CODES.O
        }

    /**
     * 是否是 Android 7.0 及以上版本
     */
    val isAndroid7: Boolean
        get() {
            return Build.VERSION.SDK_INT >= Build.VERSION_CODES.N
        }

    /**
     * 是否是 Android 6.0 及以上版本
     */
    val isAndroid6: Boolean
        get() {
            return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
        }


    /**
     * 返回应用程序在清单文件中注册的权限
     */
    fun getManifestPermissions(context: Context): List<String>? {
        return try {
            val requestedPermissions: Array<String> = context.packageManager.getPackageInfo(
                context.packageName,
                PackageManager.GET_PERMISSIONS
            ).requestedPermissions
            // 当清单文件没有注册任何权限的时候，那么这个数组对象就是空的
            // https://github.com/getActivity/XXPermissions/issues/35
            asArrayList(*requestedPermissions)
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
            null
        }
    }

    /**
     * 是否有存储权限
     */
    fun isGrantedStoragePermission(context: Context?): Boolean {
        return isGrantedPermissions(context, asArrayList(*Permissions.GROUP_STORAGE))
    }

    /**
     * 是否有安装权限
     */
    fun isGrantedInstallPermission(context: Context?): Boolean {
        if (isAndroid8) {
            return context!!.packageManager.canRequestPackageInstalls()
        }
        return true
    }

    /**
     * 是否有悬浮窗权限
     */
    fun isGrantedWindowPermission(context: Context?): Boolean {
        if (isAndroid6) {
            return Settings.canDrawOverlays(context)
        }
        return true
    }

    /**
     * 是否有通知栏权限
     */
    fun isGrantedNotifyPermission(context: Context?): Boolean {
        if (isAndroid7) {
            return context!!.getSystemService(NotificationManager::class.java)
                .areNotificationsEnabled()
        }
        if (isAndroid6) {
            // 参考 Support 库中的方法： NotificationManagerCompat.from(context).areNotificationsEnabled()
            val appOps: AppOpsManager =
                context!!.getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
            try {
                val method: Method = appOps.javaClass.getMethod(
                    "checkOpNoThrow",
                    Integer.TYPE,
                    Integer.TYPE,
                    String::class.java
                )
                val field: Field = appOps.javaClass.getDeclaredField("OP_POST_NOTIFICATION")
                val value: Int = field.get(Int::class.java) as Int
                return (method.invoke(
                    appOps,
                    value,
                    context.applicationInfo.uid,
                    context.packageName
                ) as Int) == AppOpsManager.MODE_ALLOWED
            } catch (e: NoSuchMethodException) {
                e.printStackTrace()
                return true
            } catch (e: NoSuchFieldException) {
                e.printStackTrace()
                return true
            } catch (e: InvocationTargetException) {
                e.printStackTrace()
                return true
            } catch (e: IllegalAccessException) {
                e.printStackTrace()
                return true
            } catch (e: RuntimeException) {
                e.printStackTrace()
                return true
            }
        }
        return true
    }

    /**
     * 是否有系统设置权限
     */
    fun isGrantedSettingPermission(context: Context?): Boolean {
        if (isAndroid6) {
            return Settings.System.canWrite(context)
        }
        return true
    }

    /**
     * 判断某个权限集合是否包含特殊权限
     */
    fun containsSpecialPermission(permissions: List<String?>?): Boolean {
        if (permissions == null || permissions.isEmpty()) {
            return false
        }
        for (permission: String? in permissions) {
            if (isSpecialPermission(permission)) {
                return true
            }
        }
        return false
    }

    /**
     * 判断某个权限是否是特殊权限
     */
    fun isSpecialPermission(permission: String?): Boolean {
        return ((Manifest.permission.MANAGE_EXTERNAL_STORAGE == permission) || (Manifest.permission.REQUEST_INSTALL_PACKAGES == permission) || (Manifest.permission.SYSTEM_ALERT_WINDOW == permission) || (Permissions.NOTIFICATION_SERVICE == permission) || (Manifest.permission.WRITE_SETTINGS == permission))
    }

    /**
     * 判断某些权限是否全部被授予
     */
    fun isGrantedPermissions(context: Context?, permissions: List<String?>?): Boolean {
        // 如果是安卓 6.0 以下版本就直接返回 true
        if (!isAndroid6) {
            return true
        }
        if (permissions == null || permissions.isEmpty()) {
            return false
        }
        for (permission: String? in permissions) {
            if (!isGrantedPermission(context, permission)) {
                return false
            }
        }
        return true
    }

    @TargetApi(value = Build.VERSION_CODES.M)
    fun findDeniedPermissions(activity: Activity, permission: List<String>?): List<String> {
        val denyPermissions: MutableList<String> = java.util.ArrayList()
        for (value: String? in permission!!) {
            if (activity.checkSelfPermission((value)!!) != PackageManager.PERMISSION_GRANTED) {
                denyPermissions.add(value)
            }
        }
        return denyPermissions
    }

    /**
     * 获取没有授予的权限
     */
    fun getDeniedPermissions(context: Context?, permissions: List<String>?): List<String> {
        val deniedPermission: MutableList<String> = java.util.ArrayList(
            permissions!!.size
        )

        // 如果是安卓 6.0 以下版本就默认授予
        if (!isAndroid6) {
            return deniedPermission
        }
        for (permission: String in permissions) {
            if (!isGrantedPermission(context, permission)) {
                deniedPermission.add(permission)
            }
        }
        return deniedPermission
    }

    /**
     * 判断某个权限是否授予
     */
    fun isGrantedPermission(context: Context?, permission: String?): Boolean {
        // 如果是安卓 6.0 以下版本就默认授予
        if (!isAndroid6) {
            return true
        }

        // 检测存储权限
        if ((Manifest.permission.MANAGE_EXTERNAL_STORAGE == permission)) {
            return isGrantedStoragePermission(context)
        }

        // 检测安装权限
        if ((Manifest.permission.REQUEST_INSTALL_PACKAGES == permission)) {
            return isGrantedInstallPermission(context)
        }

        // 检测悬浮窗权限
        if ((Manifest.permission.SYSTEM_ALERT_WINDOW == permission)) {
            return isGrantedWindowPermission(context)
        }

        // 检测通知栏权限
        if ((Permissions.NOTIFICATION_SERVICE == permission)) {
            return isGrantedNotifyPermission(context)
        }

        // 检测系统权限
        if ((Manifest.permission.WRITE_SETTINGS == permission)) {
            return isGrantedSettingPermission(context)
        }

        // 检测 9.0 的一个新权限
        if (!isAndroid9) {
            if ((Manifest.permission.ACCEPT_HANDOVER == permission)) {
                return true
            }
        }

        // 检测 8.0 的两个新权限
        if (!isAndroid8) {
            if ((Manifest.permission.ANSWER_PHONE_CALLS == permission)) {
                return true
            }
            if ((Manifest.permission.READ_PHONE_NUMBERS == permission)) {
                return context!!.checkSelfPermission(Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED
            }
        }
        return context!!.checkSelfPermission((permission)!!) == PackageManager.PERMISSION_GRANTED
    }

    @JvmStatic
    fun hasPermissions(context: Context, vararg permissions: String): Boolean {
        return hasPermissions(context, asArrayList(*permissions))
    }

    @JvmStatic
    fun hasPermissions(context: Context, permissions: List<String>?): Boolean {
        permissions?.let {
            for (perm in permissions) {
                if (context.checkSelfPermission(perm) != PackageManager.PERMISSION_GRANTED) {
                    return false
                }
            }
        } ?: return false
        return true
    }


    /**
     * 判断某个权限是否被永久拒绝
     *
     * @param activity   Activity对象
     * @param permission 请求的权限
     */
    fun isPermissionPermanentDenied(activity: Activity?, permission: String?): Boolean {
        if (!isAndroid6) {
            return false
        }

        // 特殊权限不算，本身申请方式和危险权限申请方式不同，因为没有永久拒绝的选项，所以这里返回 false
        if (isSpecialPermission(permission)) {
            return false
        }

        // 检测 9.0 的一个新权限
        if (!isAndroid9) {
            if ((Manifest.permission.ACCEPT_HANDOVER == permission)) {
                return false
            }
        }

        // 检测 8.0 的两个新权限
        if (!isAndroid8) {
            if ((Manifest.permission.ANSWER_PHONE_CALLS == permission)) {
                return true
            }
            if ((Manifest.permission.READ_PHONE_NUMBERS == permission)) {
                return !isGrantedPermission(activity, Manifest.permission.READ_PHONE_STATE) &&
                        !activity!!.shouldShowRequestPermissionRationale(Manifest.permission.READ_PHONE_STATE)
            }
        }
        // 当用户没有授予该权限&该权限请求了但被拒绝了，下面的结果返回true
        return !isGrantedPermission(activity, permission) &&
                !activity!!.shouldShowRequestPermissionRationale((permission)!!)
    }


    /**
     * 获取某个权限的状态
     *
     * @return 已授权返回  [PackageManager.PERMISSION_GRANTED]
     * 未授权返回  [PackageManager.PERMISSION_DENIED]
     */
    fun getPermissionStatus(context: Context?, permission: String?): Int {
        return if (isGrantedPermission(
                context,
                permission
            )
        ) PackageManager.PERMISSION_GRANTED else PackageManager.PERMISSION_DENIED
    }

    /**
     * 在权限组中检查是否有某个权限是否被永久拒绝
     *
     * @param activity    Activity对象
     * @param permissions 请求的权限
     */
    fun isPermissionPermanentDenied(activity: Activity?, permissions: List<String?>?): Boolean {
        for (permission: String? in permissions!!) {
            if (isPermissionPermanentDenied(activity, permission)) {
                return true
            }
        }
        return false
    }

    /**
     * true场景：
     * 1，第一次请求权限既没有点击允许，也没有点击拒绝的情况（cancel）
     * 2，用户未在App里面请求过该权限，且提前在设置里面拒绝了权限
     * 3，用户第二次拒绝时
     */
    fun isPermissionsCanceled(activity: Activity, permissions: List<String>?): Boolean {
        permissions?.forEach { permission ->
            //只要有一个点击了拒绝或者允许都不算cancel
            if (!activity.shouldShowRequestPermissionRationale(permission) && activity.checkSelfPermission(
                    permission
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return true
            }
        }
        return false
    }

    /**
     * 获取没有授予的权限
     *
     * @param permissions  需要请求的权限组
     * @param grantResults 允许结果组
     */
    fun getDeniedPermissions(permissions: Array<String>, grantResults: IntArray): List<String> {
        val deniedPermissions: MutableList<String> = java.util.ArrayList()
        for (i in grantResults.indices) {
            // 把没有授予过的权限加入到集合中
            if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
                deniedPermissions.add(permissions[i])
            }
        }
        return deniedPermissions
    }

    /**
     * 获取已授予的权限
     *
     * @param permissions  需要请求的权限组
     * @param grantResults 允许结果组
     */
    fun getGrantedPermissions(permissions: Array<String>, grantResults: IntArray): List<String> {
        val grantedPermissions: MutableList<String> = java.util.ArrayList()
        for (i in grantResults.indices) {
            // 把授予过的权限加入到集合中
            if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                grantedPermissions.add(permissions[i])
            }
        }
        return grantedPermissions
    }

    fun <T> asArrayLists(vararg arrays: Array<T>): java.util.ArrayList<T> {
        val list: java.util.ArrayList<T> = java.util.ArrayList()
        if (arrays == null || arrays.isEmpty()) {
            return list
        }
        for (ts: Array<T> in arrays) {
            list.addAll((asArrayList(*ts))!!)
        }
        return list
    }

    /**
     * 获取当前应用 Apk 在 AssetManager 中的 Cookie
     */
    @SuppressLint("PrivateApi", "BlockedPrivateApi")
    fun findApkPathCookie(context: Context): Int {
        val assets: AssetManager = context.assets
        val path: String = context.applicationInfo.sourceDir
        var cookie = 0
        try {
            try {
                // 为什么不直接通过反射 AssetManager.findCookieForPath 方法来判断？因为这个 API 属于反射黑名单，反射执行不了
                val method: Method =
                    assets.javaClass.getDeclaredMethod("addOverlayPath", String::class.java)
                cookie = method.invoke(assets, path) as Int
            } catch (e: Exception) {
                // NoSuchMethodException
                // IllegalAccessException
                // InvocationTargetException
                e.printStackTrace()
                val method: Method = assets.javaClass.getDeclaredMethod("getApkPaths")
                val apkPaths: Array<String> =
                    method.invoke(assets) as Array<String>? ?: return cookie
                var i = 0
                while (i < apkPaths.size) {
                    if ((apkPaths[i] == path)) {
                        cookie = i + 1
                        break
                    }
                    i++
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return cookie
    }

    fun isLocationPermission(permission: String?): Boolean {
        return TextUtils.equals(Manifest.permission.ACCESS_FINE_LOCATION, permission) ||
                TextUtils.equals(Manifest.permission.ACCESS_COARSE_LOCATION, permission)
    }

    fun isStoragePermission(permission: String?): Boolean {
        return TextUtils.equals(Manifest.permission.READ_EXTERNAL_STORAGE, permission) ||
                TextUtils.equals(Manifest.permission.WRITE_EXTERNAL_STORAGE, permission)
    }

    fun isReadMediaPermission(permission: String?): Boolean {
        return TextUtils.equals(Manifest.permission.READ_MEDIA_IMAGES, permission) ||
                TextUtils.equals(Manifest.permission.READ_MEDIA_VIDEO, permission) ||
                TextUtils.equals(Manifest.permission.READ_MEDIA_AUDIO, permission)
    }

    fun isBluetoothPermission(permission: String?): Boolean {
        return TextUtils.equals(Manifest.permission.BLUETOOTH, permission) ||
                TextUtils.equals(Manifest.permission.BLUETOOTH_SCAN, permission) ||
                TextUtils.equals(Manifest.permission.BLUETOOTH_ADVERTISE, permission) ||
                TextUtils.equals(Manifest.permission.BLUETOOTH_CONNECT, permission) ||
                TextUtils.equals(Manifest.permission.BLUETOOTH_ADMIN, permission) ||
                TextUtils.equals(Manifest.permission.BLUETOOTH_PRIVILEGED, permission)
    }

    fun isAudioPermission(permission: String?): Boolean {
        return TextUtils.equals(Manifest.permission.RECORD_AUDIO, permission) ||
                TextUtils.equals(Manifest.permission.MODIFY_AUDIO_SETTINGS, permission)
    }

//    fun isCalendarPermission(permission: String?): Boolean {
//        return TextUtils.equals(Manifest.permission.READ_CALENDAR, permission) ||
//                TextUtils.equals(Manifest.permission.WRITE_CALENDAR, permission) ||
//                TextUtils.equals(PER_OPPO_PRIVATE_CAL_READ_CALENDAR, permission) ||
//                TextUtils.equals(PER_OPPO_PRIVATE_CAL_WRITE_CALENDAR, permission)
//    }

//    fun goSettingWithPermissions(context: Context, permissions: List<String>?) {
//        if (permissions.isNullOrEmpty()) {
//            goToPermissionSetting(context)
//        } else {
//            when (permissions[0]) {
//                Manifest.permission.POST_NOTIFICATIONS -> PermissionSettingUtils.gotoNotificationSetting(
//                    context
//                )
//
//                else -> PermissionSettingUtils.goToPermissionSetting(context)
//            }
//        }
//    }

    /**
     * 统一处理相册权限的申请
     * Android13以上读取权限做了拆分，默认只读取图片和视频
     * 为什么相册需要camera权限，是因为Lg相册里带了相机功能
     */
    fun getPhotoPermission(): Array<String> {
        return when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU -> {
                arrayOf(
                    Manifest.permission.READ_MEDIA_IMAGES,
                    Manifest.permission.READ_MEDIA_VIDEO,
                    Manifest.permission.CAMERA
                )
            }

            Build.VERSION.SDK_INT >= Build.VERSION_CODES.R -> {
                arrayOf(
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.CAMERA
                )
            }

            else -> {
                arrayOf(
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.CAMERA
                )
            }
        }
    }

    /**
     * 统一处理相机权限的申请
     * LgCamera在Android11以上不需要存储权限
     */
    fun getCameraPermission(): Array<String> {
        return if (Build.VERSION.SDK_INT <= 29) {
            arrayOf(
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA
            )
        } else {
            arrayOf(Manifest.permission.CAMERA)
        }
    }

    /**
     * 统一处理录制音频权限的申请
     * 如果录制的音频不需要发送到MediaStore，单独使用RECORD_AUDIO权限即可，不要用此方法
     */
    fun getRecordAudioPermission(): Array<String> {
        return return if (Build.VERSION.SDK_INT <= 29) {
            arrayOf(
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.RECORD_AUDIO
            )
        } else {
            arrayOf(
                Manifest.permission.RECORD_AUDIO
            )
        }
    }

    /**
     * 统一处理录制视频权限的申请
     * 如果录制的视频不需要更新到MediaStore，使用RECORD_AUDIO和CAMERA权限即可，不要用此方法
     */
    fun getRecordVideoPermission(): Array<String> {
        return if (Build.VERSION.SDK_INT <= 29) {
            arrayOf(
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA,
                Manifest.permission.RECORD_AUDIO
            )
        } else {
            arrayOf(
                Manifest.permission.CAMERA,
                Manifest.permission.RECORD_AUDIO
            )
        }
    }

    fun isHarmonyOS(): Boolean {
        return try {
            val clz = Class.forName("com.huawei.system.BuildEx")
            val method = clz.getMethod("getOsBrand")
            "harmony" == method.invoke(clz)
        } catch (t: Throwable) {
            false
        }
    }

}