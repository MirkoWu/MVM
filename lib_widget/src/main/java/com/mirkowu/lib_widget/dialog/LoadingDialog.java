package com.mirkowu.lib_widget.dialog;

import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.annotation.StringRes;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;

import com.mirkowu.lib_util.utilcode.util.StringUtils;
import com.mirkowu.lib_widget.R;


/**
 * 加载弹窗
 */
public class LoadingDialog extends BaseDialog {
    protected CharSequence mMessage;

    public LoadingDialog() {
        setDimAmount(0f);
        setTouchOutCancel(false);
        setDialogCancelable(true);
    }

    public LoadingDialog(CharSequence message) {
        this();
        setMessage(message);
    }

    public LoadingDialog(@StringRes int resId) {
        this();
        setMessage(resId);
    }

    @Override
    public int getLayoutResId() {
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

    public LoadingDialog setMessage(CharSequence message) {
        this.mMessage = message;
        return this;
    }

    public LoadingDialog setMessage(@StringRes int resId) {
        return setMessage(StringUtils.getString(resId));
    }

    @Override
    public LoadingDialog show(FragmentActivity activity) {
        return (LoadingDialog) super.show(activity);
    }

    @Override
    public LoadingDialog show(FragmentManager manager) {
        return (LoadingDialog) super.show(manager);
    }
}
