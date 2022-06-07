package com.mirkowu.mvm.widgetdemo

import android.content.Context
import android.content.Intent
import android.content.res.Resources
import com.mirkowu.lib_base.mediator.EmptyMediator
import com.mirkowu.lib_base.util.bindingView
import com.mirkowu.lib_screen.AutoSizeManager
import com.mirkowu.lib_screen.internal.CustomAdapt
import com.mirkowu.lib_util.ktxutil.click
import com.mirkowu.lib_util.pattern.InterceptChainManager
import com.mirkowu.lib_util.utilcode.util.ScreenUtils
import com.mirkowu.lib_util.utilcode.util.ToastUtils
import com.mirkowu.lib_widget.dialog.BottomListDialog
import com.mirkowu.lib_widget.dialog.InputDialog
import com.mirkowu.lib_widget.dialog.PromptDialog
import com.mirkowu.mvm.R
import com.mirkowu.mvm.base.BaseActivity
import com.mirkowu.mvm.databinding.ActivityWidgetDemoBinding
import com.mirkowu.mvm.interceptchain.FirstInterceptChain
import com.mirkowu.mvm.interceptchain.SecondInterceptChain
import com.mirkowu.mvm.interceptchain.ThirdInterceptChain
import com.mirkowu.mvm.mvvm.viewbinding.DataBindingDialog

class WidgetDemoActivity : BaseActivity<EmptyMediator>(), CustomAdapt {
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
                .setIcon(R.mipmap.ic_launcher)
                .setNegativeButton("取消")
                .show(supportFragmentManager)
//            showLoadingDialog("Toast测试")
        }
        binding.btnPrompt2.click {
            PromptDialog().setTitle("温馨提示")
                .setContent("确认关闭吗？")
                .show(supportFragmentManager)
//            showLoadingDialog("Toast测试")
        }
        binding.btnBottomList.click {
            BottomListDialog()
                .setData(mutableListOf<CharSequence>("测试环境", "测试环境", "测试环境", "测试环境"))
                //.setTitle("标题")
//                .setMarginHor(20).setMarginVer(20)
                .setOnItemClickListener { dialog, data, position ->

                }
                .show(supportFragmentManager)
        }
        binding.btnInput.click {
            InputDialog()
                .setTitle("标题")
                .setInputHint("提示文字")
                .setInputMaxCharLength(3)
                .show(supportFragmentManager)
        }
        binding.btnBinding.click {
            DataBindingDialog(context!!).show()
        }
        binding.btnToastDark.click {
            ToastUtils.make()
                .setMode(ToastUtils.MODE.DARK)
                .setLeftIcon(R.mipmap.ic_launcher)
                .show("下载成功，已保存到下载成功，已保存到下载成功，已保存到下载成功，已保存到下载成功，已保存到")
        }
        binding.btnToastLight.click {
            ToastUtils.make().setMode(ToastUtils.MODE.LIGHT)
                .setLeftIcon(R.mipmap.ic_launcher)
                .show("下载成功，已保存到下载成功，已保存到下载成功，已保存到下载成功，已保存到下载成功，已保存到")
        }

        val manager2 =
            InterceptChainManager<String>()
        manager2.add(/*"A1",*/ FirstInterceptChain(this))
        manager2.add(/*"A2",*/ SecondInterceptChain(this))
        manager2.add(/*"A3",*/ ThirdInterceptChain(this))
        manager2.add(/*"A4",*/ SecondInterceptChain(this))


        binding.btnInterceptChain.click {
            manager2.reset()
            manager2.start("开始")
        }
    }


    //以短边为准 最好
    override fun isBaseOnWidth() = ScreenUtils.isPortrait()
    override fun getSizeInDp() = AutoSizeManager.DESIGN_WIDTH_IN_DP.toFloat()

    //以长边为准
//    override fun isBaseOnWidth() = true
//    override fun getSizeInDp() =
//            if (ScreenUtils.isPortrait()) {
//                AutoSizeManager.DESIGN_WIDTH_IN_DP.toFloat()
//            } else {
//                AutoSizeManager.DESIGN_HEIGHT_IN_DP.toFloat()
//            }


    override fun adaptScreenSize(superResources: Resources) {
        super.adaptScreenSize(superResources)
    }
}