package com.mirkowu.mvm.recycelerview

import android.content.Context
import android.content.Intent
import android.graphics.Color
import androidx.recyclerview.widget.GridLayoutManager
import com.mirkowu.lib_base.mediator.EmptyMediator
import com.mirkowu.lib_base.util.bindingView
import com.mirkowu.lib_util.LogUtil
import com.mirkowu.lib_widget.decoration.GridDecoration
import com.mirkowu.mvm.R
import com.mirkowu.mvm.base.BaseActivity
import com.mirkowu.mvm.databinding.ActivityGridListBinding

class GridListActivity : BaseActivity<EmptyMediator>() {
    companion object {
        @JvmStatic
        fun start(context: Context) {
            val starter = Intent(context, GridListActivity::class.java)
//                    .putExtra()
            context.startActivity(starter)
        }
    }

    val binding by bindingView(ActivityGridListBinding::inflate)
    override fun bindContentView() {
//        super.bindContentView()
        binding.root
    }

    override fun getLayoutId() = R.layout.activity_grid_list

    override fun initialize() {

        val list = mutableListOf<FirstBean>()
        for (index in 0 until 400) {
            list.add(FirstBean())
        }
        val gridAdapter = GridAdapter()
        binding.rvGrid.apply {
            layoutManager = GridLayoutManager(context, 4, GridLayoutManager.VERTICAL, false)
            adapter = gridAdapter
            addItemDecoration(
                GridDecoration(context).setSpace(30f)/*.setEdgeSpace(10f)*/
                    .setTopSpace(20f).setBottomSpace(10f)
//                    .setSideSpace(20f)
                    .setSpaceColor(Color.parseColor("#40000000"))
            )
        }
        gridAdapter.data = list
        gridAdapter.setOnItemChildClickListener { view, item, position ->
            when (view.id) {
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