package com.mirkowu.mvm.ui

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import com.mirkowu.lib_base.mediator.EmptyMediator
import com.mirkowu.lib_base.util.bindingView
import com.mirkowu.lib_screen.internal.CancelAdapt
import com.mirkowu.mvm.R
import com.mirkowu.mvm.base.BaseActivity
import com.mirkowu.mvm.databinding.ActivityBitmapBinding

class BitmapActivity : BaseActivity<EmptyMediator>(), CancelAdapt {
    companion object {
        const val TAG = "BitmapTest"

        @JvmStatic
        fun start(context: Context) {
            val starter = Intent(context, BitmapActivity::class.java)
//             .putExtra()
            context.startActivity(starter)
        }
    }

    val binding by bindingView(ActivityBitmapBinding::inflate)
    override fun getLayoutId() = R.layout.activity_bitmap

    override fun initialize() {

        // 120*112 * (480/160) *4 =

        val bitmap1 = BitmapFactory.decodeResource(resources, R.mipmap.ic_search)
        val bitmap2 = BitmapFactory.decodeResource(resources, R.mipmap.ic_search,
            BitmapFactory.Options().apply {
//                inSampleSize
              //  inPreferredConfig = Bitmap.Config.RGB_565
            })

       val bmp = bitmap2.copy(Bitmap.Config.RGB_565, true);
//        bmp.config=Bitmap.Config.RGB_565


        val devDpi = resources.getDisplayMetrics().densityDpi

        //新图高度= 原图高度 *（设备dpi /目录对应dpi）
        Log.e(TAG, " densityDpi = " + devDpi)
        val scale = devDpi / 160f
        Log.e(TAG, " 原图 bitmap size = " + (144 * scale * 144 * scale * 4))
        Log.e(TAG, "100dp bitmap size = " + bitmap1.byteCount)
        Log.e(TAG, "200dp bitmap size = " + bitmap2.byteCount) //mdpi 685584 xxhdpi 43264
        Log.e(TAG, "200dp 565 bitmap size = " + bmp.byteCount) //mdpi 685584 xxhdpi 43264

        binding.iv100.setImageBitmap(bitmap1)
        binding.iv200.setImageBitmap(bitmap1)
    }
}