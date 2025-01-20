package com.mirkowu.lib_util.permission

import android.Manifest
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.SparseBooleanArray
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.mirkowu.lib_util.R
import java.util.Random
import kotlin.math.pow

class PermissionFragment : Fragment() {
    companion object {
        const val REQUEST_PERMISSIONS = "request_permissions"
        const val REQUEST_CODE = "request_code"

        /**
         * 权限请求码存放集合
         */
        private val REQUEST_CODE_ARRAY: SparseBooleanArray = SparseBooleanArray()

        fun requestPermissions(
            activity: FragmentActivity,
            permissions: ArrayList<String>,
            options: DialogOptions?,
            callback: PermissionCallback
        ) {

            val fragment = PermissionFragment()
            var requestCode: Int
            // 请求码随机生成，避免随机产生之前的请求码，必须进行循环判断
            do {
                requestCode = Random().nextInt(2.0.pow(8.0).toInt())
            } while (REQUEST_CODE_ARRAY.get(requestCode))
            // 标记这个请求码已经被占用
            REQUEST_CODE_ARRAY.put(requestCode, true)
            val bundle = Bundle().apply {
                putStringArrayList(REQUEST_PERMISSIONS, permissions)
                putInt(REQUEST_CODE, requestCode)
            }
            fragment.arguments = bundle
            fragment.retainInstance = true
            fragment.setCallback(callback)
            fragment.setDialogOptions(options)
            fragment.attachActivity(activity)
        }
    }

    private var permissionCallback: PermissionCallback? = null

    private var requestedPermission = false
    private var dialogOptions: DialogOptions? = null

    /**
     * 绑定 Activity
     */
    fun attachActivity(activity: FragmentActivity) {
        activity.supportFragmentManager.beginTransaction().add(this, this.toString())
            .commitAllowingStateLoss()
    }

    fun setCallback(callback: PermissionCallback) {
        this.permissionCallback = callback
    }

    fun setDialogOptions(dialogOptions: DialogOptions?) {
        this.dialogOptions = dialogOptions
    }

    /**
     * 解绑 Activity
     */
    fun detachActivity(activity: FragmentActivity) {
        activity.supportFragmentManager.beginTransaction().remove(this)
            .commitAllowingStateLoss()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
    }

    override fun onDetach() {
        super.onDetach()
    }

    override fun onResume() {
        super.onResume()
        if (requestedPermission) {
            return
        }
        requestedPermission = true
        requestPermission()
    }

    private fun requestPermission() {
        val activity = activity ?: return
        val args = arguments ?: return
        val list = args.getStringArrayList(REQUEST_PERMISSIONS)
        if (list.isNullOrEmpty()) {
            return
        }
        val requestCode = args.getInt(REQUEST_CODE, 0)

        //特殊权限需要跳转到设置界面
        if (PermissionUtils.isSpecialPermission(list)) {
            Manifest.permission.MANAGE_EXTERNAL_STORAGE.let {
                if (list.contains(it) && !PermissionUtils.isGrantedInstallPermission(activity)) {
                    gotoAppSettingPage(requestCode, listOf(it))
                    return
                }
            }
            Manifest.permission.REQUEST_INSTALL_PACKAGES.let {
                if (list.contains(it) && !PermissionUtils.isGrantedInstallPermission(activity)) {
                    gotoAppSettingPage(requestCode, listOf(it))
                    return
                }
            }
            Manifest.permission.SYSTEM_ALERT_WINDOW.let {
                if (list.contains(it) && !PermissionUtils.isGrantedWindowPermission(activity)) {
                    gotoAppSettingPage(requestCode, listOf(it))
                    return
                }
            }
            Manifest.permission.WRITE_SETTINGS.let {
                if (list.contains(it) && !PermissionUtils.isGrantedSettingPermission(activity)) {
                    gotoAppSettingPage(requestCode, listOf(it))
                    return
                }
            }

            Permissions.NOTIFICATION_SERVICE.let {
                if (list.contains(it) && !PermissionUtils.isGrantedNotifyPermission(activity)) {
                    gotoAppSettingPage(requestCode, listOf(it))
                    return
                }
            }
        }

        requestDangerousPermission(list, requestCode)
    }

