package com.mirkowu.mvm.webview

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.webkit.JavascriptInterface
import com.mirkowu.lib_util.LogUtil
import com.mirkowu.lib_util.utilcode.util.ToastUtils
import com.mirkowu.lib_webview.CommonWebActivity
import com.mirkowu.lib_webview.config.WebConfig
import com.mirkowu.lib_webview.dsbridge.CompletionHandler
import com.mirkowu.lib_webview.dsbridge.OnReturnValue
import com.mirkowu.mvm.R
import org.json.JSONException
import org.json.JSONObject

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

        mWebView.addJavascriptObject(JsObject())
        mWebView.addJavascriptObject(JsObject(), "echo")
//        mWebView.settings.useWideViewPort=true
//        mWebView.settings.loadWithOverviewMode=true
    }


    fun startTimer(view: android.view.View) {
        mWebView.callHandler("startTimer", OnReturnValue<Int?> { retValue ->
            ToastUtils.showShort("返回 = $retValue")
        })
    }

    fun isRegisterXX(view: android.view.View) {
        mWebView.hasJavascriptMethod("XX", OnReturnValue<Boolean?> { retValue ->
            ToastUtils.showShort("返回 = $retValue")
        })
    }

    fun isRegisterAysnFun(view: android.view.View) {
        mWebView.hasJavascriptMethod("asynCall", OnReturnValue<Boolean?> { retValue ->
            ToastUtils.showShort("返回 = $retValue")
        })
    }

    fun isRegisterSynFun(view: android.view.View) {
        mWebView.hasJavascriptMethod("synCall", OnReturnValue<Boolean?> { retValue ->
            ToastUtils.showShort("返回 = $retValue")
        })
    }

    fun asynCall(view: android.view.View) {
        mWebView.callHandler(
            "asynCall",
            arrayOf<Any>("3", "4", "5"),
            OnReturnValue<Int?> { retValue ->
                ToastUtils.showShort("返回 = $retValue")
            })
    }

    fun synCallNoResult(view: android.view.View) {
        mWebView.callHandler("synCallNoResult", arrayOf<Any>())
    }

    fun synCall(view: android.view.View) {
        mWebView.callHandler("synCall", arrayOf<Any>(3, 4), OnReturnValue<Int?> { retValue ->
            ToastUtils.showShort("返回 = $retValue")
        })
    }

    class JsObject {
        var i = 0
        var i2 = 0


        @JavascriptInterface
        fun testSyn(msg: Any): String? {
            i++
            LogUtil.e("TAG","testSyn 次数= $i")
            return "$msg［syn call］"
        }

        @JavascriptInterface
        fun testAsyn(msg: Any, handler: CompletionHandler<String>) {
            handler.complete("$msg [ asyn call]")
            i2++
            LogUtil.e("TAG","testAsyn 次数= $i2")
        }

        @JavascriptInterface
        @Throws(JSONException::class)
        fun testNoArgSyn(arg: Any?): String? {
            LogUtil.e("TAG","无参的调用"+arg)
            return "testNoArgSyn called [ syn call]"
        }
//       @JavascriptInterface
//        @Throws(JSONException::class)
//        fun testNoArgSyn(): String? {
//            LogUtil.e("TAG","无参的调用")
//            return "testNoArgSyn called [ syn call]"
//        }

        @JavascriptInterface
        fun testNoArgAsyn(arg: Any?, handler: CompletionHandler<String>) {
            handler.complete("testNoArgAsyn   called [ asyn call]")
        }


        //@JavascriptInterface
        //without @JavascriptInterface annotation can't be called
        @Throws(JSONException::class)
        fun testNever(arg: Any): String {
            val jsonObject = arg as JSONObject
            return jsonObject.getString("msg") + "[ never call]"
        }

        @JavascriptInterface
        fun callProgress(args: Any?, handler: CompletionHandler<Int>) {
            object : CountDownTimer(11000, 1000) {
                var i = 10
                override fun onTick(millisUntilFinished: Long) {
                    //setProgressData can be called many times util complete be called.
                    handler.setProgressData(i--)
                }

                override fun onFinish() {
                    //complete the js invocation with data; handler will be invalid when complete is called
                    handler.complete(0)
                }
            }.start()
        }


        @JavascriptInterface
        @Throws(JSONException::class)
        fun syn(args: Any?): Any? {
            return args
        }

        @JavascriptInterface
        fun asyn(args: Any?, handler: CompletionHandler<Any>) {
            handler.complete(args)
        }
    }


}