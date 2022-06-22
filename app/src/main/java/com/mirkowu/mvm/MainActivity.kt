package com.mirkowu.mvm

import android.Manifest
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import com.mirkowu.lib_base.adapter.BaseFragmentPagerAdapter
import com.mirkowu.lib_base.mediator.EmptyMediator
import com.mirkowu.lib_base.util.bindingView
import com.mirkowu.lib_bugly.BuglyManager
import com.mirkowu.lib_bugly.UpgradeDialog
import com.mirkowu.lib_util.LogUtil
import com.mirkowu.lib_util.utilcode.util.BarUtils
import com.mirkowu.lib_webview.CommonWebActivity
import com.mirkowu.mvm.base.BaseActivity
import com.mirkowu.mvm.service.MyService
import com.mirkowu.mvm.ui.mvc.MVCActivity
import com.mirkowu.mvm.ui.mvp.MVPActivity
import com.mirkowu.mvm.ui.mvvm.MVVMActivity
import com.mirkowu.mvm.ui.mvvm.viewbinding.DataBindingActivity
import com.mirkowu.mvm.ui.mvvm.viewbinding.DataBindingFragment
import com.mirkowu.mvm.ui.recycelerview.GridListActivity
import com.mirkowu.mvm.ui.webview.WebActivity


class MainActivity : BaseActivity<EmptyMediator>() {
    companion object {
        fun start(context: Context) {
            val starter = Intent(context, MainActivity::class.java)
            context.startActivity(starter)
        }
    }

    val binding by bindingView(com.mirkowu.mvm.databinding.ActivityMainBinding::inflate)
    lateinit var pagerAdapter: BaseFragmentPagerAdapter

    override fun initMediator(): EmptyMediator {
        return super.initMediator()
    }

    override fun initStatusBar() {
        super.initStatusBar()
        BarUtils.transparentStatusBar(this)
    }

    override fun getLayoutId() = R.layout.activity_main

    override fun initialize() {
        LogUtil.d("点击 跳过 initialize")
        pagerAdapter =
            BaseFragmentPagerAdapter(
                supportFragmentManager,
                mutableListOf<Fragment>(
                    DataBindingFragment.newInstance(),
//                    DataBindingFragment.newInstance(),
                )
            )

        binding.vpHome.apply {
            adapter = pagerAdapter
            isScroll = true
            offscreenPageLimit = pagerAdapter.count
        }
        val PERMISSION_STORAGE = arrayOf(
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.READ_PHONE_STATE
        )

        BuglyManager.checkUpgrade { hasNewVersion, upgradeInfo ->
            Log.e("BuglyManager", "setUpgradeListener:   upgradeInfo=$upgradeInfo")
            if (upgradeInfo != null) {
                UpgradeDialog.show(supportFragmentManager, upgradeInfo)
            }
        }


        LogUtil.d("点击 跳过 initialize end---")

    }

    fun webLocalClick(view: View?) {
//        MVCActivity.start(this)
//        WebActivity.start(context, "ces", "file:///android_asset/test.html")
        WebActivity.start(context, "ces", "file:///android_asset/jsbridge_test.html")
//        WebActivity.start(context, "ces", "http://www.baidu.com")
        Log.d("WebActivity", "start: ")
    }

    fun webNetClick(view: View?) {
//        CommonWebActivity.start(context, "ces", "http://www.baid")
//        WebViewActivity.start(context, "ces", "https://x5.tencent.com/docs/questions.html")
        CommonWebActivity.start(
            context,
            "标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题",
            "http://www.baidu.com/"
        )
        Log.d("WebActivity", "start: ")
    }

    fun mvcClick(view: View?) {
        MVCActivity.start(this)
    }

    fun mvpClick(view: View?) {
        MVPActivity.start(this)
    }

    fun mvvmClick(view: View?) {
       // MVVMActivity.start(this)
        val intent=Intent(context, MVVMActivity::class.java)
//        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
         startActivityForResult(intent,1)
    }

    override fun startActivity(intent: Intent?) {
        super.startActivity(intent)
    }
    fun bindingClick(view: View?) {
        startActivity(Intent(this, DataBindingActivity::class.java))
    }

    val connection =object :ServiceConnection  {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            if (service is MyService.MyBinder) {
                service.service
            }
        }

        override fun onServiceDisconnected(name: ComponentName?) {

        }

    }
    fun listClick(view: View?) {
//        LinearListActivity.start(this)
        GridListActivity.start(this)

        val intent=Intent(this,MyService::class.java)
        bindService(intent,connection,Context.BIND_AUTO_CREATE)
    }

    override fun onDestroy() {
        pagerAdapter.clear()
        BuglyManager.removeUpgradeListener()
        unbindService(connection)
        super.onDestroy()
    }
    override fun onRestart() {
        super.onRestart()
        Log.d("LifeTest", "MainActivity onRestart: ")
    }
    override fun onStart() {
        super.onStart()
        Log.d("LifeTest", "MainActivity onStart: taskId="+this.taskId)
    }
    override fun onStop() {
        super.onStop()
        Log.d("LifeTest", "MainActivity onStop: ")
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        Log.d("LifeTest", "MainActivity onSaveInstanceState: ")
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        Log.d("LifeTest", "MainActivity onRestoreInstanceState: ")
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Log.d("LifeTest", "MainActivity onActivityResult: ")
    }
}