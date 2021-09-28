package com.mirkowu.mvm.recycelerview

import android.widget.ImageView
import android.widget.TextView
import com.mirkowu.lib_widget.adapter.BaseRVHolder
import com.mirkowu.lib_widget.adapter.SimpleRVAdapter
import com.mirkowu.mvm.R

class GridAdapter : SimpleRVAdapter<FirstBean>(R.layout.item_grid) {

    override fun onBindHolder(holder: BaseRVHolder, item: FirstBean?, position: Int) {
        val tvTitle: TextView = holder.getView(R.id.tv_title)
        val ivImage: ImageView = holder.getView(R.id.ivImage)
        holder.setText(
            R.id.tv_title,
            String.format("%d 宽高比=%d:%d",position, tvTitle.width, tvTitle.height)
        )
    }


}