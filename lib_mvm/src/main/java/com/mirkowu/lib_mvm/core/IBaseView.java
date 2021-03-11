package com.mirkowu.lib_mvm.core;

import android.app.Activity;
import android.content.Context;

//import com.yoozoo.lib_base.autosize.IAutoAdapt;

public interface IBaseView extends ILifecycleBinder /*, IAutoAdapt*/ {
    Context getContext();

    Activity getActivity();

    void showLoading(String msg);

    void hideLoading();

}
