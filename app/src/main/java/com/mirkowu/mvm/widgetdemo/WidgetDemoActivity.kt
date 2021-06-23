package com.mirkowu.mvm.widgetdemo

import android.content.Context
import android.content.Intent
import com.mirkowu.lib_base.mediator.EmptyMediator
import com.mirkowu.lib_util.ktxutil.click
import com.mirkowu.lib_widget.dialog.PromptDialog
import com.mirkowu.mvm.R
import com.mirkowu.mvm.base.BaseActivity
import com.mirkowu.mvm.databinding.ActivityWidgetDemoBinding
import com.mirkowu.mvm.viewbinding.DataBindingDialog
import com.mirkowu.mvm.viewbinding.bindingView

class WidgetDemoActivity : BaseActivity<EmptyMediator>() {
    companion object {
        fun start(context: Context) {
            val starter = Intent(context, WidgetDemoActivity::class.java)
            context.startActivity(starter)
        }
    }

    val binding by bindingView(ActivityWidgetDemoBinding::inflate)

    override fun getLayoutId() = R.layout.activity_widget_demo

    override fun initialize() {
        binding.btnLoading.click {
            showLoadingDialog("Toast测试")
        }
        binding.btnPrompt.click {
            PromptDialog().setTitle("温馨提示")
                    .setContent("确认关闭吗？")
                    .setUseDefaultButton()
                    .setIcon(R.mipmap.ic_launcher)
                    .show(supportFragmentManager)
//            showLoadingDialog("Toast测试")
        }
        binding.btnPrompt2.click {
            PromptDialog().setTitle("温馨提示")
                    .setContent("确认关闭吗？")
                    .setUseDefaultButton()
                    .show(supportFragmentManager)
//            showLoadingDialog("Toast测试")
        }
        binding.btnBinding.click {
            DataBindingDialog(context!!).show()
        }
    }
}