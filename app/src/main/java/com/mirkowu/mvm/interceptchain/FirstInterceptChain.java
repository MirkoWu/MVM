package com.mirkowu.mvm.interceptchain;

import androidx.fragment.app.FragmentActivity;

import com.mirkowu.lib_util.pattern.InterceptChain;
import com.mirkowu.lib_widget.dialog.PromptDialog;

public class FirstInterceptChain extends InterceptChain<String> {

    private FragmentActivity mContext;

    public FirstInterceptChain(FragmentActivity context) {
        mContext = context;
    }

    @Override
    public void intercept(String data) {
        new PromptDialog()
                .setTitle("第一个")
                .setContent(data)
                .setOnButtonClickListener(new PromptDialog.OnButtonClickListener() {
                    @Override
                    public void onButtonClick(PromptDialog dialog, boolean isPositiveClick) {
                        if (isPositiveClick) {
                            next(data + "--第一个 --");
                        } else {

                        }
                    }
                })
                .show(mContext);
    }
}
