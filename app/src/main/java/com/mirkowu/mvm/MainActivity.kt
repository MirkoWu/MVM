package com.mirkowu.mvm

import android.Manifest
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.util.Log
import android.view.View
import com.mirkowu.lib_base.adapter.FragmentBasePagerAdapter
import com.mirkowu.lib_base.mediator.EmptyMediator
import com.mirkowu.lib_base.util.bindingView
import com.mirkowu.lib_bugly.BuglyManager
import com.mirkowu.lib_bugly.UpgradeDialog
import com.mirkowu.lib_util.utilcode.util.NetworkUtils
import com.mirkowu.lib_webview.CommonWebActivity
import com.mirkowu.mvm.base.BaseActivity
import com.mirkowu.mvm.mvc.MVCActivity
import com.mirkowu.mvm.mvp.MVPActivity
import com.mirkowu.mvm.mvvm.MVVMActivity
import com.mirkowu.mvm.recycelerview.LinearListActivity
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

        BuglyManager.checkUpgrade { hasNewVersion, upgradeInfo ->
            Log.e("BuglyManager", "setUpgradeListener:   upgradeInfo=$upgradeInfo")
            if (upgradeInfo != null) {
                UpgradeDialog.show(supportFragmentManager, upgradeInfo)
            }
        }

        var time = System.currentTimeMillis();
        NetworkUtils.isAvailableByPingAsync("baidu.com") {
            time = System.currentTimeMillis() - time
            if (it) {
                binding.tvNetworkStatus.setText("检测耗时${time}ms, 网络OK")
                binding.tvNetworkStatus.setTextColor(Color.GREEN)
//                binding.tvNetworkStatus.visibility = View.GONE
            } else {
                binding.tvNetworkStatus.setText("检测耗时${time}ms, 网络不可用")
                binding.tvNetworkStatus.setTextColor(Color.RED)
                binding.tvNetworkStatus.visibility = View.VISIBLE
            }
        }
    }

    fun webLocalClick(view: View?) {
//        MVCActivity.start(this)
        WebActivity.start(context, "ces", "file:///android_asset/test.html")
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
        LinearListActivity.start(this)
//        GridListActivity.start(this)
    }


}