package com.mirkowu.mvm.ui.recycelerview;

import com.mirkowu.lib_widget.adapter.IMultiType;

public class ThirdBean implements IMultiType {
    @Override
    public int getItemViewType() {
        return LinearAdapter.TYPE_THIRD;
    }
}
