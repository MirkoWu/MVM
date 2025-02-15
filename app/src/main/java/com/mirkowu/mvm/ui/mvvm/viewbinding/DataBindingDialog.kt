package com.mirkowu.mvm.ui.mvvm.viewbinding

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import com.mirkowu.lib_base.fragment.BaseMVMDialog
import com.mirkowu.lib_base.mediator.EmptyMediator
import com.mirkowu.lib_base.util.bindingView
import com.mirkowu.lib_widget.dialog.BaseDialog
import com.mirkowu.mvm.R
import com.mirkowu.mvm.databinding.DialogDatabindingBinding

class DataBindingDialog : BaseMVMDialog<EmptyMediator>() {

    val binding by bindingView(DialogDatabindingBinding::bind)
    override fun initialize() {
        binding.tvTitle.text = "这是标题"
        binding.tvContent.text = "这是内容----------"
        binding.tvCancel.apply {
            text = "取消"
            setOnClickListener {
                this@DataBindingDialog.dismiss()
            }
        }
        mMediator.showLoadingDialog()
        mMediator.hideLoadingDialog()
    }

    override fun getLayoutId() = R.layout.dialog_databinding
    override fun initMediator(): EmptyMediator {
        return EmptyMediator()
    }


}