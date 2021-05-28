package com.mirkowu.mvm

import android.view.View

fun <T : View> T.click(block: (T) -> Unit) {
    setOnClickListener {
        block(this)
    }
}

/**
 * 带登录监测的点击事件
 */
private fun <T : View> T.clickDelayEnable(delay: Long = 500): Boolean {
    val tag = getTag(id)
    val lastTime = if (tag != null) tag as Long else -1
    val curTime = System.currentTimeMillis()
    val enable = curTime - lastTime > delay
    if (enable) {
        setTag(id, curTime)
    }
    return enable
}

/**
 * 防止重复点击
 */
fun <T : View> T.clickFilter(delay: Long = 500, block: (T) -> Unit) {
    setOnClickListener {
        if (clickDelayEnable(delay)) {
            block(this)
        }/* else {
            // L.i("clickFilter", "无效点击")
        }*/

    }
}

///**
// * 带登录监测的点击事件
// */
//fun <T : View> T.clickWithLogin(block: (T) -> Unit) {
//    setOnClickListener {
//        //是否登录
////        if ( ) {
//        block(it as T)
////        } else {//前往登录
////
////        }
//    }
//}