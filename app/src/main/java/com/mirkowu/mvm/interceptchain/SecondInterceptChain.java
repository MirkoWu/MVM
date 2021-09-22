package com.mirkowu.mvm.interceptchain;

import androidx.fragment.app.FragmentActivity;

import com.mirkowu.lib_util.pattern.InterceptChain;
import com.mirkowu.lib_widget.dialog.PromptDialog;
import com.mirkowu.mvm.R;

public class SecondInterceptChain extends InterceptChain<String> {
    private FragmentActivity mContext;

    public SecondInterceptChain(FragmentActivity context) {
        mContext = context;
    }

    @Override
    public void intercept(String data) {
        new PromptDialog()
                .setTitle("第二个")
                .setIcon(R.mipmap.ic_launcher)
                .setContent(data)
                .setOnButtonClickListener(new PromptDialog.OnButtonClickListener() {
                    @Override
                    public void onButtonClick(PromptDialog dialog, boolean isPositiveClick) {
                        if (isPositiveClick) {
                            next(data + "--第二个 --");
                        } else {

                        }
                    }
                })
                .show(mContext);
    }
}
