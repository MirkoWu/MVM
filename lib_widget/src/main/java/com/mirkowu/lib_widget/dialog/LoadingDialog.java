package com.mirkowu.lib_widget.dialog;

import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.annotation.StringRes;

import com.mirkowu.lib_widget.R;


/**
 * 加载弹窗
 */
public class LoadingDialog extends BaseDialog {
    private String mMessage;

    @Override
    protected int getLayoutResId() {
        return R.layout.widget_dialog_loading;
    }

    @Override
    protected void convertView(ViewHolder viewHolder, BaseDialog baseDialog) {
        TextView mLoadingTextView = viewHolder.getView(R.id.mLoadingTextView);
        if (!TextUtils.isEmpty(mMessage)) {
            mLoadingTextView.setText(mMessage);
            mLoadingTextView.setVisibility(View.VISIBLE);
        } else {
            mLoadingTextView.setVisibility(View.GONE);
        }
        setTouchOutCancel(false);
        setDialogCancelable(true);
        setDimAmount(0f);
    }

    @Override
    public void onStart() {
        super.onStart();
        //宽度自适应
        Window window = getDialog().getWindow();
        if (window != null) {
            WindowManager.LayoutParams params = window.getAttributes();
            params.width = WindowManager.LayoutParams.WRAP_CONTENT;
            window.setAttributes(params);
        }
    }

    public LoadingDialog setMessage(String message) {
        this.mMessage = message;
        return this;
    }

    public LoadingDialog setMessage(@StringRes int resId) {
        this.mMessage = getContext().getString(resId);
        return this;
    }

}
