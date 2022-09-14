package com.mirkowu.mvm.ui.mvvm.viewbinding

import com.mirkowu.lib_base.util.bindingView
import com.mirkowu.lib_network.state.observeRequest
import com.mirkowu.lib_util.LogUtil
import com.mirkowu.mvm.base.BaseActivity
import com.mirkowu.mvm.databinding.ActivityDataBindingBinding

class DataBindingActivity : BaseActivity<DBMediator>() {

    val binding by bindingView(ActivityDataBindingBinding::inflate)

    override fun bindContentView() {
        binding.root
    }

    override fun getLayoutId() = 0

    override fun initialize() {
        //  binding.btnTest.text = "sssss"

        mMediator.mImageListData.observeRequest(this) {
            onLoading { }
            onSuccess { LogUtil.d("onSuccess${it}") }
            onFailure { LogUtil.d("onFailure1111") }
            onFailure { LogUtil.d("onFailure2222${it}") } //注意只会执行最后重新的方法
            onFinish { }
        }
        mMediator.loadImage(1, 10)
    }
}