    private fun gotoAppSettingPage(
        requestCode: Int,
        permissions: List<String>
    ) {
        AlertDialog.Builder(requireActivity())
            .setTitle(getString(R.string.util_permission_dialog_title))
            .setMessage(
                getString(
                    R.string.util_permission_dialog_content,
                    Permissions.getDeniedPermissionName(permissions)
                )
            )
            .setPositiveButton(R.string.util_permission_dialog_ok) { dialog, which ->
                PermissionUtils.smartGotoAppSettingPageForResult(
                    this@PermissionFragment,
                    requestCode,
                    permissions
                )
            }
            .setNegativeButton(R.string.util_permission_dialog_cancel) { dialog, which ->
                onDeniedCallback(permissions, true)
            }
            .setCancelable(false)
            .create().show()
    }

    private fun requestDangerousPermission(list: ArrayList<String>, requestCode: Int) {
        requestPermissions(list.toTypedArray(), requestCode)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        val activity = activity ?: return
        val args = arguments ?: return
        val list = args.getStringArrayList(REQUEST_PERMISSIONS)
        if (list.isNullOrEmpty()) {
            return
        }
        val code = args.getInt(REQUEST_CODE, 0)
        if (requestCode != code) {
            return
        }
        permissionCallback ?: return

        // 获取已授予的权限
        val grantedPermission: List<String> =
            PermissionUtils.getGrantedPermissions(permissions, grantResults)

        //授权通过数一致，则表示全部通过
        if (grantedPermission.size == permissions.size) {
            onGrantedCallback(grantedPermission)
        } else {
            // 获取已拒绝的权限
            val deniedPermissions = PermissionUtils.getDeniedPermissions(permissions, grantResults)

            //是否被永久拒绝
            val hasPermissionForeverDenied =
                PermissionUtils.isPermissionPermanentDenied(activity, deniedPermissions)
            dialogOptions?.apply {
                AlertDialog.Builder(activity)
                    .setTitle(if (!title.isNullOrEmpty()) title else getString(R.string.util_permission_dialog_title))
                    .setMessage(
                        if (!content.isNullOrEmpty()) content else getString(
                            R.string.util_permission_dialog_content,
                            Permissions.getDeniedPermissionName(deniedPermissions)
                        )
                    )
                    .setPositiveButton(R.string.util_permission_dialog_ok) { dialog, which ->
                        PermissionUtils.smartGotoAppSettingPageForResult(
                            this@PermissionFragment,
                            requestCode,
                            deniedPermissions
                        )
                    }
                    .setNegativeButton(negativeButton) { dialog, which ->
                        onDeniedCallback(deniedPermissions, hasPermissionForeverDenied)
                    }
                    .setCancelable(false)
                    .create().show()
            } ?: run {
                onDeniedCallback(deniedPermissions, hasPermissionForeverDenied)
            }
        }
    }


    private fun onDeniedCallback(permissions: List<String>, hasPermissionForeverDenied: Boolean) {
        val activity = activity ?: return
        val args = arguments ?: return
        val code = args.getInt(REQUEST_CODE, 0)

        permissionCallback ?: return
        val callback = permissionCallback!!
        permissionCallback = null
        // 释放对这个请求码的占用
        REQUEST_CODE_ARRAY.delete(code)
        // 将 Fragment 从 Activity 移除
        detachActivity(activity)

        callback.onDenied(permissions, hasPermissionForeverDenied)
    }

    private fun onGrantedCallback(permissions: List<String>) {
        val activity = activity ?: return
        val args = arguments ?: return
        val code = args.getInt(REQUEST_CODE, 0)

        permissionCallback ?: return
        val callback = permissionCallback!!
        permissionCallback = null
        // 释放对这个请求码的占用
        REQUEST_CODE_ARRAY.delete(code)
        // 将 Fragment 从 Activity 移除
        detachActivity(activity)

        callback.onGranted(permissions)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        val activity = activity ?: return
        val args = arguments ?: return
        val list = args.getStringArrayList(REQUEST_PERMISSIONS)
        if (list.isNullOrEmpty()) {
            return
        }
        val code = args.getInt(REQUEST_CODE, 0)
        if (requestCode != code) {
            return
        }

        //跳转设置页回来后，重新检查权限
        requestPermission()
    }

    override fun onDestroy() {
        super.onDestroy()
        permissionCallback = null
    }
}