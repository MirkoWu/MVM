package com.mirkowu.mvm.viewbinding

import android.view.View

inline fun View.click(crossinline block: (view:View) -> Unit) {
    setOnClickListener { block(it) }
}

