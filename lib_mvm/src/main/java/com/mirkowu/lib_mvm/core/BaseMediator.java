package com.mirkowu.lib_mvm.core;

import androidx.annotation.NonNull;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ViewModel;


public class BaseMediator<V extends IBaseView, M extends IBaseModel> extends ViewModel implements IMediator<V> {
    public static final String TAG = BaseMediator.class.getSimpleName();
    @NonNull
    protected M model;
    //    protected WeakReference<IBaseView> view;
    @NonNull
    protected V view;
//    public SingleLiveEvent<ApiException> mCommonError = new SingleLiveEvent<>();//通用错误，一般Toast
//    public SingleLiveEvent<ApiException> mStateEvent = new SingleLiveEvent<>();//状态界面

    public BaseMediator() {
        createModel();
    }

    protected void createModel() {
        model = InstanceFactory.newModel(getClass());
    }


    @Override
    public void attachView(V baseView) {
//        view=new WeakReference<>(baseView);
        view = baseView;
    }

    @Override
    public void detachView() {
//        view.clear();
        view = null;
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
        detachView();
    }

    @Override
    public void registerEventBus() {

    }

    @Override
    public void unregisterEventBus() {

    }
}
