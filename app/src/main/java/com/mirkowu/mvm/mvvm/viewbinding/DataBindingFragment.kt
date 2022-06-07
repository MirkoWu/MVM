package com.mirkowu.mvm.mvvm.viewbinding

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.mirkowu.lib_base.util.bindingView
import com.mirkowu.lib_bugly.BuglyManager
import com.mirkowu.lib_camera.CameraActivity
import com.mirkowu.lib_network.state.observerRequest
import com.mirkowu.lib_qr.QRScanner
import com.mirkowu.lib_qr.ScanConfig
import com.mirkowu.lib_util.LogUtil
import com.mirkowu.lib_util.PermissionsUtil
import com.mirkowu.lib_util.ktxutil.click
import com.mirkowu.lib_util.utilcode.util.LanguageUtils
import com.mirkowu.lib_util.utilcode.util.ToastUtils
import com.mirkowu.mvm.R
import com.mirkowu.mvm.base.BaseFragment
import com.mirkowu.mvm.databinding.FragmentDatabindingBinding
import com.mirkowu.mvm.download.DownloadActivity
import com.mirkowu.mvm.imagepicker.ImagePickerActivity
import com.mirkowu.mvm.mvvm.MVVMMediator
import com.mirkowu.mvm.widgetdemo.WidgetDemoActivity
import java.util.*


class DataBindingFragment : BaseFragment<MVVMMediator>() {
    companion object {

        val TAG = "DataBindingFragment"
        fun newInstance(): DataBindingFragment {
            val args = Bundle()

            val fragment = DataBindingFragment()
            fragment.arguments = args
            return fragment
        }
    }
//    val binding  :FragmentDatabindingBinding  by binding()

    val binding by bindingView { FragmentDatabindingBinding.bind(view!!) }

//    var _binding: FragmentDatabindingBinding? = null
//    val binding get() = _binding!!

    override fun getLayoutId() = R.layout.fragment_databinding
    override fun initialize() {
        LogUtil.e("测试 fragment initialize")
//        BarUtils.transparentStatusBar(activity!!)
//        BarUtils.setStatusBarLightMode(activity!!, true)
//        BarUtils.setStatusBarColor(activity!!, Color.parseColor("#50000000"))

        binding.btnStatusbar1.click {
            binding.toolbar.setShowStatusBarHeight(!binding.toolbar.isShowStatusBarHeight)
            LogUtil.e("toolbar paddingTop = " + binding.toolbar.paddingTop)
        }

        binding.btnText.text = "这是fragment databinding"
        binding.btnText.setOnClickListener {
            binding.btnText.text = "点击了${System.currentTimeMillis()}"
        }
        binding.btnUi.click {
            WidgetDemoActivity.start(context!!)
        }
        val list = mutableListOf("", "", "", "", "", "", "", "", "", "")
        val listAdapter = DataBindingAdapter(list)
        binding.rvList.apply {
            adapter = listAdapter
            layoutManager = LinearLayoutManager(context)
        }
        binding.tvBold.setBoldStyle()
        binding.tvBoldMid.setMediumStyle()
        binding.tvBoldDefault.setBoldWith(1.5f)
        binding.tvNormal.apply { paint.isFakeBoldText = true }

        binding.btnZh.click { LanguageUtils.applyLanguage(Locale.SIMPLIFIED_CHINESE, true) }
        binding.btnEn.click { LanguageUtils.applyLanguage(Locale.ENGLISH, false) }
        binding.btnPermission.click {
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
//                if (!Environment.isExternalStorageLegacy()) {//是否传统的存储
//                }
//                //判断有没有完全的外部存储访问权限
//                if (!Environment.isExternalStorageManager()) {
//                    //没有就申请权限，需要用户手动授权
//                    val intent = Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION)
//                    intent.data = Uri.parse("package:" + context!!.packageName)
//                    startActivityForResult(intent, REQUEST_CODE)
//                }
//            }
            PermissionsUtil.getInstance().requestStorageManage(this,
                object : PermissionsUtil.OnPermissionsListener {
                    override fun onPermissionGranted(requestCode: Int) {
                        ToastUtils.showShort("已授权------")
                    }

                    override fun onPermissionShowRationale(
                        requestCode: Int,
                        permissions: Array<out String>
                    ) {
                        ToastUtils.showShort("询问")
                    }

                    override fun onPermissionDenied(requestCode: Int) {
                        ToastUtils.showShort("已拒绝")
                    }

                })

//            PermissionsUtil.getInstance().requestPermissions(this, PermissionsUtil.GROUP_CAMERA,
//                object : PermissionsUtil.OnPermissionsListener {
//                    override fun onPermissionGranted(requestCode: Int) {
//                        Log.d(TAG, "onPermissionGranted: ")
//                    }
//
//                    override fun onPermissionShowRationale(
//                        requestCode: Int,
//                        permissions: Array<out String>
//                    ) {
//                        Log.d(TAG, "onPermissionShowRationale: ")
//
//                    }
//
//                    override fun onPermissionDenied(requestCode: Int) {
//                        Log.d(TAG, "onPermissionDenied: ")
//
//                    }
//                })
        }
        binding.btnImagePicker.click {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                ToastUtils.showShort("isExternalStorageLegacy = " + Environment.isExternalStorageLegacy())
            }
            startActivity(Intent(context, ImagePickerActivity::class.java))
        }
        binding.btnDown.click {
            startActivity(Intent(context, DownloadActivity::class.java))
        }
        binding.btnBuglyUpgrade.click {
            showLoadingDialog("检查新版本")
            BuglyManager.checkUpgrade { hasNewVersion, upgradeInfo ->
                Log.e("BuglyManager", "setUpgradeListener:   upgradeInfo=$upgradeInfo  ")
                hideLoadingDialog()
                if (upgradeInfo != null) {
                    com.mirkowu.lib_bugly.UpgradeDialog.show(childFragmentManager, upgradeInfo)
                } else {
                    ToastUtils.showShort("当前已是最新版本!")
                }
            }
        }
        binding.btnUpgrade.click {
            // BuglyManager.checkUpgrade(true, false)
            val url =
                "https://outexp-beta.cdn.qq.com/outbeta/2021/06/18/commirkowumvm_1.0.1_56987f9a-fb39-56d5-9ac4-a4c055633672.apk"
            BuglyManager.checkUpgrade { hasNewVersion, upgradeInfo ->
                Log.e("BuglyManager", "setUpgradeListener:   upgradeInfo=$upgradeInfo  ")


                if (upgradeInfo != null) {
                    com.mirkowu.lib_bugly.UpgradeDialog.show(childFragmentManager, upgradeInfo)
                } else {
                    ToastUtils.showShort("当前已是最新版本!")
                }
            }

        }
        binding.btnQr.click {
            QRScanner.getInstance().setScanConfig(
                ScanConfig()
                    .setShowFlashlight(true)
                    .setShowAlbumPick(true)
                    .setCameraAutoZoom(true)
            )
                .setOnScanResultListener {
                    ToastUtils.showShort("扫描结果：" + it)
                    LogUtil.e("扫描结果：" + it)
                }.start(context)
        }
        binding.btnCamera.click {
            startActivity(Intent(context, CameraActivity::class.java))
        }
        binding.btnCrash.click {
            throw RuntimeException("测试BUG")
        }

