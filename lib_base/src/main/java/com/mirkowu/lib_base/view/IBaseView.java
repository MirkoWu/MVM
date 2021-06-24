package com.mirkowu.lib_base.view;

import android.app.Activity;
import android.content.Context;

public interface IBaseView extends ILifecycleBinder {
    Context getContext();

    Activity getActivity();

    void showLoadingDialog();

    void showLoadingDialog(final String msg);

    void hideLoadingDialog();

//    void showStateView(StateBean bean);
//    void hideStateView();
//    void onFailure();

}
