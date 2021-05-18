package com.mirkowu.lib_core.event;

import android.os.Bundle;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import com.mirkowu.lib_core.view.IBaseView;

public class UiChangeEvent {
    private SingleLiveEvent<Boolean> mIsShowLoadingEvent;
    private SingleLiveEvent<Boolean> mIsEmptyViewEvent;
    private SingleLiveEvent<Boolean> mIsDismissViewEvent;
    private SingleLiveEvent<Boolean> mIsReloadViewEvent;
    private SingleLiveEvent<String> mJumpPathEvent;
    private SingleLiveEvent<Bundle> mJumpPathParamsEvent;

    public UiChangeEvent() {
    }

    public MutableLiveData<Boolean> getShowLoadingEvent() {
        return this.mIsShowLoadingEvent = this.getLiveData(this.mIsShowLoadingEvent);
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
        getShowLoadingEvent().observe(owner, new Observer<Boolean>() {
            public void onChanged(Boolean aBoolean) {
                if (aBoolean) {
                    iBaseView.showLoading();
                } else {
                    iBaseView.hideLoading();
                }
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
