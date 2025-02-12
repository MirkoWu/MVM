package com.mirkowu.lib_util

import android.text.TextUtils
import com.mirkowu.lib_util.utilcode.util.Utils
import com.tencent.mmkv.MMKV

class MmkvStorage : BaseSsp {
    private var mmkv: MMKV? = null
    override var name: String? = null
        private set

    constructor(spName: String?) : this(
        spName,
        MMKV.SINGLE_PROCESS_MODE
    )

    constructor(spName: String?, mode: Int) : super(Utils.getApp()) {
        MMKV.initialize(Utils.getApp())
        if (TextUtils.isEmpty(spName)) {
            mmkv = MMKV.defaultMMKV(mode, null)
            name = MMKV.getRootDir()
        } else {
            mmkv = MMKV.mmkvWithID(spName, mode)
            name = spName
        }
    }

    public override fun putBoolean(key: String, value: Boolean) {
        mmkv!!.encode(key, value)
    }

    public override fun getBoolean(key: String, defaultValue: Boolean): Boolean {
        return mmkv!!.decodeBool(key, defaultValue)
    }

    public override fun putLong(key: String, value: Long) {
        mmkv!!.putLong(key, value)
    }

    public override fun getLong(key: String, defaultValue: Long): Long {
        return mmkv!!.getLong(key, defaultValue)
    }

    public override fun putInt(key: String, value: Int) {
        mmkv!!.putInt(key, value)
    }

    public override fun getInt(key: String, defaultValue: Int): Int {
        return mmkv!!.getInt(key, defaultValue)
    }

    public override fun putString(key: String?, value: String?) {
        mmkv!!.putString(key, value)
    }

    public override fun getString(key: String, defaultValue: String?): String? {
        return mmkv!!.getString(key, defaultValue)
    }

    public override fun contains(key: String): Boolean {
        return mmkv!!.contains(key)
    }

    override val all: Map<String, *>
        get() {
            return mmkv!!.all
        }

    override fun onRemove(key: String?) {
        mmkv!!.remove(key)
    }

    override fun onClear() {
        mmkv!!.clear()
    }
}