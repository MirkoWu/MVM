package com.mirkowu.mvm.viewbinding

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.mirkowu.mvm.base.BaseActivity
import com.mirkowu.mvm.databinding.ActivityDataBindingBinding
import com.mirkowu.mvm.mvvm.MVVMMediator
import com.mirkowu.mvm.viewbinding.binding

class DataBindingActivity : BaseActivity<MVVMMediator>() {

    val binding by binding(ActivityDataBindingBinding::inflate)

    override fun bindContentView() {
     //   super.bindContentView()
        binding.root
    }

    override fun getLayoutId() = 0

    override fun initialize() {
      //  binding.btnTest.text = "sssss"
    }
}