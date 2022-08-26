package com.mirkowu.mvm

import android.Manifest
import android.content.*
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.provider.MediaStore
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import com.mirkowu.lib_base.adapter.BaseFragmentPagerAdapter
import com.mirkowu.lib_base.mediator.EmptyMediator
import com.mirkowu.lib_base.util.bindingView
import com.mirkowu.lib_bugly.BuglyManager
import com.mirkowu.lib_bugly.UpgradeDialog
import com.mirkowu.lib_util.LogUtil
import com.mirkowu.lib_util.utilcode.util.BarUtils
import com.mirkowu.lib_util.utilcode.util.ObjectUtils
import com.mirkowu.lib_webview.CommonWebActivity
import com.mirkowu.mvm.base.BaseActivity
import com.mirkowu.mvm.bean.Image
import com.mirkowu.mvm.service.MyService
import com.mirkowu.mvm.ui.mvc.MVCActivity
import com.mirkowu.mvm.ui.mvp.MVPActivity
import com.mirkowu.mvm.ui.mvvm.MVVMActivity
import com.mirkowu.mvm.ui.mvvm.viewbinding.DataBindingFragment
import com.mirkowu.mvm.ui.recycelerview.GridListActivity
import com.mirkowu.mvm.ui.webview.WebActivity

class MainActivity : BaseActivity<EmptyMediator>() {
    companion object {
        fun start(context: Context) {
            val starter = Intent(context, MainActivity::class.java)
            context.startActivity(starter)
        }
    }

    val binding by bindingView(com.mirkowu.mvm.databinding.ActivityMainBinding::inflate)
    lateinit var pagerAdapter: BaseFragmentPagerAdapter

    override fun initMediator(): EmptyMediator {
        return super.initMediator()
    }

    override fun initStatusBar() {
        super.initStatusBar()
        BarUtils.transparentStatusBar(this)
    }

    override fun getLayoutId() = R.layout.activity_main

    override fun initialize() {
        LogUtil.d("点击 跳过 initialize")
        pagerAdapter =
            BaseFragmentPagerAdapter(
                supportFragmentManager,
                mutableListOf<Fragment>(
                    DataBindingFragment.newInstance(),
//                    DataBindingFragment.newInstance(),
                )
            )

        binding.vpHome.apply {
            adapter = pagerAdapter
            isScroll = true
            offscreenPageLimit = pagerAdapter.count
        }
        val PERMISSION_STORAGE = arrayOf(
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.READ_PHONE_STATE
        )

        BuglyManager.checkUpgrade { hasNewVersion, upgradeInfo ->
            Log.e("BuglyManager", "setUpgradeListener:   upgradeInfo=$upgradeInfo")
            if (upgradeInfo != null) {
                UpgradeDialog.show(supportFragmentManager, upgradeInfo)
            }
        }


        LogUtil.d("点击 跳过 initialize end---")

        TestKeepMethod().testA()
        TestKeepMethod().testB()

    }
    private val projection = arrayOf(
        MediaStore.Images.Media._ID,
        MediaStore.Images.Media.BUCKET_ID,
        MediaStore.Images.Media.BUCKET_DISPLAY_NAME,
        MediaStore.Images.Media.DATA,
        if (Build.VERSION.SDK_INT > 28) MediaStore.Images.Media.DATE_MODIFIED else MediaStore.Images.Media.DATE_TAKEN,
        MediaStore.Images.Media.ORIENTATION,
        MediaStore.Images.Media.WIDTH,
        MediaStore.Images.Media.HEIGHT,
        MediaStore.Images.Media.SIZE
    )
    private val IMAGE_PROJECTION = arrayOf(
        MediaStore.MediaColumns.DATA,
        MediaStore.MediaColumns.DISPLAY_NAME,
        MediaStore.MediaColumns.DATE_ADDED,
        MediaStore.MediaColumns.MIME_TYPE,
        MediaStore.MediaColumns.SIZE,
        MediaStore.MediaColumns.DURATION,
        MediaStore.MediaColumns._ID
    )
    private val selectionSingle = "(" + MediaStore.Files.FileColumns.MEDIA_TYPE + "=?)"
    private val selectionAll = ("(" + MediaStore.Files.FileColumns.MEDIA_TYPE + "=?"
            + " OR " + MediaStore.Files.FileColumns.MEDIA_TYPE + "=?)")

    private val selectionArgsImage =
        arrayOf(MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE.toString())
    private val selectionArgsVideo =
        arrayOf(MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO.toString())
    private val selectionAllArgs = arrayOf(
        MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE.toString(),
        MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO.toString()
    )
    val galleryPickedImageList: MutableLiveData<List<Image>> = MutableLiveData()
    fun query(contentResolver: ContentResolver) {
       Thread {
           queryInternal(contentResolver)
       }.start()

    }

