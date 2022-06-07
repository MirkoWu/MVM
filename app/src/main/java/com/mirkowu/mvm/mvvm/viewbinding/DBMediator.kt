package com.mirkowu.mvm.mvvm.viewbinding

import com.mirkowu.lib_base.mediator.BaseMediator
import com.mirkowu.lib_base.util.RxLife
import com.mirkowu.lib_base.view.IBaseView
import com.mirkowu.lib_network.ErrorType
import com.mirkowu.lib_network.state.ResponseData
import com.mirkowu.lib_network.state.ResponseLiveData
import com.mirkowu.lib_util.LogUtil
import com.mirkowu.mvm.BizModel
import com.mirkowu.mvm.bean.GankBaseBean
import com.mirkowu.mvm.bean.GankImageBean
import com.mirkowu.mvm.network.RxObserver

class DBMediator : BaseMediator<IBaseView?, BizModel?>() {
    var mImageListData = ResponseLiveData<List<GankImageBean?>?>()

    fun loadImage(page: Int, pageSize: Int) {
        mModel.loadImage(page, pageSize)
                .doOnDispose { LogUtil.d("RxJava 被解绑") }
                .to(RxLife.bindLifecycle(mView))
                .subscribe(object : RxObserver<GankBaseBean<List<GankImageBean?>?>?>() {
                    override fun onStart() {
                        super.onStart()
                        showLoadingDialog()
                    }

                    override fun onFinish() {
                        super.onFinish()
                        hideLoadingDialog()
                    }

                    override fun onSuccess(data: GankBaseBean<List<GankImageBean?>?>?) {
                        data?.let {
                            if (it.isSuccess && !it.data.isNullOrEmpty()) {
                                mImageListData.value = ResponseData.success(it.data!!)
                            }
                        }
                    }

                    override fun onFailure(errorType: ErrorType, code: Int, msg: String) {
                        mImageListData.value = ResponseData.error(errorType, code, msg)
                    }
                })
    }


}