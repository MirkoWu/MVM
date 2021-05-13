package com.mirkowu.mvm

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.mirkowu.mvm.base.BaseActivity
import com.mirkowu.mvm.databinding.ActivityDataBindingBinding
import com.mirkowu.mvm.mvvm.MVVMMediator
import com.mirkowu.mvm.viewbinding.binding
import com.mirkowu.mvm.viewbinding.inflate
import com.mirkowu.mvm.viewbinding.inflateBinding

class DataBindingActivity : BaseActivity<MVVMMediator>() {

//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_data_binding)
//    }

    //    val binding: ActivityDataBindingBinding by inflate()
    val binding by binding(ActivityDataBindingBinding::inflate)
    override fun bindContentView() {
//        super.bindContentView()
    }

    override fun getLayoutId() = 0

    override fun initialize() {

        binding.btnTest.text = "sssss"
    }
}