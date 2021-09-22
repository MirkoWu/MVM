package com.mirkowu.mvm.interceptchain;

import androidx.fragment.app.FragmentActivity;

import com.mirkowu.lib_util.pattern.InterceptChain;
import com.mirkowu.lib_widget.dialog.PromptDialog;

public class ThirdInterceptChain extends InterceptChain<String> {

    private FragmentActivity mContext;

    public ThirdInterceptChain(FragmentActivity context) {
        mContext = context;
    }

    @Override
    public void intercept(String data) {
        new PromptDialog()
                .setTitle("第三个")
                .setContent(data)
                .setOnButtonClickListener(new PromptDialog.OnButtonClickListener() {
                    @Override
                    public void onButtonClick(PromptDialog dialog, boolean isPositiveClick) {
                        if (isPositiveClick) {
                            next(data + "--第三个 --");
                        } else {

                        }
                    }
                })
                .show(mContext);
    }
}
