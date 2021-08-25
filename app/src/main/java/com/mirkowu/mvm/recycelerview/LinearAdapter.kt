package com.mirkowu.mvm.recycelerview

import android.view.View
import com.mirkowu.lib_image.ImageLoader
import com.mirkowu.lib_widget.adapter.BaseRVHolder
import com.mirkowu.lib_widget.adapter.IMultiType
import com.mirkowu.lib_widget.adapter.MultiTypeRVAdapter
import com.mirkowu.mvm.R

class LinearAdapter : MultiTypeRVAdapter<IMultiType, LinearAdapter.Holder> {


    companion object {
        const val TYPE_FIRST = 1
        const val TYPE_SECOND = 2
        const val TYPE_THIRD = 3
        const val TYPE_FOUR = 4
    }

    constructor() : super() {
        addItemViewType(TYPE_FIRST, R.layout.item_first)
        addItemViewType(TYPE_SECOND, R.layout.item_second)
        addItemViewType(TYPE_THIRD, R.layout.item_binding_list)
        addItemViewType(TYPE_FOUR, R.layout.item_binding_list)
    }

    override fun onBindHolder(holder: LinearAdapter.Holder, item: IMultiType?, position: Int) {
        if (item == null) return
        when (item) {
            is FirstBean -> {
                bindHolderFirst(holder, item, position)
            }
            is SecondBean -> {
                bindHolderSecond(holder, item, position)
            }
            is ThirdBean -> {
                bindHolderThird(holder, item, position)
            }
        }

//        holder.binding.apply {
//            tvTitle.text = "第${position}XXXXXXXXXX"
//            tvContent.text = "内容${position}"
//            addOnClickListener(holder, tvTitle)
//            addOnClickListener(holder, tvContent)
//        }
    }

    private fun bindHolderFirst(holder: BaseRVHolder, item: FirstBean, position: Int) {
        holder.setText(R.id.tv_title, "第一张类型")

    }

    private fun bindHolderSecond(holder: BaseRVHolder, item: SecondBean, position: Int) {

        ImageLoader.load(holder.getView(R.id.iv_image), R.mipmap.ic_launcher)
    }

    private fun bindHolderThird(holder: BaseRVHolder, item: ThirdBean, position: Int) {
        holder.setText(R.id.tv_title, "标题")
        holder.setText(R.id.tv_content, "内容")

    }

    //    class Holder(val binding: ItemBindingListBinding) : BaseRVHolder(binding.root) {
//
//    }
    class Holder(val view: View) : BaseRVHolder(view) {

    }

}