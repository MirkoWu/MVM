package com.mirkowu.mvm.mvvm.viewbinding

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mirkowu.mvm.databinding.ItemBindingListBinding

class DataBindingAdapter<T>(private val mData: MutableList<T>?) : RecyclerView.Adapter<DataBindingAdapter.BindingViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BindingViewHolder {
//        BindingViewHolder(parent, ItemBindingListBinding::inflate)

//        return BindingViewHolder(ItemBindingListBinding.inflate(LayoutInflater.from(parent.context), parent, false))
        return BindingViewHolder(ItemBindingListBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: BindingViewHolder, position: Int) {
        holder.binding.apply {
            tvTitle.text = "标题${position}"
            tvContent.text = "。。。。。。"
        }
        holder.binding.tvContent
    }

    override fun getItemCount(): Int {
        return mData?.size ?: 0
    }


    class BindingViewHolder(val binding: ItemBindingListBinding) : RecyclerView.ViewHolder(binding.root) {

//    }
//    class BindingViewHolder(val binding: ItemBindingListBinding) : RecyclerView.ViewHolder(binding.root) {
//        constructor(parent: ViewGroup, inflate: (LayoutInflater, ViewGroup, Boolean) -> ItemBindingListBinding)
//                : this(inflate(LayoutInflater.from(parent.context), parent, false))
//
//    }
//    class BindingViewHolder(parent: ViewGroup, inflate: (LayoutInflater, ViewGroup, Boolean) -> ItemBindingListBinding)
//        : RecyclerView.ViewHolder(inflate(LayoutInflater.from(parent.context), parent, false).root) {
//    }

//    class BindingViewHolder(val binding: ItemBindingListBinding) : BaseViewHolder<ItemBindingListBinding>(binding) {
//
//    }
    }
}