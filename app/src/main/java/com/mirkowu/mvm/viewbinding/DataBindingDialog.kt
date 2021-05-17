package com.mirkowu.mvm.viewbinding

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import com.mirkowu.mvm.R
import com.mirkowu.mvm.databinding.DialogDatabindingBinding

class DataBindingDialog(context: Context) : Dialog(context) {

    val binding by binding(DialogDatabindingBinding::inflate)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.dialog_databinding)

        binding.tvTitle.text="这是标题"
        binding.tvContent.text="这是内容----------"
        binding.tvCancel.apply { text="取消"
        setOnClickListener {
            this@DataBindingDialog.dismiss()
        }}
    }



}