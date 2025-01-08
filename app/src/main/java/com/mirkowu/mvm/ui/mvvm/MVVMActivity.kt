package com.mirkowu.mvm.ui.mvvm

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.view.View
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.mirkowu.lib_base.util.bindingView
import com.mirkowu.lib_base.widget.RefreshHelper
import com.mirkowu.lib_network.request.flow.asRequestLiveData
import com.mirkowu.lib_network.request.flow.request
import com.mirkowu.lib_network.request.request
import com.mirkowu.lib_util.ColorFilterUtils
import com.mirkowu.lib_util.LogUtil
import com.mirkowu.lib_widget.adapter.BaseRVAdapter
import com.mirkowu.lib_widget.stateview.LoadingDot
import com.mirkowu.mvm.base.BaseActivity
import com.mirkowu.mvm.bean.ImageBean
import com.mirkowu.mvm.databinding.ActivityMVVMBinding
import com.mirkowu.mvm.network.GankClient
import com.mirkowu.mvm.network.ImageApi
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.util.*


class MVVMActivity : BaseActivity<MVVMMediator?>(), RefreshHelper.OnRefreshListener {
    val binding by bindingView(ActivityMVVMBinding::inflate)
    private lateinit var refreshHelper: RefreshHelper
    private lateinit var imageAdapter: ImageAdapter

    override fun bindContentView() {
        setContentView(binding.root)
    }

