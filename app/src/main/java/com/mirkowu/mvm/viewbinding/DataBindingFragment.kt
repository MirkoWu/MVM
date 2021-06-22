package com.mirkowu.mvm.viewbinding

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.mirkowu.lib_qr.QRScanner
import com.mirkowu.lib_qr.ScanConfig
import com.mirkowu.lib_upgrade.AppUpgradeDialog
import com.mirkowu.lib_upgrade.BuglyManager
import com.mirkowu.lib_upgrade.IUpgradeInfo
import com.mirkowu.lib_util.LogUtil
import com.mirkowu.lib_util.PermissionsUtil
import com.mirkowu.lib_util.ktxutil.click
import com.mirkowu.lib_util.utilcode.util.LanguageUtils
import com.mirkowu.lib_util.utilcode.util.ToastUtils
import com.mirkowu.lib_widget.dialog.PromptDialog
import com.mirkowu.mvm.R
import com.mirkowu.mvm.base.BaseFragment
import com.mirkowu.mvm.databinding.FragmentDatabindingBinding
import com.mirkowu.mvm.download.DownloadActivity
import com.mirkowu.mvm.imagepicker.ImagePickerActivity
import com.mirkowu.mvm.mvvm.MVVMMediator
import com.tencent.bugly.Bugly
import com.tencent.bugly.beta.upgrade.UpgradeStateListener
import java.lang.RuntimeException
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

    val binding by binding { FragmentDatabindingBinding.bind(view!!) }

//    var _binding: FragmentDatabindingBinding? = null
//    val binding get() = _binding!!

    override fun getLayoutId() = R.layout.fragment_databinding
    override fun initialize() {
        binding.btnText.text = "这是fragment databinding"
        binding.btnText.setOnClickListener {
            binding.btnText.text = "点击了${System.currentTimeMillis()}"
            DataBindingDialog(context!!).show()
            PromptDialog().setTitle("温馨提示")
                    .setContent("确认关闭吗？")
                    .setUseDefaultButton()
                    .setIcon(R.mipmap.ic_launcher)
                    .show(childFragmentManager)
            showLoadingDialog("Toast测试")
//            showLoadingDialog("Toast测试")
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
        binding.btnEn.click { LanguageUtils.applyLanguage(Locale.ENGLISH, true) }
        binding.btnPermission.click {
            PermissionsUtil.getInstance().requestPermissions(this, PermissionsUtil.GROUP_CAMERA,
                    object : PermissionsUtil.OnPermissionsListener {
                        override fun onPermissionGranted(requestCode: Int) {
                            Log.d(TAG, "onPermissionGranted: ")
                        }

                        override fun onPermissionShowRationale(requestCode: Int, permissions: Array<out String>?) {
                            Log.d(TAG, "onPermissionShowRationale: ")

                        }

                        override fun onPermissionDenied(requestCode: Int) {
                            Log.d(TAG, "onPermissionDenied: ")

                        }
                    })
        }
        binding.btnImagePicker.click {
            startActivity(Intent(context, ImagePickerActivity::class.java))

        }
        binding.btnDown.click {
            startActivity(Intent(context, DownloadActivity::class.java))
        }
        binding.btnUpgrade.click {
           // BuglyManager.checkUpgrade(true, false)
            val url = "https://outexp-beta.cdn.qq.com/outbeta/2021/06/18/commirkowumvm_1.0.1_56987f9a-fb39-56d5-9ac4-a4c055633672.apk"

            AppUpgradeDialog.show(childFragmentManager, object : IUpgradeInfo {
                override fun getTitle(): String {
                    return "title"
                }

                override fun getContent(): String {
                    return "getContent"
                }

                override fun getApkUrl(): String {
                    return url;
                }

                override fun getVersionName(): String {
                    return "1.0.1"
                }

                override fun getVersionCode(): Int {
                    return 1001
                }

                override fun isForceUpgrade(): Int {
                    return 0
                }
            })
        }
        binding.btnQr.click {
            QRScanner.getInstance().setScanConfig(ScanConfig()
                    .setShowFlashlight(true)
                    .setShowAlbumPick(true)
                    .setCameraAutoZoom(true)
            )
                    .setOnScanResultListener {
                        ToastUtils.showShort("扫描结果：" + it)
                        LogUtil.e("扫描结果：" + it)
                    }.start(context)
        }
        binding.btnCrash.click {
            throw RuntimeException("测试BUG")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d(TAG, "onCreate: $position")
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        Log.d(TAG, "onCreateView: $position")
        return super.onCreateView(inflater, container, savedInstanceState)

//        _binding = FragmentDatabindingBinding.inflate(inflater, container, false)
//        return binding.root
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
//    override fun onAttach(context: Context) {
//        Log.d(TAG, "onAttach: $position")
//        super.onAttach(context)
//    }
//
//    override fun onAttachFragment(childFragment: Fragment) {
//        Log.d(TAG, "onAttachFragment: $position")
//        super.onAttachFragment(childFragment)
//    }
//
//    override fun onResume() {
//        Log.d(TAG, "onResume: $position")
//        super.onResume()
//    }
//
//    override fun onPause() {
//        Log.d(TAG, "onPause: $position")
//        super.onPause()
//    }
//
//    override fun onStop() {
//        Log.d(TAG, "onStop: $position")
//        super.onStop()
//    }
//
//    override fun onDestroyView() {
//        Log.d(TAG, "onDestroyView: $position")
////        _binding = null
//        super.onDestroyView()
//    }
//
//    override fun onDestroy() {
//        Log.d(TAG, "onDestroy: $position")
//        super.onDestroy()
//    }
//
//    override fun onDetach() {
//        Log.d(TAG, "onDetach: $position")
//        super.onDetach()
//    }
}