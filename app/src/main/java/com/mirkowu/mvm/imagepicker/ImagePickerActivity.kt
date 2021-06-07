package com.mirkowu.mvm.imagepicker

import com.mirkowu.lib_base.mediator.EmptyMediator
import com.mirkowu.lib_photo.ImagePicker
import com.mirkowu.lib_photo.PickerConfig
import com.mirkowu.lib_photo.mediaLoader.ResultModel
import com.mirkowu.lib_photo.view.ImagePickerRecyclerView
import com.mirkowu.lib_util.FileUtil
import com.mirkowu.lib_util.ktxutil.click
import com.mirkowu.mvm.base.BaseActivity
import com.mirkowu.mvm.databinding.ActivityImagePickerBinding
import com.mirkowu.mvm.viewbinding.binding

class ImagePickerActivity : BaseActivity<EmptyMediator>() {

    val binding by binding(ActivityImagePickerBinding::inflate)

    override fun bindContentView() {
        binding.root
    }

    override fun getLayoutId() = 0
    override fun initialize() {

        val list = mutableListOf(
                "https://gimg2.baidu.com/image_search/src=http%3A%2F%2Fattach.bbs.miui.com%2Fforum%2Fmonth_1011%2F1011250123f7480cd63703c992.jpg&refer=http%3A%2F%2Fattach.bbs.miui.com&app=2002&size=f9999,10000&q=a80&n=0&g=0n&fmt=jpeg?sec=1625652923&t=f88648f6594af41d02c7a30ff67d3284",
                "https://gimg2.baidu.com/image_search/src=http%3A%2F%2Fwx1.sinaimg.cn%2Flarge%2F008fHVgdly4gqfhftvhl5j30u00iv40g.jpg&refer=http%3A%2F%2Fwx1.sinaimg.cn&app=2002&size=f9999,10000&q=a80&n=0&g=0n&fmt=jpeg?sec=1625652886&t=a116fe8bd5ca59a461966d2b09b814e4"
        )

        binding.rvPick.setOnImagePickEventListener(object : ImagePickerRecyclerView.OnImagePickEventListener {
            override fun onItemClick(position: Int, isAddImage: Boolean) {
                if (isAddImage) {
                    ImagePicker.getInstance()
                            .setPickerConfig(PickerConfig().setOriginSelectList(ResultModel.pathsToBeans(binding.rvPick.adapter.data)))
                            .setOnPickResultListener {
                                binding.rvPick.setData(ResultModel.getPaths(it))
                            }.start(context)
                } else {
                    ImagePicker.previewImageWithSave(context, FileUtil.getDiskExternalPath(), binding.rvPick.adapter.data, position)
                }
            }

            override fun onItemDeleteClick(position: Int) {
                binding.rvPick.adapter.remove(position)
            }
        })

        binding.rvPick.setData(list)

        binding.btnSelect.click {
            ImagePicker.getInstance()
                    .setPickerConfig(PickerConfig().setShowCamera(true).setShowGif(true))
                    .setOnPickResultListener {
                        binding.rvPick.setData(ResultModel.getPaths(it))
                    }.start(this)
        }
    }

}