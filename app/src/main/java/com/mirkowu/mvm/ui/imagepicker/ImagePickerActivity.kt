package com.mirkowu.mvm.ui.imagepicker

import android.graphics.BitmapFactory
import android.graphics.Color
import android.util.Log
import com.mirkowu.lib_base.mediator.EmptyMediator
import com.mirkowu.lib_base.util.bindingView
import com.mirkowu.lib_photo.ImagePicker
import com.mirkowu.lib_photo.PickerConfig
import com.mirkowu.lib_photo.mediaLoader.ResultModel
import com.mirkowu.lib_photo.view.ImagePickerRecyclerView
import com.mirkowu.lib_util.FileUtil
import com.mirkowu.lib_util.LogUtil
import com.mirkowu.lib_util.PermissionsUtil
import com.mirkowu.lib_util.SystemShareUtil
import com.mirkowu.lib_util.ktxutil.click
import com.mirkowu.lib_util.utilcode.util.ScreenUtils
import com.mirkowu.mvm.Constant
import com.mirkowu.mvm.R
import com.mirkowu.mvm.base.BaseActivity
import com.mirkowu.mvm.databinding.ActivityImagePickerBinding
import com.mirkowu.mvm.ui.BitmapActivity
import java.io.File

class ImagePickerActivity : BaseActivity<EmptyMediator>() {

    val binding by bindingView(ActivityImagePickerBinding::inflate)

    override fun bindContentView() {
        binding.root
    }

    override fun getLayoutId() = 0
    override fun initialize() {

//        val toolbar = Toolbar(this)
//        binding.mRootView.addView(toolbar, 0)
        val toolbar = binding.mToolbar
        toolbar.setBackgroundColor(Color.WHITE)
        toolbar.setTitle("选取图片")
        toolbar.setRightIcon(R.drawable.svg_setting) {
            val list = binding.rvPick.data
            val files = mutableListOf<File>()
            if (list.isNotEmpty()) {
                for (path in list) {
                    val file = File(path)
                    if (file.exists()) {
                        files.add(file)
                    }
                }
                if (files.isNotEmpty()) {
                    SystemShareUtil.shareToWxCircle(this, "测试", files.last())
                }
            }
        }

        val list = mutableListOf(
            "https://gimg2.baidu.com/image_search/src=http%3A%2F%2Fattach.bbs.miui.com%2Fforum%2Fmonth_1011%2F1011250123f7480cd63703c992.jpg&refer=http%3A%2F%2Fattach.bbs.miui.com&app=2002&size=f9999,10000&q=a80&n=0&g=0n&fmt=jpeg?sec=1625652923&t=f88648f6594af41d02c7a30ff67d3284",
            "https://gimg2.baidu.com/image_search/src=http%3A%2F%2Fwx1.sinaimg.cn%2Flarge%2F008fHVgdly4gqfhftvhl5j30u00iv40g.jpg&refer=http%3A%2F%2Fwx1.sinaimg.cn&app=2002&size=f9999,10000&q=a80&n=0&g=0n&fmt=jpeg?sec=1625652886&t=a116fe8bd5ca59a461966d2b09b814e4"
        )

        binding.rvPick.setOnImagePickEventListener(
            object :
                ImagePickerRecyclerView.OnImagePickEventListener {
                override fun onItemClick(position: Int, isAddImage: Boolean) {
                    if (isAddImage) {
                        ImagePicker.getInstance()
                            .setPickerConfig(
                                PickerConfig()
                                    .setShowCamera(true)
                                    .setShowGif(true)
                                    .setShowVideo(false)
                                    .setOriginSelectList(ResultModel.pathsToBeans(binding.rvPick.data))
                            )
                            .setOnPickResultListener {
                                LogUtil.d("ImagePicker: $it")
                               val list= ResultModel.getPaths(it)
                                binding.rvPick.setData(list)


                                val bitmap1 = BitmapFactory.decodeFile(list.get(list.size-1))
                                val bitmap2 = BitmapFactory.decodeFile(list.get(list.size-1),
                                    BitmapFactory.Options().apply { })

                                val devDpi=  ScreenUtils.getScreenDensityDpi()

                                //新图高度= 原图高度 *（设备dpi /目录对应dpi）
                                Log.e(BitmapActivity.TAG, " densityDpi = " + devDpi)
                                val scale = devDpi / 160f
                                Log.e(BitmapActivity.TAG, " 原图 bitmap size = " + (1080 * 2160 * 4))
                                Log.e(BitmapActivity.TAG, "100dp bitmap size = " + bitmap1.byteCount)
                                Log.e(BitmapActivity.TAG, "200dp bitmap size = " + bitmap2.byteCount) //mdpi 685584 xxhdpi 43264

                                binding.iv100.setImageBitmap(bitmap1)
                                binding.iv200.setImageBitmap(bitmap1)

                            }.start(context)
                    } else {
                        ImagePicker.previewImageWithSave(
                            context,
                            FileUtil.getDiskExternalPath(Constant.FILE_SAVE_DIR),
                            binding.rvPick.data,
                            position
                        )
                    }
                }

                override fun onItemDeleteClick(position: Int) {
                    binding.rvPick.remove(position)
                }
            })

        binding.rvPick.setData(list)

        binding.btnSelect.click {
//            ImagePicker.getInstance()
//                    .setPickerConfig(PickerConfig().setShowCamera(true).setShowGif(true))
//                    .setOnPickResultListener {
//                        LogUtil.d("ImagePicker: $it")
//                        binding.rvPick.setData(ResultModel.getPaths(it))
//                    }.start(this)
            PermissionsUtil.getInstance()
                .requestPermissions(this, PermissionsUtil.GROUP_CAMERA, 0, onPermissionsListener)
        }




    }

    var onPermissionsListener = object : PermissionsUtil.OnPermissionsListener {
        override fun onPermissionGranted(requestCode: Int) {
            Log.d("TAG", "onPermissionGranted: ")
        }

        override fun onPermissionShowRationale(requestCode: Int, permissions: Array<out String>) {
            Log.d("TAG", "onPermissionShowRationale: ")

        }

        override fun onPermissionDenied(requestCode: Int) {
            Log.d("TAG", "onPermissionDenied: ")

        }
    }

    override fun onDestroy() {
        super.onDestroy()
        ImagePicker.getInstance().removeAllListener()
    }
}