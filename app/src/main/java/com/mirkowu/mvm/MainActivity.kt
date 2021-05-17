package com.mirkowu.mvm

import android.content.Intent
import android.view.View
import com.mirkowu.lib_core.adapter.FragmentBasePagerAdapter
import com.mirkowu.lib_core.mediator.EmptyMediator
import com.mirkowu.mvm.base.BaseActivity
import com.mirkowu.mvm.databinding.ActivityMainBinding
import com.mirkowu.mvm.mvc.MVCActivity
import com.mirkowu.mvm.mvp.MVPActivity
import com.mirkowu.mvm.mvvm.MVVMActivity
import com.mirkowu.mvm.viewbinding.DataBindingActivity
import com.mirkowu.mvm.viewbinding.DataBindingFragment
import com.mirkowu.mvm.viewbinding.binding

class MainActivity : BaseActivity<EmptyMediator>() {
    val binding by binding(ActivityMainBinding::inflate)
    override fun createMediator() {
//        super.createMediator();//不写中间层就不要创建了
    }

    override fun getLayoutId() = R.layout.activity_main

    override fun initialize() {
        val  pagerAdapter = FragmentBasePagerAdapter(supportFragmentManager,
                DataBindingFragment.newInstance(),
                DataBindingFragment.newInstance(),
                DataBindingFragment.newInstance(),
                DataBindingFragment.newInstance()
        )
        binding.vpHome.apply {
            adapter = pagerAdapter
            isScroll=true
            offscreenPageLimit=pagerAdapter.count
        }

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

}