    var isGrayMode = false
    override fun initialize() {
        refreshHelper = RefreshHelper(binding.mRefresh, binding.mRecyclerView, this)
        imageAdapter = ImageAdapter()
        binding.mRecyclerView.adapter = imageAdapter
        binding.mRecyclerView.layoutManager = LinearLayoutManager(this)
        imageAdapter.onItemClickListener =
            BaseRVAdapter.OnItemClickListener { view: View?, item: ImageBean?, position: Int ->
                LogUtil.i(
                    "TAG",
                    "onItemClick: $position"
                )
            }
        imageAdapter.setOnItemChildClickListener { view: View?, item: ImageBean?, position: Int ->
            LogUtil.i(
                "TAG",
                "onItemChildClick: $position"
            )
            isGrayMode = !isGrayMode
            if (isGrayMode) {
                ColorFilterUtils.setGrayFilter(activity)
            } else {
                ColorFilterUtils.removeFilter(activity)
            }
        }
        imageAdapter.setOnItemChildLongClickListener { view, item, position ->
            LogUtil.i("TAG", "onItemChildLongClick: $position")
            false
        }

//        LiveDataUtilKt.observerRequest(mMediator.mRequestImageListData, this,
//                () -> null, () -> null,
//                gankImageBeans -> {
//                    refreshHelper.setLoadMore(imageAdapter, gankImageBeans);
//                    return null;
//                }, errorBean -> {
//                    if (errorBean.isNetError()) {
//                        binding.mStateView.setShowState(R.drawable.widget_svg_disconnect, errorBean.msg(), true);
//                    } else if (errorBean.isApiError()) {
//                        Toast.makeText(MVVMActivity.this, errorBean.code() + ":" + errorBean.msg(), Toast.LENGTH_SHORT).show();
//                    } else {
//                        Toast.makeText(MVVMActivity.this, errorBean.code() + ":" + errorBean.msg(), Toast.LENGTH_SHORT).show();
//                    }
//                    return null;
//                });

        mMediator.imageBean.request(this) {
            loading { LogUtil.d("onLoading") }
            finish { LogUtil.d("onFinish") }
            success { LogUtil.d("onSuccess") }
            fail { LogUtil.d("onFailure $it") }
        }
        //todo 方式1：使用封装好的 observerRequest 方法，方便快捷
        mMediator.mRequestImageListData.request(this) {
            finish {
                hideLoadingDialog()
            }
            success {
                binding.mStateView.setGoneState()
                refreshHelper.setLoadMore(imageAdapter, it)
            }
            fail {
                binding.mStateView.setErrorState(it.msg)
                refreshHelper.finishLoad()
                val errorBean = it
                if (errorBean.isNetError && refreshHelper.isFirstPage) {
                    //  binding.mStateView.setShowState(R.drawable.widget_svg_disconnect, errorBean.msg(), true);
                } else if (errorBean.isApiError) {
                    Toast.makeText(
                        this@MVVMActivity,
                        errorBean.code.toString() + ":" + errorBean.msg,
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    Toast.makeText(
                        this@MVVMActivity,
                        errorBean.code.toString() + ":" + errorBean.msg,
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
        //todo 方式2：使用原始的 observe 方法
        mMediator.mRequestImageListData.observe(this) { responseData ->
            if (responseData.isSuccess) {
                refreshHelper.setLoadMore(imageAdapter, responseData.data)
            } else if (responseData.isFailure) {
                refreshHelper.finishLoad()
                val errorBean = responseData.error
                if (errorBean != null) {
                    if (errorBean.isNetError && refreshHelper.isFirstPage) {
                        //  binding.mStateView.setShowState(R.drawable.widget_svg_disconnect, errorBean.msg(), true);
                    } else if (errorBean.isApiError) {
                        Toast.makeText(
                            this@MVVMActivity,
                            errorBean.code.toString() + ":" + errorBean.msg,
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        Toast.makeText(
                            this@MVVMActivity,
                            errorBean.code.toString() + ":" + errorBean.msg,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }
        mMediator.mImageError.observe(this) { errorBean ->
            hideLoadingDialog()
            refreshHelper.finishLoad()
            binding.mStateView.setErrorState(errorBean.msg)
        }
        mMediator.mImageData.observe(this) { data ->
            hideLoadingDialog()
            refreshHelper.finishLoad()
            if (data.isSuccess) {
                if (data.data != null && !data!!.data!!.isEmpty()) {
                    val imgUrl = data.data!!.get(0)!!.imgurl
                    val bean = ImageBean()
                    bean.url = imgUrl
                    val list: MutableList<ImageBean?> = ArrayList()
                    list.add(bean)
                    refreshHelper.setLoadMore(imageAdapter, list)
                }
            } else if (data.isFailure) {
                binding.mStateView.setErrorState(data.error?.msg)
            }
        }
//        binding.mStateView.setLoadingState()
//        binding.mStateView.setLoadingState(ThreeBounce(),"拼命加载中...")
        //        binding.mStateView.setLoadingState("拼命加载中...");
//        binding.mStateView.setLoadingState(getString(R.string.widget_loading));
//        binding.mStateView.setEmptyState(getString(R.string.widget_loading));
//        binding.mStateView.setErrorState("家再说吧");
        //binding.stateview.setLoadingState(R.mipmap.ic_launcher, getString(R.string.widget_loading));
//        binding.mStateView.setOnClickListener { binding.mStateView.setErrorState("加载失败") }
        binding.mStateView.setOnRefreshListener {
            val drawable = LoadingDot()
//            val drawable = Circle()
//            val drawable = DoubleBounce()
            drawable.setDrawBounds(0, 0, 540, 540)
            drawable.color = Color.RED
//            binding.mStateView.setLoadingState(drawable, "加载中")
//            binding.mStateView.setLoadingState(R.drawable.anim_loading, "加载中")
//            binding.mStateView.setLoadingState(R.drawable.widget_svg_loading, "加载中")


            refreshHelper.refresh()
        }
        refreshHelper.pageCount = 1
        refreshHelper.refresh()
    }

    override fun onLoadData(page: Int) {
        binding.mStateView.setLoadingState()
        // showLoadingDialog();
        mMediator.loadImage(page, refreshHelper.pageCount)
        lifecycleScope.launch {
            GankClient.getInstance().getService<ImageApi>(ImageApi::class.java)
                .loadImage("", 1, 1)
                .map {
//                if(it.isSuccess){
                    it.data
//                } else {
//                    null
//                }

                }
                .request {
                    success { it!!.list }
                    fail { }
                }
        }
        GankClient.getInstance()
            .getService<ImageApi>(ImageApi::class.java)
            .loadImage("", 1, 1)
            .map {
//                if(it.isSuccess){
                it.data
//                } else {
//                    null
//                }

            }
            .asRequestLiveData()
            .request(this@MVVMActivity) {
                success { it!!.list }
                fail { }
            }

        mMediator.loadImageAsLiveData(page, refreshHelper.pageCount)
            .request(this) {
                success { it }
            }
        mMediator.getPing2LiveData().request(this) {
            success { }
            fail { }
        }
    }

    override fun onLoadNoMore() {}
    override fun onEmptyChange(isEmpty: Boolean) {
        binding.mStateView.apply {
            if (isEmpty) setEmptyState("暂无数据") else setGoneState()
        }
    }

    companion object {
        fun start(context: Context) {
            val starter = Intent(context, MVVMActivity::class.java)
            //    starter.putExtra();
            context.startActivity(starter)
        }
    }
}