        var time = System.currentTimeMillis();
        mMediator.mPingResult.observerRequest(this, onSuccess = {
            time = System.currentTimeMillis() - time
            if (it!!) {
                binding.tvNetworkStatus.setText("检测耗时${time}ms, 网络OK")
                binding.tvNetworkStatus.setTextColor(Color.GREEN)
//                binding.tvNetworkStatus.visibility = View.GONE
            } else {
                binding.tvNetworkStatus.setText("检测耗时${time}ms, 网络不可用")
                binding.tvNetworkStatus.setTextColor(Color.RED)
                binding.tvNetworkStatus.visibility = View.VISIBLE
            }
        })
        mMediator.getPing()

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d(TAG, "onCreate: $position")
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.d(TAG, "onCreateView: $position")
        return super.onCreateView(inflater, container, savedInstanceState)

//        _binding = FragmentDatabindingBinding.inflate(inflater, container, false)
//        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        QRScanner.getInstance().removeOnScanResultListener()
    }

    //    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        Log.d(TAG, "onViewCreated: $position")
//        super.onViewCreated(view, savedInstanceState)
//    }
//
//    override fun onActivityCreated(savedInstanceState: Bundle?) {
//        Log.d(TAG, "onActivityCreated: $position")
//        super.onActivityCreated(savedInstanceState)
//    }
//
    override fun onAttach(context: Context) {
        Log.d(TAG, "onAttach: $position")
        super.onAttach(context)
    }

    override fun onStart() {
        Log.d(TAG, "onStart: $position")
        super.onStart()
    }

    //
//    override fun onAttachFragment(childFragment: Fragment) {
//        Log.d(TAG, "onAttachFragment: $position")
//        super.onAttachFragment(childFragment)
//    }
//
    override fun onResume() {
        Log.d(TAG, "onResume: $position")
        LogUtil.d("点击 跳过 fragment onResume")
        super.onResume()
    }

    override fun onPause() {
        Log.d(TAG, "onPause: $position")
        super.onPause()
    }

    //
    override fun onStop() {
        Log.d(TAG, "onStop: $position")
        super.onStop()
    }

    //
//    override fun onDestroyView() {
//        Log.d(TAG, "onDestroyView: $position")
////        _binding = null
//        super.onDestroyView()
//    }
//
    override fun onDestroy() {
        Log.d(TAG, "onDestroy: $position")
        super.onDestroy()
    }

    //
    override fun onDetach() {
        Log.d(TAG, "onDetach: $position")
        super.onDetach()
    }
}