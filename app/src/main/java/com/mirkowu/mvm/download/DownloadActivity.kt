package com.mirkowu.mvm.download

import com.mirkowu.lib_base.mediator.EmptyMediator
import com.mirkowu.lib_network.download.DownloadClient
import com.mirkowu.lib_network.download.OnDownloadListener
import com.mirkowu.lib_util.FileUtil
import com.mirkowu.lib_util.ktxutil.click
import com.mirkowu.lib_util.utilcode.util.ConvertUtils
import com.mirkowu.mvm.base.BaseActivity
import com.mirkowu.mvm.databinding.ActivityDownloadBinding
import com.mirkowu.mvm.viewbinding.binding
import java.io.File

class DownloadActivity : BaseActivity<EmptyMediator>() {

    val binding by binding(ActivityDownloadBinding::inflate)

    override fun bindContentView() {
        // super.bindContentView()
        binding.root
    }

    override fun getLayoutId() = 0
    var id = 0L
    var id2 = 0L
    override fun initialize() {
        val url = "https://gimg2.baidu.com/image_search/src=http%3A%2F%2Fpic.jj20.com%2Fup%2Fallimg%2F1114%2F0FR0104017%2F200FQ04017-6-1200.jpg&refer=http%3A%2F%2Fpic.jj20.com&app=2002&size=f9999,10000&q=a80&n=0&g=0n&fmt=jpeg?sec=1625987715&t=6b9300c42b95e879ae1c41387cdb33bc"
        val url2 = "https://dw.fjweite.cn/syt/windows_10_professional_x64_2020.iso"
        binding.btnDown.click {
            val filePath = FileUtil.getDiskExternalPath() + "/" + System.currentTimeMillis() + ".jpg"
            id = DownloadClient.getInstance().setUrl(url2)
                    .setFilePath(filePath)
                    .setOnProgressListener(object : OnDownloadListener {
                        override fun onProgress(readBytes: Long, totalBytes: Long) {
                            val size = ConvertUtils.byte2FitMemorySize(totalBytes)
                            //  LogUtil.e("readBytes :" + readBytes + "  总大小：" + size)
                            var progress = (readBytes * 100f / totalBytes).toInt()
                            binding.pbDown.progress = progress

                            binding.tvProgress.setText("$progress% ${size}")
                        }

                        override fun onSuccess(file: File) {
                        }

                        override fun onFailure(e: Throwable?) {
                        }
                    }).start()
        }
        binding.btnCancel.click {
            DownloadClient.getInstance().cancel(id)
        }
        binding.btnDown2.click {
            val filePath = FileUtil.getDiskExternalPath() + "/" + System.currentTimeMillis() + ".jpg"
            id2 = DownloadClient.getInstance().setUrl(url2)
                    .setFilePath(filePath)
                    .setOnProgressListener(object : OnDownloadListener {
                        override fun onProgress(readBytes: Long, totalBytes: Long) {
                            val size = ConvertUtils.byte2FitMemorySize(totalBytes)
                            //  LogUtil.e("readBytes :" + readBytes + "  总大小：" + size)
                            var progress = (readBytes * 100f / totalBytes).toInt()
                            binding.pbDown2.progress = progress

                            binding.tvProgress2.setText("$progress% ${size}")
                        }

                        override fun onSuccess(file: File) {
                        }

                        override fun onFailure(e: Throwable?) {
                        }
                    }).start()
        }
        binding.btnCancel2.click {
            DownloadClient.getInstance().cancel(id2)
        }
    }
}