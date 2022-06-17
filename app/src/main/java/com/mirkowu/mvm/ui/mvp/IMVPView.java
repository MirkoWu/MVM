package com.mirkowu.mvm.ui.mvp;

import com.mirkowu.lib_base.view.IBaseView;

public interface IMVPView extends IBaseView {

    void onLoadDataSuccess(Object o);

    void onLoadDataError(Throwable t);
}
