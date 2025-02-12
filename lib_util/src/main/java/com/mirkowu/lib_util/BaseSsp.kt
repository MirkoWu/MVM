package com.mirkowu.lib_util

import android.content.Context
import android.text.TextUtils
import com.google.gson.reflect.TypeToken
import com.mirkowu.lib_util.utilcode.util.EncryptUtils
import com.mirkowu.lib_util.utilcode.util.GsonUtils

abstract class BaseSsp protected constructor(context: Context?) {
    protected open val encrype_key = "ghueagoij83iuiu2"
    protected open val encrype_iv = "0000000000000000"
    protected open val encrype_transformation = "AES/CBC/PKCS5Padding"
    abstract val name: String?
    abstract fun putBoolean(key: String, value: Boolean)
    abstract fun getBoolean(key: String, defaultValue: Boolean): Boolean
    abstract fun putLong(key: String, value: Long)
    abstract fun getLong(key: String, defaultValue: Long): Long
    abstract fun putInt(key: String, value: Int)
    abstract fun getInt(key: String, defaultValue: Int): Int
    abstract fun putString(key: String?, value: String?)
    abstract fun getString(key: String, defaultValue: String?): String?
    protected abstract fun onRemove(key: String?)
    protected abstract fun onClear()
    fun getBoolean(key: String): Boolean {
        return getBoolean(key, false)
    }

    fun getLong(key: String): Long {
        return getLong(key, 0)
    }

    fun getInt(key: String): Int {
        return getInt(key, 0)
    }

    fun getString(key: String): String? {
        return getString(key, "")
    }

    @Synchronized
    fun putEncryptString(key: String?, value: String?) {
        if (TextUtils.isEmpty(key)) {
            return
        }
        var encryptValue: String? = value
        if (value != null) {
            encryptValue = EncryptUtils.encryptAES2HexString(
                value.toByteArray(),
                encrype_key.toByteArray(),
                encrype_transformation,
                encrype_iv.toByteArray()
            )
        }
        putString(key, encryptValue)
    }

    @Synchronized
    fun getEncryptString(key: String): String? {
        if (TextUtils.isEmpty(key)) {
            return ""
        }
        var plainValue: String?
        val cipherValue: String? = getString(key)
        if (!TextUtils.isEmpty(cipherValue)) {
            plainValue = EncryptUtils.decryptHexStringAES(
                cipherValue,
                encrype_key.toByteArray(),
                encrype_transformation,
                encrype_iv.toByteArray()
            ).toString()

        } else {
            plainValue = cipherValue
        }
        return plainValue
    }

    fun putObject(key: String?, value: Any?) {
        val jsonValue: String? = GsonUtils.toJson(value)
        putString(key, jsonValue)
    }

    fun <T> getObject(key: String, clazz: Class<T>): T? {
        val jsonValue: String? = getString(key)
        try {
            return GsonUtils.fromJson(jsonValue, clazz)
        } catch (e: Exception) {
            remove(key)
            return null
        }
    }

    fun <T> getObject(key: String, typeOfT: TypeToken<T>): T? {
        val jsonValue: String? = getString(key)
        try {
            return GsonUtils.fromJson(jsonValue, typeOfT.getType())
        } catch (e: Exception) {
            remove(key)
            return null
        }
    }

    fun putEncryptObject(key: String?, value: Any?) {
        val jsonValue: String? = GsonUtils.toJson(value)
        putEncryptString(key, jsonValue)
    }

    fun <T> getEncryptObject(key: String, clazz: Class<T>): T? {
        val jsonValue: String? = getEncryptString(key)
        try {
            return GsonUtils.fromJson(jsonValue, clazz)
        } catch (e: Exception) {
            remove(key)
            return null
        }
    }

    fun <T> getEncryptObject(key: String, typeOfT: TypeToken<T>): T? {
        val jsonValue: String? = getEncryptString(key)
        try {
            return GsonUtils.fromJson(jsonValue, typeOfT.getType())
        } catch (e: Exception) {
            remove(key)
            return null
        }
    }

    abstract operator fun contains(key: String): Boolean
    abstract val all: Map<String, *>?
    fun remove(key: String?) {
        onRemove(key)
    }

    fun clear() {
        onClear()
    }
}