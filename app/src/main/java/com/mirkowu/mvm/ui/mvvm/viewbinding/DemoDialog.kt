package com.mirkowu.mvm.ui.mvvm.viewbinding

import com.mirkowu.lib_base.util.bindingView
import com.mirkowu.lib_widget.dialog.BaseDialog
import com.mirkowu.mvm.R
import com.mirkowu.mvm.databinding.DialogDatabindingBinding

class DemoDialog : BaseDialog() {

    val binding by bindingView(DialogDatabindingBinding::bind)

    override fun getLayoutId() = R.layout.dialog_databinding
}