package com.mirkowu.mvm.recycelerview;

import com.mirkowu.lib_widget.adapter.IMultiType;

public class ThirdBean implements IMultiType {
    @Override
    public int getItemViewType() {
        return GridAdapter.TYPE_THIRD;
    }
}