    private   fun queryInternal(
        contentResolver: ContentResolver,
        force: Boolean = galleryPickedImageList.value == null
    )  {
        if (!force) {
            return
        }
        var time=System.currentTimeMillis();
        val list = ArrayList<Image>()
        val contentUri = MediaStore.Files.getContentUri("external");
        contentResolver.query(
//            contentUri,
//            IMAGE_PROJECTION,
//            selectionSingle,
//            selectionArgsImage,
//            IMAGE_PROJECTION[2] + " DESC"
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            projection,
            null,
            null,
            (if (Build.VERSION.SDK_INT > 28) MediaStore.Images.Media.DATE_MODIFIED else MediaStore.Images.Media.DATE_TAKEN) + " DESC"
        )?.use { cursor ->
            LogUtil.e(
                "pickTest",
                "内部finish cursor =" + (System.currentTimeMillis() - time) + " " + Thread.currentThread().name
            )
            val imageIdColumn = cursor.getColumnIndex(MediaStore.Images.Media._ID)
            val bucketIdColumn = cursor.getColumnIndex(MediaStore.Images.Media.BUCKET_ID)
            val bucketNameColumn =
                cursor.getColumnIndex(MediaStore.Images.Media.BUCKET_DISPLAY_NAME)
            val dataColumn = cursor.getColumnIndex(MediaStore.Images.Media.DATA)
            val dateColumn =
                cursor.getColumnIndex(if (Build.VERSION.SDK_INT > 28) MediaStore.Images.Media.DATE_MODIFIED else MediaStore.Images.Media.DATE_TAKEN)
            val orientationColumn = cursor.getColumnIndex(MediaStore.Images.Media.ORIENTATION)
            val widthColumn = cursor.getColumnIndex(MediaStore.Images.Media.WIDTH)
            val heightColumn = cursor.getColumnIndex(MediaStore.Images.Media.HEIGHT)
            val sizeColumn = cursor.getColumnIndex(MediaStore.Images.Media.SIZE)

            while (cursor.moveToNext()) {
                val path = cursor.getString(dataColumn)
                if (path.isNullOrBlank()) {
                    continue
                }

                val imageId = cursor.getInt(imageIdColumn)
                val bucketId = cursor.getInt(bucketIdColumn)
                val bucketName = cursor.getString(bucketNameColumn) ?: ""
                val dateTaken = cursor.getLong(dateColumn)
                val orientation = cursor.getInt(orientationColumn)
                val width = cursor.getInt(widthColumn)
                val height = cursor.getInt(heightColumn)
                val size = cursor.getLong(sizeColumn)

                val contentUri: Uri = ContentUris.withAppendedId(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    imageId.toLong()
                )

                // Stores column values and the contentUri in a local object
                // that represents the media file.
                val image = com.mirkowu.mvm.bean.Image(imageId, contentUri, bucketName, size, dateTaken)
                list += image
            }
            LogUtil.e(
                "pickTest",
                "内部finish 循环 =" + (System.currentTimeMillis() - time) + " " + Thread.currentThread().name
            )

            galleryPickedImageList.postValue(list)
        }
    }
    fun webLocalClick(view: View?) {
//        MVCActivity.start(this)
//        WebActivity.start(context, "ces", "file:///android_asset/test.html")
        WebActivity.start(context, "ces", "file:///android_asset/jsbridge_test.html")
//        WebActivity.start(context, "ces", "http://www.baidu.com")
        Log.d("WebActivity", "start: ")
    }

    fun webNetClick(view: View?) {
//        CommonWebActivity.start(context, "ces", "http://www.baid")
//        WebViewActivity.start(context, "ces", "https://x5.tencent.com/docs/questions.html")
        CommonWebActivity.start(
            context,
            "标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题",
            "http://www.baidu.com/"
        )
        Log.d("WebActivity", "start: ")
    }

    fun mvcClick(view: View?) {
        MVCActivity.start(this)
    }

    fun mvpClick(view: View?) {
        MVPActivity.start(this)
    }

    fun mvvmClick(view: View?) {
        // MVVMActivity.start(this)
        val intent = Intent(context, MVVMActivity::class.java)
//        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivityForResult(intent, 1)
    }

    override fun startActivity(intent: Intent?) {
        super.startActivity(intent)
    }

    fun bindingClick(view: View?) {
//        startActivity(Intent(this, DataBindingActivity::class.java))
       // openOtherActivity()
        query( contentResolver)
    }
    //        Comparator.comparingInt(  value -> );
//        Looper.myLooper();
//        Looper.loop();
//        Looper.myLooper().setMessageLogging();
//        HandlerThread thread=new HandlerThread("");
//        thread.start();
//        thread.getLooper()


    fun openOtherActivity() {
        LogUtil.e("TestActivity getTaskId= " + getTaskId())
        val intent = Intent()
//        intent.setClassName("com.mirkowu.metronotice","com.mirkowu.metronotice.MainActivity")
        intent.setClassName("com.mirkowu.metronotice","com.mirkowu.metronotice.ui.SplashActivity")
//        val componentName = ComponentName("com.mirkowu.metronotice", "com.mirkowu.metronotice.MainActivity")
//        intent.component = componentName
               intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent)
    }

    val connection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            if (service is MyService.MyBinder) {
                service.service
            }
        }

        override fun onServiceDisconnected(name: ComponentName?) {

        }

    }

    override fun startActivity(intent: Intent?, options: Bundle?) {
        super.startActivity(intent, options)
    }
    fun listClick(view: View?) {
//        LinearListActivity.start(this)
        GridListActivity.start(this)

        val intent = Intent(this, MyService::class.java)
        bindService(intent, connection, Context.BIND_AUTO_CREATE)
    }

    override fun onDestroy() {
        pagerAdapter.clear()
        BuglyManager.removeUpgradeListener()
        unbindService(connection)
        super.onDestroy()
    }

    override fun onRestart() {
        super.onRestart()
        Log.d("LifeTest", "MainActivity onRestart: ")
    }

    override fun onStart() {
        super.onStart()
        Log.d("LifeTest", "MainActivity onStart: taskId=" + this.taskId)
    }

    override fun onStop() {
        super.onStop()
        Log.d("LifeTest", "MainActivity onStop: ")
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        Log.d("LifeTest", "MainActivity onSaveInstanceState: ")
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        Log.d("LifeTest", "MainActivity onRestoreInstanceState: ")
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Log.d("LifeTest", "MainActivity onActivityResult: ")
    }
}