package com.mirkowu.mvm.mvp;

import com.mirkowu.lib_mvm.core.IBaseView;

public interface IMVPView extends IBaseView {

      void onLoadDataSuccess(Object o);
      void onLoadDataError(Throwable t);
}
