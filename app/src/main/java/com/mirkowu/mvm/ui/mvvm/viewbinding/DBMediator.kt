package com.mirkowu.mvm.ui.mvvm.viewbinding

import com.mirkowu.lib_base.mediator.BaseMediator
import com.mirkowu.lib_base.util.RxLife
import com.mirkowu.lib_base.view.IBaseView
import com.mirkowu.lib_network.request.ErrorData
import com.mirkowu.lib_network.request.RequestData
import com.mirkowu.lib_network.request.RequestLiveData
import com.mirkowu.lib_util.LogUtil
import com.mirkowu.mvm.BizModel
import com.mirkowu.mvm.bean.GankBaseBean
import com.mirkowu.mvm.bean.ImageBean
import com.mirkowu.mvm.bean.ImageListBean
import com.mirkowu.mvm.network.RxObserver

class DBMediator : BaseMediator<IBaseView?, BizModel?>() {
    var mImageListData = RequestLiveData<List<ImageBean?>?>()

    fun loadImage(page: Int, pageSize: Int) {
        mModel.loadImage(page, pageSize)
            .doOnDispose { LogUtil.d("RxJava 被解绑") }
            .to(RxLife.bindLifecycle(mView))
            .subscribe(object : RxObserver<GankBaseBean<ImageListBean>?>() {
                override fun onStart() {
                    super.onStart()
                    showLoadingDialog()
                }

                override fun onFinish() {
                    super.onFinish()
                    hideLoadingDialog()
                }

                override fun onSuccess(data: GankBaseBean<ImageListBean>?) {
                    data?.let {
                        if (it.isSuccess && !it.data.list.isNullOrEmpty()) {
                            mImageListData.value = RequestData.success(it.data!!.list)
                        }
                    }
                }

                override fun onFailure(error: ErrorData) {
                    mImageListData.value = RequestData.failure(error)
                }
            })
    }


}