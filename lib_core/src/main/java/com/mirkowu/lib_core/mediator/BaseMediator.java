package com.mirkowu.lib_core.mediator;

import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ViewModel;

import com.mirkowu.lib_core.event.UiChangeEvent;
import com.mirkowu.lib_core.view.IBaseView;
import com.mirkowu.lib_core.util.InstanceFactory;
import com.mirkowu.lib_core.model.IBaseModel;


public class BaseMediator<V extends IBaseView, M extends IBaseModel> extends ViewModel implements IMediator<V> {
    public static final String TAG = BaseMediator.class.getSimpleName();
    @NonNull
    protected M mModel;
    //    protected WeakReference<IBaseView> view;
    @NonNull
    protected V mView;
    //    public SingleLiveEvent<ApiException> mCommonError = new SingleLiveEvent<>();//通用错误，一般Toast
//    public SingleLiveEvent<ApiException> mStateEvent = new SingleLiveEvent<>();//状态界面
    private UiChangeEvent mUiStatusChangeLiveData;

    public BaseMediator() {
        createModel();
    }

    protected void createModel() {
        mModel = InstanceFactory.newModel(getClass());
    }


    @Override
    public void attachView(V baseView) {
//        view=new WeakReference<>(baseView);
        mView = baseView;
        getUiEventChangeLiveData().registerEvent(mView.getLifecycleOwner(), mView);
    }

    @Override
    public void detachView() {
//        view.clear();
        mView = null;
    }

//    @Override
//    public IBaseView getView() {
//        return view.get();
//    }


    //    protected void sendError(int code, String msg) {
//        sendError(false, code, msg);
//    }
//
//    protected void sendError(boolean showState, int code, String msg) {
//        ApiException api = new ApiException(code, msg);
//        if (showState) {
//            mStateEvent.setValue(api);
//        } else {
//            mCommonError.setValue(api);
//        }
//    }
    public UiChangeEvent getUiEventChangeLiveData() {
        if (this.mUiStatusChangeLiveData == null) {
            this.mUiStatusChangeLiveData = new UiChangeEvent();
        }

        return this.mUiStatusChangeLiveData;
    }

    public void showLoading() {
        this.mUiStatusChangeLiveData.getShowLoadingEvent().setValue(true);
    }

    public void hideLoading() {
        this.mUiStatusChangeLiveData.getShowLoadingEvent().setValue(false);
    }

    public void jumpPage(@NonNull String path) {
        if (!TextUtils.isEmpty(path)) {
            this.mUiStatusChangeLiveData.getJumpPagePathEvent().setValue(path);
        }

    }

    @Override
    public void onAny(LifecycleOwner owner, Lifecycle.Event event) {
//        LogUtils.d(TAG, "LifecycleOwner = " + owner + " event " + event.name());
    }

    @Override
    public void onCreate() {

    }

    @Override
    public void onStart() {

    }

    @Override
    public void onResume() {

    }

    @Override
    public void onPause() {

    }

    @Override
    public void onStop() {

    }

    @Override
    public void onDestroy() {

    }

    @Override
    public void registerEventBus() {

    }

    @Override
    public void unregisterEventBus() {

    }


}
