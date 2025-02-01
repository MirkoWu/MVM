package com.mirkowu.lib_widget.dialog;

import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.ColorRes;
import androidx.annotation.DrawableRes;
import androidx.annotation.StringRes;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;

import com.mirkowu.lib_util.utilcode.util.StringUtils;
import com.mirkowu.lib_widget.R;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

public class PromptDialog extends BaseDialog implements View.OnClickListener {
    private static final int DEFAULT_WIDTH = 280; //默认宽度 dp
    private int mIconResId;
    private CharSequence mTitle;
    private CharSequence mContent;
    private CharSequence mPositiveText;
    private CharSequence mNegativeText;
    private int mPositiveTextColorResId;
    private int mNegativeTextColorResId;
    protected OnButtonClickListener mOnButtonClickListener;
//    protected OnClickListener mPositiveClickListener;
//    protected OnClickListener mNegativeClickListener;

    public PromptDialog() {
        setWidth(DEFAULT_WIDTH);
    }

    @Override
    public int getLayoutId() {
        return R.layout.widget_dialog_prompt;
    }

    @Override
    protected void convertView(ViewHolder viewHolder, BaseDialog baseDialog) {

        ImageView ivIcon = viewHolder.getView(R.id.ivIcon);
        TextView tvTitle = viewHolder.getView(R.id.tvTitle);
        TextView tvContent = viewHolder.getView(R.id.tvContent);
        TextView tvPositive = viewHolder.getView(R.id.tvPositive);
        TextView tvNegative = viewHolder.getView(R.id.tvNegative);

        tvPositive.setOnClickListener(this);
        tvNegative.setOnClickListener(this);

        if (mIconResId > 0) {
            ivIcon.setImageResource(mIconResId);
            ivIcon.setVisibility(VISIBLE);
        } else {
            ivIcon.setVisibility(GONE);
        }

        tvTitle.setText(mTitle);
        tvContent.setText(mContent);
        tvTitle.setVisibility(TextUtils.isEmpty(mTitle) ? GONE : VISIBLE);
        tvContent.setVisibility(TextUtils.isEmpty(mContent) ? GONE : VISIBLE);

        if (!TextUtils.isEmpty(mPositiveText) || !TextUtils.isEmpty(mNegativeText)) {
            tvPositive.setText(mPositiveText);
            tvNegative.setText(mNegativeText);
            tvPositive.setVisibility(TextUtils.isEmpty(mPositiveText) ? GONE : VISIBLE);
            tvNegative.setVisibility(TextUtils.isEmpty(mNegativeText) ? GONE : VISIBLE);

            if (mPositiveTextColorResId != 0) {
                tvPositive.setTextColor(ContextCompat.getColor(getContext(), mPositiveTextColorResId));
            }
            if (mNegativeTextColorResId != 0) {
                tvNegative.setTextColor(ContextCompat.getColor(getContext(), mNegativeTextColorResId));
            }
        } else {
            tvPositive.setText(R.string.widget_btn_confirm);
            tvNegative.setText(R.string.widget_btn_cancel);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        //宽度
//        Window window = getDialog().getWindow();
//        if (window != null) {
//            WindowManager.LayoutParams params = window.getAttributes();
//            params.width = (int) (ScreenUtils.getScreenWidth() * 0.75f);
//            window.setAttributes(params);
//        }
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.tvPositive) {
//            if (mPositiveClickListener != null) {
//                mPositiveClickListener.onClick(this);
//            }
            if (mOnButtonClickListener != null) {
                mOnButtonClickListener.onButtonClick(this, true);
            }
        } else if (i == R.id.tvNegative) {
//            if (mPositiveClickListener != null) {
//                mPositiveClickListener.onClick(this);
//            }
            if (mOnButtonClickListener != null) {
                mOnButtonClickListener.onButtonClick(this, false);
            }
        }

        dismissAllowingStateLoss();
    }

    public PromptDialog setIcon(@DrawableRes int iconResId) {
        this.mIconResId = iconResId;
        return this;
    }

    public PromptDialog setTitle(CharSequence title) {
        this.mTitle = title;
        return this;
    }

    public PromptDialog setTitle(@StringRes int resId) {
        return setTitle(StringUtils.getString(resId));
    }

    public PromptDialog setContent(CharSequence content) {
        this.mContent = content;
        return this;
    }

    public PromptDialog setContent(@StringRes int resId) {
        return setContent(StringUtils.getString(resId));
    }

    /**
     * 左边的 （默认取消）
     *
     * @param negativeText
     * @return
     */
    public PromptDialog setNegativeButton(CharSequence negativeText, @ColorRes int textColor) {
        this.mNegativeText = negativeText;
        this.mNegativeTextColorResId = textColor;
        return this;
    }

    public PromptDialog setNegativeButton(@StringRes int resId, @ColorRes int textColor) {
        return setNegativeButton(StringUtils.getString(resId), textColor);
    }


    public PromptDialog setNegativeButton(CharSequence negativeText) {
        this.mNegativeText = negativeText;
        return this;
    }

//    public PromptDialog setNegativeButton(CharSequence negativeText, OnClickListener listener) {
//        this.mNegativeText = negativeText;
//        this.mNegativeClickListener = listener;
//        return this;
//    }

    public PromptDialog setNegativeButton(@StringRes int resId) {
        return setNegativeButton(StringUtils.getString(resId));
    }

    /**
     * 右边的 （默认确定）
     *
     * @return
     */
    public PromptDialog setPositiveButton(CharSequence positiveText, @ColorRes int textColor) {
        this.mPositiveText = positiveText;
        this.mPositiveTextColorResId = textColor;
        return this;
    }

    public PromptDialog setPositiveButton(@StringRes int resId, @ColorRes int textColor) {
        return setPositiveButton(StringUtils.getString(resId), textColor);
    }


    public PromptDialog setPositiveButton(CharSequence positiveText) {
        this.mPositiveText = positiveText;
        return this;
    }

//    public PromptDialog setPositiveButton(CharSequence positiveText, OnClickListener listener) {
//        this.mPositiveText = positiveText;
//        this.mPositiveClickListener = listener;
//        return this;
//    }

    public PromptDialog setPositiveButton(@StringRes int resId) {
        return setPositiveButton(StringUtils.getString(resId));
    }

    public PromptDialog setOnButtonClickListener(OnButtonClickListener listener) {
        this.mOnButtonClickListener = listener;
        return this;
    }

    @Override
    public PromptDialog show(FragmentActivity activity) {
        return (PromptDialog) super.show(activity);
    }

    @Override
    public PromptDialog show(FragmentManager manager) {
        return (PromptDialog) super.show(manager);
    }

    public interface OnButtonClickListener {

        /**
         * 当窗口按钮被点击
         *
         * @param dialog
         * @param isPositiveClick true :PositiveButton点击, false :NegativeButton点击
         */
        void onButtonClick(PromptDialog dialog, boolean isPositiveClick);
    }

//    public interface OnClickListener {
//
//        /**
//         * 当窗口按钮被点击
//         *
//         * @param dialog
//         */
//        void onClick(PromptDialog dialog);
//    }
}
