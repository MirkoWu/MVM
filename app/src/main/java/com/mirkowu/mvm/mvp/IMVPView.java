package com.mirkowu.mvm.mvp;

import com.mirkowu.lib_core.view.IBaseView;

public interface IMVPView extends IBaseView {

      void onLoadDataSuccess(Object o);
      void onLoadDataError(Throwable t);
}
