package com.mirkowu.lib_widget.dialog;

import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.ColorRes;
import androidx.annotation.DrawableRes;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import com.mirkowu.lib_widget.R;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

public class PromptDialog extends BaseDialog implements View.OnClickListener {
    private static final int DEFAULT_WIDTH = 280; //默认宽度 dp
    private int mIconResId;
    private String mTitle;
    private String mContent;
    private String mPositiveText;
    private String mNegativeText;
    private int mPositiveTextColorResId;
    private int mNegativeTextColorResId;
    protected OnButtonClickListener mOnButtonClickListener;

    public PromptDialog() {
        setWidth(DEFAULT_WIDTH);
    }

    @Override
    protected int getLayoutResId() {
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
        if (mOnButtonClickListener != null) {
            int i = v.getId();
            if (i == R.id.tvPositive) {
                mOnButtonClickListener.onButtonClick(this, true);
            } else if (i == R.id.tvNegative) {
                mOnButtonClickListener.onButtonClick(this, false);
            }
        }
        dismiss();
    }

    public PromptDialog setIcon(@DrawableRes int iconResId) {
        this.mIconResId = iconResId;
        return this;
    }

    public PromptDialog setTitle(String title) {
        this.mTitle = title;
        return this;
    }

    public PromptDialog setContent(String content) {
        this.mContent = content;
        return this;
    }

    /**
     * 左边的 （默认取消）
     *
     * @param negativeText
     * @return
     */
    public PromptDialog setNegativeButton(String negativeText, @ColorRes int textColorInt) {
        this.mNegativeText = negativeText;
        this.mNegativeTextColorResId = textColorInt;
        return this;
    }


    public PromptDialog setNegativeButton(String negativeText) {
        this.mNegativeText = negativeText;
        return this;
    }

    /**
     * 右边的 （默认确定）
     *
     * @return
     */
    public PromptDialog setPositiveButton(String positiveText, @ColorRes int textColorInt) {
        this.mPositiveText = positiveText;
        this.mPositiveTextColorResId = textColorInt;
        return this;
    }


    public PromptDialog setPositiveButton(String positiveText) {
        this.mPositiveText = positiveText;
        return this;
    }


    public PromptDialog setOnButtonClickListener(OnButtonClickListener listener) {
        this.mOnButtonClickListener = listener;
        return this;
    }

    /**
     * 显示Dialog
     */
    public void show(FragmentActivity activity) {
        this.show(activity.getSupportFragmentManager(), getClass().getName());
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
}
