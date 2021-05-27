package com.mirkowu.lib_widget.dialog;

import android.app.Dialog;
import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.annotation.StringRes;

import com.mirkowu.lib_widget.R;


/**
 * @author: mirko
 * @date: 19-9-27
 */
public class LoadingDialog extends Dialog {
    private TextView mLoadingTextView;
    private View mView;

    public LoadingDialog(Context context) {
        this(context, context.getString(R.string.widget_loading));
    }

    public LoadingDialog(Context context, CharSequence msg) {
        super(context, R.style.LoadingDialogStyle);

        mView = View.inflate(context, R.layout.widget_dialog_loading, null);
        mLoadingTextView = mView.findViewById(R.id.mLoadingTextView);
        if (!TextUtils.isEmpty(msg)) {
            mLoadingTextView.setText(msg);
            mLoadingTextView.setVisibility(View.VISIBLE);
        } else {
            mLoadingTextView.setVisibility(View.GONE);
        }
        this.setCanceledOnTouchOutside(false);
        this.setCancelable(true);
    }

    @Override
    public void show() {
        try {
            if (this.isShowing()) {
                this.dismiss();
            } else {
                super.show();
            }
            //setContentView（）一定要在show之后调用
            this.setContentView(mView);
        } catch (WindowManager.BadTokenException exception) {
        }
    }

    public void setMessage(String message) {
        if (!TextUtils.isEmpty(message)) {
            if (mLoadingTextView.getVisibility() != View.VISIBLE) {
                mLoadingTextView.setVisibility(View.VISIBLE);
            }
            mLoadingTextView.setText(message);
        }
    }

    public void setMessage(@StringRes int message) {
        if (mLoadingTextView.getVisibility() != View.VISIBLE) {
            mLoadingTextView.setVisibility(View.VISIBLE);
        }
        mLoadingTextView.setText(message);
    }

}
