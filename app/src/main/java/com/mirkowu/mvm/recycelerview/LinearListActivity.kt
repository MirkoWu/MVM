package com.mirkowu.mvm.recycelerview

import android.content.Context
import android.content.Intent
import android.graphics.Color
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mirkowu.lib_base.mediator.EmptyMediator
import com.mirkowu.lib_base.util.bindingView
import com.mirkowu.lib_util.LogUtil
import com.mirkowu.lib_widget.adapter.IMultiType
import com.mirkowu.lib_widget.decoration.LinearDecoration
import com.mirkowu.mvm.R
import com.mirkowu.mvm.base.BaseActivity
import com.mirkowu.mvm.databinding.ActivityLinearListBinding

class LinearListActivity : BaseActivity<EmptyMediator>() {
    companion object {
        @JvmStatic
        fun start(context: Context) {
            val starter = Intent(context, LinearListActivity::class.java)
//                    .putExtra()
            context.startActivity(starter)
        }
    }

    val binding by bindingView(ActivityLinearListBinding::inflate)
    override fun bindContentView() {
//        super.bindContentView()
        binding.root
    }

    override fun getLayoutId() = R.layout.activity_linear_list

    override fun initialize() {

        val list = mutableListOf<IMultiType>()
        for (index in 0 until 40) {
            list.add(FirstBean())
//            if (index % 2 == 0) {
//                list.add(SecondBean())
//            } else if (index % 3 == 0) {
//                list.add(ThirdBean())
//            } else {
//                list.add(FirstBean())
//                list.add(FourBean())
//            }
        }
        val gridAdapter = LinearAdapter()
        binding.rvGrid.apply {
            layoutManager = LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)
            adapter = gridAdapter
            addItemDecoration(
                LinearDecoration(context).setSpace(10f)/*.setEdgeSpace(10f)*/
//                    .setTopSpace(20f).setBottomSpace(50f)
                    .setLeftPadding(10f).setRightPadding(60f)
                    .setSpaceColor(Color.parseColor("#90FF0000"))
            )
        }
        gridAdapter.data = list
        gridAdapter.setOnItemChildClickListener { view, item, position ->
            when (view.id) {
                R.id.tv_title -> {
                    gridAdapter.removeData(position)
                }
                R.id.tv_content -> {
                    // gridAdapter.addData(1, "A")
                }
            }
        }
        gridAdapter.setOnItemClickListener { view, item, position ->
            LogUtil.d(
                "setOnItemClickListener: position=$position item=$item  item=${
                    gridAdapter.getItem(
                        position
                    )
                }"
            )
        }
    }
}