package com.mirkowu.mvm.mvvm.viewbinding

import com.mirkowu.lib_base.util.bindingView
import com.mirkowu.lib_network.state.observerRequest
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

        mMediator.mImageListData.observerRequest(this,
                onSuccess = {
                    LogUtil.d("onSuccess")
                },
                onFailure = {
                    LogUtil.d("onFailure")
                }
        )

        mMediator.loadImage(1, 10)
    }
}