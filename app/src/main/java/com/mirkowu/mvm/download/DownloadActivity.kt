package com.mirkowu.mvm.download

import android.annotation.SuppressLint
import com.mirkowu.lib_base.mediator.EmptyMediator
import com.mirkowu.lib_base.util.RxScheduler
import com.mirkowu.lib_base.util.bindingView
import com.mirkowu.lib_network.ErrorType
import com.mirkowu.lib_network.load.Downloader
import com.mirkowu.lib_network.load.OnDownloadListener
import com.mirkowu.lib_network.load.OnProgressListener
import com.mirkowu.lib_util.FileUtil
import com.mirkowu.lib_util.LogUtil
import com.mirkowu.lib_util.PermissionsUtil
import com.mirkowu.lib_util.ktxutil.click
import com.mirkowu.lib_util.utilcode.util.ConvertUtils
import com.mirkowu.lib_util.utilcode.util.ToastUtils
import com.mirkowu.mvm.Constant
import com.mirkowu.mvm.base.BaseActivity
import com.mirkowu.mvm.databinding.ActivityDownloadBinding
import com.mirkowu.mvm.network.FileClient
import com.mirkowu.mvm.network.RxObserver
import com.mirkowu.mvm.network.UploadFileClient
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File

class DownloadActivity : BaseActivity<EmptyMediator>() {

    val binding by bindingView(ActivityDownloadBinding::inflate)

    override fun bindContentView() {
        // super.bindContentView()
        binding.root
    }

    override fun getLayoutId() = 0
    var id = 0L
    var id2 = 0L
    var filePath = ""

    @SuppressLint("AutoDispose")
    override fun initialize() {

        PermissionsUtil.getInstance().requestPermissions(
            this,
            PermissionsUtil.GROUP_STORAGE,
            object : PermissionsUtil.OnPermissionsListener {
                override fun onPermissionGranted(requestCode: Int) {

                }

                override fun onPermissionShowRationale(
                    requestCode: Int,
                    permissions: Array<out String>
                ) {

                }

                override fun onPermissionDenied(requestCode: Int) {

                }

            })

        val url =
            "https://gimg2.baidu.com/image_search/src=http%3A%2F%2Fpic.jj20.com%2Fup%2Fallimg%2F1114%2F0FR0104017%2F200FQ04017-6-1200.jpg&refer=http%3A%2F%2Fpic.jj20.com&app=2002&size=f9999,10000&q=a80&n=0&g=0n&fmt=jpeg?sec=1625987715&t=6b9300c42b95e879ae1c41387cdb33bc"
        val url2 = "https://dw.fjweite.cn/syt/windows_10_professional_x64_2020.iso"
        binding.btnDown.click {
            //外部存储
//            val filePath = FileUtil.getDiskExternalPath("DCIM") + "/IMG_YYY" + System.currentTimeMillis() + ".jpg"
            val filePath =
                FileUtil.getDiskExternalPath("Download") + "/test/IMG_YYY" + System.currentTimeMillis() + ".jpg"
            //内部存储
//            val filePath = FileUtil.getAppCachePath(context) + "/" + System.currentTimeMillis() + ".jpg"
            //公共存储
//            val filePath = "${Environment.DIRECTORY_DOWNLOADS}/test/IMG_XXX_${System.currentTimeMillis()}.jpg"

            id = Downloader.create(url)
                .setFilePath(filePath)
                .setOnProgressListener(object :
                    OnDownloadListener {
                    override fun onProgress(readBytes: Long, totalBytes: Long) {
                        val size = ConvertUtils.byte2FitMemorySize(totalBytes)
                        LogUtil.e("readBytes :" + readBytes + "  总大小：" + size)
                        var progress = (readBytes * 100f / totalBytes).toInt()
                        binding.pbDown.progress = progress
                        binding.tvProgress.setText("$progress% ${size}")
                    }

                    override fun onSuccess(file: File) {
                        ToastUtils.showShort("下载成功，已保存到" + file.absolutePath)
                    }

                    override fun onFailure(e: Throwable?) {
                        ToastUtils.showShort("下载失败")
                    }
                }).start()
        }
        binding.btnCancel.click {
            Downloader.cancel(id)
        }
        binding.btnDown2.click {
            //外部存储
            filePath =
                FileUtil.getDiskExternalPath(Constant.FILE_SAVE_DIR) + "/" + System.currentTimeMillis() + ".jpg"
            id2 = Downloader.create(url2)
                .setUrl(url2)
                .setFilePath(filePath)
                .setOnProgressListener(object :
                    OnDownloadListener {
                    override fun onProgress(readBytes: Long, totalBytes: Long) {
                        val size = ConvertUtils.byte2FitMemorySize(totalBytes)
                        LogUtil.e("readBytes :" + readBytes + "  总大小：" + size)
                        var progress = (readBytes * 100f / totalBytes).toInt()
                        binding.pbDown2.progress = progress

                        binding.tvProgress2.setText("$progress% ${size}")
                    }

                    override fun onSuccess(file: File) {
                        ToastUtils.showShort("下载成功，已保存到" + file.absolutePath)
                    }

                    override fun onFailure(e: Throwable) {
                        ToastUtils.showShort("下载失败")
                    }
                }).start()
        }
        binding.btnCancel2.click {
            Downloader.cancel(id2)
        }

        binding.btnUpload.click {
            UploadFileClient.getInstance().onProgressListener =
                OnProgressListener { readBytes, totalBytes ->
                    val progress = (100f * readBytes / totalBytes).toInt()
                    LogUtil.d(
                        "UploadFileClient",
                        " onProgress $progress"
                    )
                    binding.pbUpload.progress = progress
                }
            val file = RequestBody.create(
                MediaType.parse("*/*"),
                File(filePath)
            )
            val body = MultipartBody.Builder().setType(MultipartBody.FORM)
                .addFormDataPart("file", "file", file).build()

            UploadFileClient.getUploadFileApi()
                .uploadFile(body)
                .compose(RxScheduler.ioToMain())
                .subscribe(object : RxObserver<Any>() {
                    override fun onSuccess(data: Any?) {
                        LogUtil.d("UploadFileClient", " onSuccess")
                    }

                    override fun onFailure(type: ErrorType, code: Int, msg: String?) {
                        super.onFailure(type, code, msg)
                        LogUtil.d("UploadFileClient", " onFailure")
                    }

                })
        }

        binding.btnUpload2.click {

            val file = RequestBody.create(
                MediaType.parse("*/*"),
                File("/storage/emulated/0/Download/test/IMG_YYY1628780542488.jpg")
            )
            val body = MultipartBody.Builder().setType(MultipartBody.FORM)
                .addFormDataPart("file", "file", file).build()

            FileClient.getUploadFileApi()
                .uploadFile(body)
                .compose(RxScheduler.ioToMain())
                .subscribe(object : RxObserver<Any>() {
                    override fun onSuccess(data: Any?) {
                        LogUtil.d("UploadFileClient", " onSuccess")
                    }

                    override fun onFailure(type: ErrorType, code: Int, msg: String?) {
                        super.onFailure(type, code, msg)
                        LogUtil.d("UploadFileClient", " onFailure")
                    }

                })
        }

        binding.btnCancel3.click { }
    }
}