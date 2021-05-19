package com.mirkowu.lib_base.view;

import android.app.Activity;
import android.content.Context;

import com.mirkowu.lib_base.event.StateBean;

//import com.yoozoo.lib_base.autosize.IAutoAdapt;

public interface IBaseView extends ILifecycleBinder /*, IAutoAdapt*/ {
    Context getContext();

    Activity getActivity();

    void showLoadingDialog();
    void showLoadingDialog(final String msg);
    void hideLoadingDialog();

    void showStateView(StateBean bean);
    void hideStateView();

    void onFailure();

}
