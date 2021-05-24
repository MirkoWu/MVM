package com.mirkowu.mvm.recycelerview

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mirkowu.lib_base.adapter.BaseAdapter
import com.mirkowu.mvm.databinding.ItemBindingListBinding

class GridAdapter : BaseAdapter<String, GridAdapter.Holder>() {
    class Holder(val binding: ItemBindingListBinding) : RecyclerView.ViewHolder(binding.root) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        return Holder(ItemBindingListBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindHolder(holder: Holder, item: String?, position: Int) {
        holder.binding.apply {
            tvTitle.text = "第${position}XXXXXXXXXX"
            tvContent.text = "内容${position}"
        }
    }
}