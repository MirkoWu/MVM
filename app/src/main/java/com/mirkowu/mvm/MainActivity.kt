package com.mirkowu.mvm

import android.Manifest
import android.content.Intent
import android.util.Log
import android.view.View
import com.mirkowu.lib_base.adapter.FragmentBasePagerAdapter
import com.mirkowu.lib_base.mediator.EmptyMediator
import com.mirkowu.mvm.base.BaseActivity
import com.mirkowu.mvm.databinding.ActivityMainBinding
import com.mirkowu.mvm.mvp.MVPActivity
import com.mirkowu.mvm.mvvm.MVVMActivity
import com.mirkowu.mvm.recycelerview.GridListActivity
import com.mirkowu.mvm.viewbinding.DataBindingActivity
import com.mirkowu.mvm.viewbinding.DataBindingFragment
import com.mirkowu.mvm.viewbinding.binding

class MainActivity : BaseActivity<EmptyMediator>() {
    val binding by binding(ActivityMainBinding::inflate)


    override fun initMediator(): EmptyMediator {
        return super.initMediator()
    }

    override fun getLayoutId() = R.layout.activity_main

    override fun initialize() {
        val pagerAdapter = FragmentBasePagerAdapter(supportFragmentManager,
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
    }

    fun mvcClick(view: View?) {
//        MVCActivity.start(this)
//        WebViewActivity.start(context, "ces", "http://www.baidu.com/")
//        CommonWebActivity.start(context, "ces", "http://www.baid")
//        WebViewActivity.start(context, "ces", "https://x5.tencent.com/docs/questions.html")
//        WebViewActivity.start(context, "ces", "file:///android_asset/test.html")
//        WebActivity.start(context, "ces", "file:///android_asset/test.html")
        WebActivity.start(context, "ces", "http://www.baidu.com/")
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