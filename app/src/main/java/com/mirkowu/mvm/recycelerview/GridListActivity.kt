package com.mirkowu.mvm.recycelerview

import android.content.Context
import android.content.Intent
import android.graphics.Color
import androidx.recyclerview.widget.LinearLayoutManager
import com.mirkowu.lib_base.mediator.EmptyMediator
import com.mirkowu.lib_widget.LinearDecoration
import com.mirkowu.mvm.R
import com.mirkowu.mvm.base.BaseActivity
import com.mirkowu.mvm.databinding.ActivityGridListBinding
import com.mirkowu.mvm.viewbinding.binding

class GridListActivity : BaseActivity<EmptyMediator>() {
    companion object {
        @JvmStatic
        fun start(context: Context) {
            val starter = Intent(context, GridListActivity::class.java)
//                    .putExtra()
            context.startActivity(starter)
        }
    }

    val binding by binding(ActivityGridListBinding::inflate)
    override fun bindContentView() {
//        super.bindContentView()
        binding.root
    }

    override fun getLayoutId() = R.layout.activity_grid_list

    override fun initialize() {

        val list = mutableListOf("", "", "", "", "", "", "", "", "", "", "")
        val gridAdapter = GridAdapter()
        binding.rvGrid.apply {
            layoutManager = LinearLayoutManager(context )
            adapter = gridAdapter
            addItemDecoration(LinearDecoration(context).setSpace(10f).setEdgeSpace(10f).setTopSpace(20f).setBottomSpace(50f).setSpaceColor(Color.parseColor("#90FF0000")))
        }
        gridAdapter.data=list
    }
}