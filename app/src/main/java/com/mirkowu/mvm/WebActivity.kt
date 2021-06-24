package com.mirkowu.mvm

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import com.mirkowu.lib_util.utilcode.util.ToastUtils
import com.mirkowu.lib_webview.CommonWebActivity
import com.mirkowu.lib_webview.config.WebConfig
import com.mirkowu.lib_webview.jsbridge.BridgeHandler
import com.mirkowu.lib_webview.jsbridge.CallBackFunction

class WebActivity : CommonWebActivity() {


    companion object {
        fun start(context: Context, title: String, url: String) {
            val starter = Intent(context, WebActivity::class.java)
                    .putExtra(KEY_TITLE, title)
                    .putExtra(KEY_URL, url)
            context.startActivity(starter)
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("WebActivity", "onCreate: ")

    }

    override fun getLayoutId(): Int {
        return R.layout.activity_web
    }

    override fun getWebConfig(): WebConfig {
        return super.getWebConfig()
    }

    override fun initialize() {
        super.initialize()
        Log.d("WebActivity", "onCreate: ")
//        supportFragmentManager.beginTransaction()
//                .replace(R.id.fl_container,
////                        CommonWebFragment.newInstance("", "http://www.baidu.com/"))
//                        CommonWebFragment.newInstance("", "file:///android_asset/test.html"))
//                .commitAllowingStateLoss()

        mWebView.registerHandler("showToast", object : BridgeHandler {
            override fun handler(data: String?, function: CallBackFunction?) {
                ToastUtils.showShort("$data")
                function?.onCallBack("Java端返回数据")
            }

        })

    }

    fun callJsFunc(view: View) {
        mWebView.callHandler("changeText", "I`m changed " +System.currentTimeMillis(), object : CallBackFunction {
            override fun onCallBack(data: String?) {
                ToastUtils.showShort("Web返回数据 = $data")
            }
        })
    }
}