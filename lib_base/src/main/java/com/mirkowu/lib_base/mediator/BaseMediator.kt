package com.mirkowu.lib_base.mediator

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import com.mirkowu.lib_base.model.IBaseModel
import com.mirkowu.lib_base.util.InstanceFactory
import com.mirkowu.lib_base.view.IBaseView
import com.mirkowu.lib_network.request.flow.event
import com.mirkowu.lib_util.utilcode.util.ToastUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow

open class BaseMediator<V : IBaseView, M : IBaseModel> : ViewModel(), IMediator<V> {
    @JvmField
    protected val mModel: M = initModel()

    @JvmField
    protected var mView: V? = null

    override fun onCleared() {
        super.onCleared()
    }

    protected fun initModel(): M {
        return InstanceFactory.newModel(javaClass)
    }

    override fun attachView(baseView: V) {
        mView = baseView
        //绑定view时也注册事件
        //  getUiEventChangeLiveData().registerEvent(mView.getLifecycleOwner(), mView);
    }

    override fun detachView() {
        mView = null
    }

    fun showLoadingDialog() {
        mView?.showLoadingDialog()
    }

    fun showLoadingDialog(msg: String?) {
        mView?.showLoadingDialog(msg)
    }

    fun hideLoadingDialog() {
        mView?.hideLoadingDialog()
    }

    fun <T> Flow<T>.autoLoading(toastOnFail: Boolean = false): Flow<T> {
        return event(Dispatchers.Main) {
            loading { showLoadingDialog() }
            fail { if (toastOnFail) ToastUtils.showShort(it.displayMsg) }
            finish { hideLoadingDialog() }
        }
    }

    //    private UiChangeEvent mUiStatusChangeLiveData;
    //
    //    public UiChangeEvent getUiEventChangeLiveData() {
    //        if (this.mUiStatusChangeLiveData == null) {
    //            this.mUiStatusChangeLiveData = new UiChangeEvent();
    //        }
    //
    //        return this.mUiStatusChangeLiveData;
    //    }
    //    public void showLoadingDialog() {
    //        this.mUiStatusChangeLiveData.getShowLoadingDialogEvent().setValue(true);
    //    }
    //
    //    public void hideLoadingDialog() {
    //        this.mUiStatusChangeLiveData.getShowLoadingDialogEvent().setValue(false);
    //    }
    //    public void jumpPage(@NonNull String path) {
    //        if (!TextUtils.isEmpty(path)) {
    //            this.mUiStatusChangeLiveData.getJumpPagePathEvent().setValue(path);
    //        }
    //
    //    }
    override fun onAny(owner: LifecycleOwner, event: Lifecycle.Event) {
//        LogUtils.d(TAG, "LifecycleOwner = " + owner + " event " + event.name());
    }

    override fun onCreate() {}
    override fun onStart() {}
    override fun onResume() {}
    override fun onPause() {}
    override fun onStop() {}
    override fun onDestroy() {}
    override fun registerEventBus() {}
    override fun unregisterEventBus() {}

    companion object {
        val TAG = BaseMediator::class.java.simpleName
    }
}