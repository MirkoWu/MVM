package com.mirkowu.mvm

import android.Manifest
import android.content.Context
import android.content.Intent
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
import com.mirkowu.mvm.mvc.MVCActivity
import com.mirkowu.mvm.mvp.MVPActivity
import com.mirkowu.mvm.mvvm.MVVMActivity
import com.mirkowu.mvm.recycelerview.GridListActivity
import com.mirkowu.mvm.mvvm.viewbinding.DataBindingActivity
import com.mirkowu.mvm.mvvm.viewbinding.DataBindingFragment
import com.mirkowu.mvm.webview.WebActivity

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
        MVVMActivity.start(this)
    }

    fun bindingClick(view: View?) {
        startActivity(Intent(this, DataBindingActivity::class.java))
    }

    fun listClick(view: View?) {
//        LinearListActivity.start(this)
        GridListActivity.start(this)
    }

    override fun onDestroy() {
        pagerAdapter.clear()
        BuglyManager.removeUpgradeListener()
        super.onDestroy()
    }

}