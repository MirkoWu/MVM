package com.mirkowu.mvm.ui.mvvm.viewbinding

import com.mirkowu.lib_base.util.bindingView
import com.mirkowu.lib_network.request.flow.request
import com.mirkowu.lib_util.LogUtil
import com.mirkowu.mvm.base.BaseActivity
import com.mirkowu.mvm.databinding.ActivityDataBindingBinding

class DataBindingActivity : BaseActivity<DBMediator>() {

    val binding by bindingView(ActivityDataBindingBinding::inflate)

    override fun bindContentView() {
//        binding.root
    }

    override fun getLayoutId() = 0

    override fun initialize() {
        //  binding.btnTest.text = "sssss"

        mMediator.mImageListData.request(this) {
            loading { }
            success { LogUtil.d("onSuccess${it}") }
            fail { LogUtil.d("onFailure1111") }
            fail { LogUtil.d("onFailure2222${it}") } //注意只会执行最后重新的方法
            finish { }
        }
        mMediator.loadImage(1, 10)
        binding.mStateView.setEmptyState("无数据")
    }
}