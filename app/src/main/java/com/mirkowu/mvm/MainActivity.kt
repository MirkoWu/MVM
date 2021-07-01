package com.mirkowu.mvm

import android.Manifest
import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.View
import com.mirkowu.lib_base.adapter.FragmentBasePagerAdapter
import com.mirkowu.lib_base.mediator.EmptyMediator
import com.mirkowu.lib_base.util.bindingView
import com.mirkowu.lib_bugly.BuglyManager
import com.mirkowu.lib_bugly.UpgradeDialog
import com.mirkowu.lib_util.utilcode.util.ToastUtils
import com.mirkowu.mvm.base.BaseActivity
import com.mirkowu.mvm.mvp.MVPActivity
import com.mirkowu.mvm.mvvm.MVVMActivity
import com.mirkowu.mvm.recycelerview.GridListActivity
import com.mirkowu.mvm.viewbinding.DataBindingActivity
import com.mirkowu.mvm.viewbinding.DataBindingFragment

class MainActivity : BaseActivity<EmptyMediator>() {
    companion object {
        fun start(context: Context) {
            val starter = Intent(context, MainActivity::class.java)
            context.startActivity(starter)
        }
    }

    val binding by bindingView(com.mirkowu.mvm.databinding.ActivityMainBinding::inflate)


    override fun initMediator(): EmptyMediator {
        return super.initMediator()
    }

    override fun getLayoutId() = R.layout.activity_main

    override fun initialize() {
        val pagerAdapter =
            FragmentBasePagerAdapter(
                supportFragmentManager,
                DataBindingFragment.newInstance(),
                DataBindingFragment.newInstance(),
                DataBindingFragment.newInstance(),
                DataBindingFragment.newInstance()
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

        BuglyManager.setOnUpgradeListener { upgradeInfo, isManual ->
            Log.e(DataBindingFragment.TAG, "setUpgradeListener:   upgradeInfo=$upgradeInfo")

            if (upgradeInfo != null) {
                UpgradeDialog.show(supportFragmentManager, upgradeInfo)
            } else if (isManual) {
                ToastUtils.showShort("当前已是最新版本!")
            }
        }
        // Beta.upgradeStateListener
        // BuglyManager.checkUpgrade(false,false)
    }

    fun webLocalClick(view: View?) {
//        MVCActivity.start(this)
        WebActivity.start(context, "ces", "file:///android_asset/test.html")
        Log.d("WebActivity", "start: ")
    }

    fun webNetClick(view: View?) {
//        CommonWebActivity.start(context, "ces", "http://www.baid")
//        WebViewActivity.start(context, "ces", "https://x5.tencent.com/docs/questions.html")
        WebActivity.start(context, "标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题", "http://www.baidu.com/")
        Log.d("WebActivity", "start: ")
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
        GridListActivity.start(this)
    }


}