package com.mirkowu.mvm.mvvm

import android.content.Context
import android.content.Intent
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.mirkowu.lib_base.util.bindingView
import com.mirkowu.lib_base.widget.RefreshHelper
import com.mirkowu.lib_network.state.observerRequest
import com.mirkowu.lib_util.LogUtil
import com.mirkowu.lib_widget.adapter.BaseRVAdapter
import com.mirkowu.mvm.R
import com.mirkowu.mvm.base.BaseActivity
import com.mirkowu.mvm.bean.GankImageBean
import com.mirkowu.mvm.databinding.ActivityMVVMBinding
import com.mirkowu.mvm.mvvm.MVVMActivity
import java.util.*

class MVVMActivity : BaseActivity<MVVMMediator?>(), RefreshHelper.OnRefreshListener {
    val binding by bindingView(ActivityMVVMBinding::inflate)
    private lateinit var refreshHelper: RefreshHelper
    private lateinit var imageAdapter: ImageAdapter
    override fun getLayoutId(): Int {
        return R.layout.activity_m_v_v_m
    }

    override fun bindContentView() {
        setContentView(binding.root)
    }

    override fun initialize() {
        refreshHelper = RefreshHelper(binding.mRefresh, binding.mRecyclerView, this)
        imageAdapter = ImageAdapter()
        binding.mRecyclerView.adapter = imageAdapter
        binding.mRecyclerView.layoutManager = LinearLayoutManager(this)
        imageAdapter.onItemClickListener =
            BaseRVAdapter.OnItemClickListener { view: View?, item: GankImageBean?, position: Int ->
                LogUtil.i(
                    "TAG",
                    "onItemClick: $position"
                )
            }
        imageAdapter.setOnItemChildClickListener { view: View?, item: GankImageBean?, position: Int ->
            LogUtil.i(
                "TAG",
                "onItemChildClick: $position"
            )
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

        //todo 方式1：使用封装好的 observerRequest 方法，方便快捷
        mMediator.mRequestImageListData.observerRequest(
            this,
            onFinish = {
                hideLoadingDialog()
            },
            onSuccess = {
                refreshHelper.setLoadMore(imageAdapter, it )
            },
            onFailure = {
                refreshHelper.finishLoad()
                val errorBean = it
                if (errorBean.isNetError && refreshHelper.isFirstPage) {
                    //  binding.mStateView.setShowState(R.drawable.widget_svg_disconnect, errorBean.msg(), true);
                } else if (errorBean.isApiError) {
                    Toast.makeText(
                        this@MVVMActivity,
                        errorBean.code().toString() + ":" + errorBean.msg(),
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    Toast.makeText(
                        this@MVVMActivity,
                        errorBean.code().toString() + ":" + errorBean.msg(),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            },
        )
        //todo 方式2：使用原始的 observe 方法，方便快捷
        mMediator.mRequestImageListData.observe(this, { responseData ->
            if (responseData.isSuccess) {
                refreshHelper.setLoadMore(imageAdapter, responseData.data)
            } else if (responseData.isFailure) {
                refreshHelper.finishLoad()
                val errorBean = responseData.error
                if (errorBean.isNetError && refreshHelper.isFirstPage) {
                    //  binding.mStateView.setShowState(R.drawable.widget_svg_disconnect, errorBean.msg(), true);
                } else if (errorBean.isApiError) {
                    Toast.makeText(
                        this@MVVMActivity,
                        errorBean.code().toString() + ":" + errorBean.msg(),
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    Toast.makeText(
                        this@MVVMActivity,
                        errorBean.code().toString() + ":" + errorBean.msg(),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        })
        mMediator.mImageError.observe(this, { errorBean ->
            hideLoadingDialog()
            refreshHelper.finishLoad()
            binding.mStateView.setErrorState(errorBean.msg())
        })
        mMediator.mImageData.observe(this, { data ->
            hideLoadingDialog()
            refreshHelper.finishLoad()
            if (data.isSuccess) {
                if (data.data != null && !data.data.isEmpty()) {
                    val imgUrl = data.data[0].imgurl
                    val bean = GankImageBean()
                    bean.url = imgUrl
                    val list: MutableList<GankImageBean?> = ArrayList()
                    list.add(bean)
                    refreshHelper.setLoadMore(imageAdapter, list)
                }
            } else {
                binding.mStateView.setErrorState(data.error.msg())
            }
        })
        binding.mStateView.setLoadingState()
        //        binding.mStateView.setLoadingState("拼命加载中...");
//        binding.mStateView.setLoadingState(getString(R.string.widget_loading));
//        binding.mStateView.setEmptyState(getString(R.string.widget_loading));
//        binding.mStateView.setErrorState("家再说吧");
        //binding.stateview.setLoadingState(R.mipmap.ic_launcher, getString(R.string.widget_loading));
        binding.mStateView.setOnClickListener { binding.mStateView.setErrorState("加载失败") }
        binding.mStateView.setOnRefreshListener {
            binding.mStateView.setLoadingState(R.drawable.anim_loading, "sss")
            refreshHelper.autoRefresh()
        }
        refreshHelper.pageCount = 1
        refreshHelper.refresh()
    }

    override fun onLoadData(page: Int) {
        // showLoadingDialog();
        mMediator.loadImage(page, refreshHelper.pageCount)
    }

    override fun onLoadNoMore() {}
    override fun onEmptyChange(isEmpty: Boolean) {
        if (isEmpty) {
            binding.mStateView.setShowState(R.mipmap.ic_launcher, "暂无数据")
        } else {
            binding.mStateView.setGoneState()
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