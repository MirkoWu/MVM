package com.mirkowu.mvm.ui.widgetdemo

import android.content.Context
import android.content.Intent
import com.mirkowu.lib_base.mediator.EmptyMediator
import com.mirkowu.lib_base.util.bindingView
import com.mirkowu.lib_util.AppSettingUtils
import com.mirkowu.lib_util.ktxutil.click
import com.mirkowu.lib_widget.stateview.ViewState
import com.mirkowu.mvm.R
import com.mirkowu.mvm.base.BaseActivity
import com.mirkowu.mvm.databinding.ActivityStateViewBinding
import com.umeng.analytics.pro.ak.bi

class StateViewActivity : BaseActivity<EmptyMediator>() {

    companion object {
        @JvmStatic
        fun start(context: Context) {
            val starter = Intent(context, StateViewActivity::class.java)
//                .putExtra()
            context.startActivity(starter)
        }
    }

    val binding by bindingView(ActivityStateViewBinding::inflate)
    override fun getLayoutId() = R.layout.activity_state_view

    override fun initialize() {
        binding.btnLoading.click {
            binding.mStateView.setLoadingState(R.drawable.widget_loading_progressbar,"加载中")
            it.postDelayed({ binding.mStateView.setGoneState() }, 2000)
        }
       binding.btnLoading2.click {
            binding.mStateView.setLoadingState(binding.mStateView.defaultLoadingDrawable ,"加载中")
            it.postDelayed({ binding.mStateView.setGoneState() }, 2000)
        }
       binding.btnLoading3.click {
            binding.mStateView.setLoadingState(R.drawable.big_white,"加载中")
            it.postDelayed({ binding.mStateView.setGoneState() }, 2000)
        }
        binding.btnEmpty.click {
            binding.mStateView.setEmptyState("空空如也～")
            it.postDelayed({ binding.mStateView.setGoneState() }, 2000)
        }
        binding.btnError.click {
            binding.mStateView.setErrorState("出错啦")

        }
        binding.btnError2.click {
            binding.mStateView.setErrorState(
                R.drawable.widget_svg_network_error,
                "出错啦",
                "点击重试",
                "检查网络"
            )

        }
        binding.btnCustom.click {
            binding.mStateView.setShowState(R.drawable.widget_svg_empty, "空空如也", "刷新", "看看别的")
        }
        binding.mStateView.setOnRefreshListener {
            binding.btnLoading2.performClick()
        }
        binding.mStateView.setOnCheckNetworkListener {
            binding.mStateView.apply {
                if (state == ViewState.SHOW) {
                    StateViewActivity.start(this@StateViewActivity)
                } else {
                    AppSettingUtils.startNetworkSetting(this@StateViewActivity)
                }
            }
        }

    }
}