package com.mirkowu.mvm.recycelerview;

import com.mirkowu.lib_widget.adapter.IMultiType;

public class SecondBean implements IMultiType {
    @Override
    public int getItemViewType() {
        return LinearAdapter.TYPE_SECOND;
    }
}
