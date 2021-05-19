package com.mirkowu.lib_base.event;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import com.mirkowu.lib_base.view.IBaseView;

public class UiChangeEvent {
    private SingleLiveEvent<Boolean> mIsShowLoadingDialogEvent;
    private SingleLiveEvent<Boolean> mIsEmptyViewEvent;
    private SingleLiveEvent<Boolean> mIsDismissViewEvent;
    private SingleLiveEvent<Boolean> mIsReloadViewEvent;
    private SingleLiveEvent<String> mJumpPathEvent;
    private SingleLiveEvent<Bundle> mJumpPathParamsEvent;
    private SingleLiveEvent<StateBean> mStateViewEvent;
    //显示 、隐藏
//    private SingleLiveEvent<ApiException> mErrorEvent;//加载中、网络错误、服务器异常、（点击刷新）、空数据

    public UiChangeEvent() {
    }

    public MutableLiveData<Boolean> getShowLoadingDialogEvent() {
        return this.mIsShowLoadingDialogEvent = this.getLiveData(this.mIsShowLoadingDialogEvent);
    }

    public MutableLiveData<StateBean> getStateViewEvent() {
        return this.mStateViewEvent = this.getLiveData(this.mStateViewEvent);
    }
    public MutableLiveData<Boolean> getEmptyViewEvent() {
        return this.mIsEmptyViewEvent = this.getLiveData(this.mIsEmptyViewEvent);
    }

    public MutableLiveData<Boolean> getDismissViewEvent() {
        return this.mIsDismissViewEvent = this.getLiveData(this.mIsDismissViewEvent);
    }

    public MutableLiveData<Boolean> getReloadViewEvent() {
        return this.mIsReloadViewEvent = this.getLiveData(this.mIsReloadViewEvent);
    }

    public MutableLiveData<String> getJumpPagePathEvent() {
        return this.mJumpPathEvent = this.getLiveData(this.mJumpPathEvent);
    }

    public MutableLiveData<Bundle> getJumpPageParamsEvent() {
        return this.mJumpPathParamsEvent = this.getLiveData(this.mJumpPathParamsEvent);
    }

    private <T> SingleLiveEvent<T> getLiveData(SingleLiveEvent<T> mutableLiveData) {
        if (mutableLiveData == null) {
            mutableLiveData = new SingleLiveEvent<>();
        }

        return mutableLiveData;
    }

    public void unregisterEvent(@NonNull LifecycleOwner owner) {
    }

    public void registerEvent(@NonNull LifecycleOwner owner, final IBaseView iBaseView) {
        getShowLoadingDialogEvent().observe(owner, new Observer<Boolean>() {
            public void onChanged(Boolean aBoolean) {
                if (aBoolean) {
                    iBaseView.showLoadingDialog();
                } else {
                    iBaseView.hideLoadingDialog();
                }
            }
        });


        getStateViewEvent().observe(owner, new Observer<StateBean>() {
            public void onChanged(StateBean stateBean) {

                 //stateBean.state==
                  //  iBaseView.showStateView();


            }
        });
//        getEmptyViewEvent().observe(owner, new Observer<Boolean>() {
//            public void onChanged(Boolean aBoolean) {
//                if (aBoolean) {
//                    iBaseView.showEmptyView();
//                }
//
//            }
//        });
//
//
//        getReloadViewEvent().observe(owner, new Observer<Boolean>() {
//            public void onChanged(Boolean aBoolean) {
//                if (aBoolean) {
//                    iBaseView.showReloadView();
//                }
//
//            }
//        });
//
//        getJumpPagePathEvent().observe(owner, new Observer<String>() {
//            public void onChanged(String path) {
//                iBaseView.jumpPage(path);
//            }
//        });
//
//
//        getJumpPageParamsEvent().observe(owner, new Observer<Bundle>() {
//            public void onChanged(Bundle paramsMap) {
//                if (paramsMap.containsKey("JUMP_PAGE_PATH_KEY")) {
//                    String path = paramsMap.getString("JUMP_PAGE_PATH_KEY");
//                    if (!TextUtils.isEmpty(path)) {
//                        paramsMap.remove("JUMP_PAGE_PATH_KEY");
//                        iBaseView.jumpPage(path, paramsMap);
//                    }
//                }
//
//            }
//        });
    }
}
