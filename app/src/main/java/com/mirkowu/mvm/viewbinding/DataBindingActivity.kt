package com.mirkowu.mvm.viewbinding

import com.mirkowu.mvm.base.BaseActivity
import com.mirkowu.mvm.databinding.ActivityDataBindingBinding
import com.mirkowu.mvm.mvvm.MVVMMediator

class DataBindingActivity : BaseActivity<MVVMMediator>() {

    val binding by binding(ActivityDataBindingBinding::inflate)

    override fun bindContentView() {
        binding.root
    }

    override fun getLayoutId() = 0

    override fun initialize() {
        //  binding.btnTest.text = "sssss"
    }
}