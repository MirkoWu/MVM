//package com.mirkowu.lib_webview
//
//
//import android.app.Activity
//import android.content.Intent
//import android.graphics.Bitmap
//import android.net.Uri
//import android.os.Build
//import android.os.Build.VERSION_CODES.LOLLIPOP
//import android.os.Environment
//import android.provider.MediaStore
//import android.text.TextUtils
//import android.view.KeyEvent
//import android.view.View
//import android.view.ViewGroup
//import android.webkit.*
//import android.widget.FrameLayout
//import android.widget.ProgressBar
//import android.widget.Toast
//import androidx.annotation.RequiresApi
//import androidx.appcompat.app.AlertDialog
//import com.mirkowu.lib_base.activity.BaseMVMActivity
//import com.mirkowu.lib_base.mediator.BaseMediator
//import com.mirkowu.lib_util.FileUtil
//import com.mirkowu.lib_widget.stateview.StateView
//import java.io.File
//
//
///**
// * webView 基类，做好了大部分配置，子类根据需要重写方法
// */
//abstract class BaseWebViewActivity<M : BaseMediator<*, *>> : BaseMVMActivity<M>() {
//
//    protected lateinit var mWebView: WebView
//    var uploadMessage: ValueCallback<Uri>? = null
//    var uploadMessageArray: ValueCallback<Array<Uri>>? = null
//
//    val REQ_CAMERA = 0x1221
//    val REQ_ALBUM = 0x1222
//    val REQ_FILE = 0x1223
//    val DCIMPATH = "${Environment.getExternalStorageDirectory()}/DCIM"
//    var imageName: String = ""
//    var isLoadError: Boolean = false
//
//    private lateinit var mProgressBar: ProgressBar
//
//    private lateinit var mStateView: StateView
//    override fun getLayoutId() = R.layout.webview_layout_common_web_view
//    override fun initialize() {
//        mWebView = WebView(applicationContext)
//
//        val rootView: ViewGroup = findViewById(R.id.flRootView)
//        val params = FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
//        rootView.addView(mWebView, 0, params)
//
//        mProgressBar = findViewById(R.id.mProgressBar)
//        mStateView = findViewById(R.id.mStateView)
//
//        mStateView.setOnRefreshListener { onRefresh() }
//        initWebView(mWebView)
//    }
//
//    /**
//     * 初始化WebView
//     */
//    fun initWebView(mWebView: WebView) {
//        this.mWebView = mWebView
//        val webSettings = mWebView.settings
//
//        //如果访问的页面中要与Javascript交互，则webview必须设置支持Javascript
//        webSettings.javaScriptEnabled = true
//        //支持插件
//        webSettings.pluginState = WebSettings.PluginState.ON
//
//
//        //设置自适应屏幕，两者合用  这样会使加载文本时 文字变小
//        webSettings.useWideViewPort = true; //将图片调整到适合webview的大小
//        webSettings.loadWithOverviewMode = true; // 缩放至屏幕的大小
//        webSettings.setRenderPriority(WebSettings.RenderPriority.HIGH)//提高渲染的优先级
//
//        //缩放操作
//        webSettings.setSupportZoom(true) //支持缩放，默认为true。是下面那个的前提。
//        webSettings.builtInZoomControls = true //设置内置的缩放控件。若为false，则该WebView不可缩放
//        webSettings.displayZoomControls = false //隐藏原生的缩放控件
//
//        //开启DomStorage缓存
//        webSettings.domStorageEnabled = true
//        //启用数据库
////        webSettings.databaseEnabled = true
////        //设置定位的数据库路径
////        val dir = this.getDir("database", Context.MODE_PRIVATE).getPath()
////        webSettings.setGeolocationDatabasePath(dir)
//
//        //其他细节操作
//        webSettings.cacheMode = WebSettings.LOAD_NO_CACHE //不使用缓存
//        webSettings.allowFileAccess = true //设置可以访问文件
//        webSettings.javaScriptCanOpenWindowsAutomatically = true //支持通过JS打开新窗口
//        webSettings.loadsImagesAutomatically = true //支持自动加载图片
//        webSettings.defaultTextEncodingName = "utf-8"//设置编码格式
//        //支持内容重新布局
//        webSettings.layoutAlgorithm = WebSettings.LayoutAlgorithm.SINGLE_COLUMN
//        //5.0以上允许加载http和https混合的页面(5.0以下默认允许，5.0+默认禁止)
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            webSettings.mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
//        }
//
//        setWebViewClient(mWebView)
//        setWebChromeClient(mWebView)
//        setDownloadListener(mWebView)
//
//
//    }
//
//    /**
//     * 刷新
//     */
//    fun onRefresh() {
//        isLoadError = false
//        updateProgress(0)
//        mWebView.reload()
//    }
//
//    fun updateProgress(progress: Int) {
//        if (isDestroyed || isLoadError) return
//        if (progress <= 99) {
//            mProgressBar.visibility = View.VISIBLE
//            mProgressBar.progress = progress
//        } else {
//            mStateView.setGoneState()
//            mProgressBar.visibility = View.GONE
//        }
//    }
//
//    fun updateError(url: String, error: String) {
//        if (isDestroyed) return
//        isLoadError = true
//        mProgressBar.visibility = View.GONE
//
//        if (!TextUtils.isEmpty(error)) {
//            mStateView.setShowState(0, url + "\n" + error, true)
//        } else {
//            mStateView.setShowState(0, url + "\n" + "加载失败，请检查网络或点击重试", true)
//        }
//    }
//
//    open fun loadUrl(url: String) {
//        mWebView.loadUrl(url)
//
//    }
//
//
//    /**
//     * 方式2：加载html文本
//     *
//     * @param content
//     */
//    open fun loadText(content: String) {
//        val webSettings = mWebView.settings
//        //设置自适应屏幕，两者合用
//        webSettings.useWideViewPort = false //将图片调整到适合webview的大小
//        webSettings.loadWithOverviewMode = false // 缩放至屏幕的大小
//
//
//        /*** 完美自适应屏幕  */
//        val sb = StringBuilder()
//        sb.append(content)
//                .append("<html>")
//                .append("<head>")
//                .append("<meta charset=\\\"utf-8\\\">")
//                .append("<meta id=\\\"viewport\\\" name=\\\"viewport\\\" content=\\\"width=device-width*0.9,initial-scale=1.0,maximum-scale=1.0,user-scalable=false\\\" />")
//                .append("<meta name=\\\"apple-mobile-web-app-capable\\\" content=\\\"yes\\\" />")
//                .append("<meta name=\\\"apple-mobile-web-app-status-bar-style\\\" content=\\\"black\\\" />")
//                .append("<meta name=\\\"black\\\" name=\\\"apple-mobile-web-app-status-bar-style\\\" />")
//                .append("<style>img{width:100%;}</style>")
//                .append("<style>iframe{width:100%;}</style>")
//                .append("<style>table{width:100%;}</style>")
//                .append("<style>body{font-size:18px;}</style>")
//                .append("<title>mWebView</title>")
//
//
//        mWebView.loadDataWithBaseURL(null, sb.toString(), "text/html", "utf-8", null)
//    }
//
//    /**
//     * 下载监听
//     */
//    open fun setDownloadListener(mWebView: WebView) {
//        mWebView.setDownloadListener { url, userAgent, contentDisposition, mimetype, contentLength ->
//            try {
//                val intent = Intent()
//                intent.action = Intent.ACTION_VIEW
//                intent.data = Uri.parse(url)
//                startActivity(intent)
//            } catch (e: Exception) {
//                e.printStackTrace()
//            }
//        }
//    }
//
//    /**
//     * 子类需要时可重写
//     */
//    open fun setWebViewClient(mWebView: WebView) {
//        mWebView.webViewClient = object : WebViewClient() {
//            override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
//                return this@BaseWebViewActivity.shouldOverrideUrlLoading(view, url)
//            }
//
//            @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
//            override fun shouldOverrideUrlLoading(view: WebView, request: WebResourceRequest?): Boolean {
//                return this@BaseWebViewActivity.shouldOverrideUrlLoading(view, request!!.url.toString())
//            }
//
//            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
//                super.onPageStarted(view, url, favicon)
//                if (!isDestroyed) {
//                    isLoadError = false
//                    //mStateView?.setGoneState()
//                }
////                LogUtil.d("onPageStarted url= " )
//            }
//
//            override fun onPageFinished(view: WebView?, url: String?) {
//                super.onPageFinished(view, url)
//                updateProgress(100)
//            }
//
//            @RequiresApi(Build.VERSION_CODES.M)
//            override fun onReceivedError(
//                    view: WebView?,
//                    request: WebResourceRequest?,
//                    error: WebResourceError?
//            ) {
//                super.onReceivedError(view, request, error)
//
//                if (TextUtils.equals("net::ERR_INTERNET_DISCONNECTED", error?.description)
//                        || request!!.isForMainFrame()) {
//                    updateError(request!!.url.toString(), "")
//                }
//            }
//
//            override fun onReceivedError(
//                    view: WebView?,
//                    errorCode: Int,
//                    description: String?,
//                    failingUrl: String?
//            ) {
//                super.onReceivedError(view, errorCode, description, failingUrl)
//                if (TextUtils.equals("net::ERR_INTERNET_DISCONNECTED", description)) {
//                    updateError(failingUrl!!, "")
//                }
//            }
//
//            override fun onReceivedHttpError(
//                    view: WebView?,
//                    request: WebResourceRequest?,
//                    errorResponse: WebResourceResponse?
//            ) {
//                super.onReceivedHttpError(view, request, errorResponse)
//                // 这个方法在 android 6.0才出现
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//                    val statusCode = errorResponse!!.statusCode
//                    if (404 == statusCode || 500 == statusCode) {
//                        updateError(request!!.url.toString(), "")// 加载自定义错误页面
//                    }
//                }
//            }
//
//        }
//    }
//
//    /**
//     * 重写加载路径 默认不重写
//     */
//    open fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
//        return false
//    }
//
//    /**
//     * 子类需要时可重写
//     */
//    open fun setWebChromeClient(mWebView: WebView) {
//        mWebView.webChromeClient = object : WebChromeClient() {
//            /**
//             * 16(Android 4.1.2) <= API <= 20(Android 4.4W.2)回调此方法
//             */
//            fun openFileChooser(
//                    valueCallback: ValueCallback<Uri>,
//                    acceptType: String,
//                    capture: String
//            ) {
//                uploadMessage = valueCallback
//                fileChooser(acceptType)
//            }
//
//            /**
//             * API >= 21(Android 5.0.1)回调此方法
//             */
//            @RequiresApi(LOLLIPOP)
//            override fun onShowFileChooser(
//                    webView: WebView?,
//                    filePathCallback: ValueCallback<Array<Uri>>?,
//                    fileChooserParams: FileChooserParams?
//            ): Boolean {
//                uploadMessageArray = filePathCallback!!
//                fileChooser(fileChooserParams!!.acceptTypes[0])
//                return true//这里要返回true 不然选择文件时会crash
//                //  return super.onShowFileChooser(webView, filePathCallback, fileChooserParams)
//            }
//
//            override fun onProgressChanged(view: WebView?, newProgress: Int) {
//                super.onProgressChanged(view, newProgress)
//                updateProgress(newProgress)
//            }
//
//            override fun onReceivedTitle(view: WebView?, title: String?) {
//                super.onReceivedTitle(view, title)
//                // android 6.0 以下通过title获取判断
//                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
//                    if (title != null && (title.contains("404") || title.contains("500") ||
//                                    title.contains("Error") || title.contains("找不到网页") ||
//                                    title.contains("网页无法打开"))
//                    ) {
//                        updateError(view!!.url!!, title) //加载自定义错误页面
//                    }
//                }
//            }
//        }
//    }
//
//    /**
//     * 根据类型选取文件
//     * 不需要动态权限申请 如果加了要处理被拒绝的情况
//     */
//    open fun fileChooser(acceptType: String) {
//        when (acceptType) {
//            "image/*" -> {
//                val selectPicTypeStr = arrayOf("拍摄", "从相册中选择")
//                AlertDialog.Builder(this)
//                        .setOnCancelListener { onReceiveValue(null) }
//                        .setItems(selectPicTypeStr) { dialog, which ->
//                            when (which) {
//                                0 -> {
//                                    openCamera()
//                                }
//                                1 -> {
//                                    selectImage(REQ_ALBUM)
//                                }
//                            }
//                        }
//                        .show()
//
//            }
//            "video/*" -> {
//                selectVideo(REQ_ALBUM)
//            }
//            else -> {
//                selectFile(REQ_FILE)
//            }
//        }
//    }
//
//
//    /**
//     * 打开摄像头拍照
//     */
//    open fun openCamera() {
//        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
//        imageName = System.currentTimeMillis().toString() + ".png"
//        val file = File(DCIMPATH)
//        if (!file.exists()) {
//            file.mkdirs()
//        }
//        intent.putExtra(
//                MediaStore.EXTRA_OUTPUT, FileUtil.createUri(this, File(DCIMPATH, imageName))
//        )
//        startActivityForResult(intent, REQ_CAMERA)
//    }
//
//    /**
//     * 选择图片
//     */
//    fun selectImage(requestCode: Int) {
//        if (!FileUtil.hasSDCard()) {
//            onReceiveValue(null)
//            return
//        }
//        val intent = Intent()
//        intent.type = "image/*"
//        intent.action = Intent.ACTION_PICK
//        intent.data =
//                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI;//使用以上这种模式，并添加以上两句
//        startActivityForResult(intent, requestCode)
//    }
//
//    /**
//     * 选择视频文件
//     */
//    open fun selectVideo(requestCode: Int) {
//        if (!FileUtil.hasSDCard()) {
//            onReceiveValue(null)
//            return
//        }
//        val intent = Intent()
//        intent.action = Intent.ACTION_PICK
//        intent.type = "video/*"
//        startActivityForResult(Intent.createChooser(intent, "选择要导入的视频"), requestCode)
//    }
//
//    /**
//     * 为空的时候就 就统一选择文件
//     */
//    fun selectFile(requestCode: Int) {
//        if (!FileUtil.hasSDCard()) {
//            onReceiveValue(null)
//            return
//        }
//        val intent = Intent()
//        intent.action = Intent.ACTION_PICK
//        intent.type = "*/*"
//        startActivityForResult(Intent.createChooser(intent, "选择要导入的文件"), requestCode)
//    }
//
//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//        if (resultCode !== Activity.RESULT_OK) {
//            onReceiveValue(null)
//            return
//        }
//        when (requestCode) {
//            REQ_CAMERA -> {
//                val fileCamera = File(DCIMPATH, imageName)
//                handleFile(fileCamera)
//            }
//            REQ_ALBUM -> {
//                val uri = data!!.data
//                if (uri != null) {
//                    var absolutePath = FileUtil.getRealFilePath(context, uri)
//                    if (absolutePath != null) {
//                        val fileAlbum = File(absolutePath)
//                        handleFile(fileAlbum)
//                    }
//                } else {
//                    showToast("选取失败")
//                    onReceiveValue(null)
//                }
//            }
//            REQ_FILE -> {
//                val uri = data!!.data
//                if (uri != null) {
//                    var absolutePath = FileUtil.getRealFilePath(context, uri)
//                    if (absolutePath != null) {
//                        val fileAlbum = File(absolutePath)
//                        handleFile(fileAlbum)
//                    } else {
//                        showToast("选取失败")
//                        onReceiveValue(null)
//                    }
//                } else {
//                    showToast("选取失败")
//                    onReceiveValue(null)
//                }
//            }
//        }
//    }
//
//    fun showToast(string: String) {
//        Toast.makeText(context, string, Toast.LENGTH_SHORT).show()
//    }
//
//    open fun handleFile(file: File) {
//        if (file.isFile()) {
//            onReceiveValue(file)
//        } else {
//            onReceiveValue(null)
//        }
//    }
//
//
//    /**
//     * 接受参数结束
//     */
//    fun onReceiveValue(file: File?) {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            if (uploadMessageArray != null) {
//                if (file == null) {
//                    uploadMessageArray!!.onReceiveValue(null)
//                } else {
//                    val uri = Uri.fromFile(file)
//                    val uriArray = arrayOf(uri)
//                    uploadMessageArray!!.onReceiveValue(uriArray)
//                    uploadMessageArray = null
//                }
//            }
//        } else {
//            if (uploadMessage != null) {
//                if (file == null) {
//                    uploadMessageArray!!.onReceiveValue(null)
//                } else {
//                    val uri = Uri.fromFile(file)
//                    uploadMessage!!.onReceiveValue(uri)
//                    uploadMessage = null
//                }
//            }
//        }
//    }
//
//
//    //处理返回键
//    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
//        if (keyCode == KeyEvent.KEYCODE_BACK) {
//            if (this.mWebView.canGoBack()) {
//                this.mWebView.goBack()
//                return true
//            }
//        }
//        return super.onKeyDown(keyCode, event)
//    }
//
//    public override fun onResume() {
//        super.onResume()
//        mWebView.onResume()
//        //通知内核尝试停止所有处理，如动画和地理位置，但是不能停止Js，
//        //如果想全局停止Js，可以调用pauseTimers()全局停止Js，调用onResume()恢复。
//        mWebView.resumeTimers()
//    }
//
//    public override fun onPause() {
//        super.onPause()
//        mWebView.onPause()
//        mWebView.pauseTimers()
//    }
//
//    override fun onDestroy() {
//        (mWebView.parent as ViewGroup).removeView(mWebView)
//        mWebView.destroy()
//        super.onDestroy()
//    }
//}
