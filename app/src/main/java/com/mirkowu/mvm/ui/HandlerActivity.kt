package com.mirkowu.mvm.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Message
import androidx.appcompat.app.AppCompatActivity
import com.mirkowu.lib_util.LogUtil
import com.mirkowu.mvm.R

class HandlerActivity : AppCompatActivity() {
    companion object {
        const val TAG = "HandlerTest"

        @JvmStatic
        fun start(context: Context) {
            val starter = Intent(context, HandlerActivity::class.java)
//                .putExtra()
            context.startActivity(starter)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_handler)

//        Trace.beginSection();
//        Trace.endAsyncSection()
    val handler=    object : Handler(object : Callback {
            override fun handleMessage(msg: Message): Boolean {
                LogUtil.e(TAG, "Callback handleMessage")
                return false //true拦截消息  false 不拦截消息
            }

        }) {
            override fun handleMessage(msg: Message) {
                super.handleMessage(msg)
                LogUtil.e(TAG, "Handler  handleMessage")
            }
        }
        handler.sendMessage(Message.obtain())
        handler.post {

        }
    }
}