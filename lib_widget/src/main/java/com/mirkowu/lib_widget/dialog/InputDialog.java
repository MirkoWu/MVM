package com.mirkowu.lib_widget.dialog;

import android.text.InputFilter;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.ColorRes;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;

import com.mirkowu.lib_widget.R;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

/**
 * @author: mirko
 * @date: 19-10-22
 */
public class InputDialog extends BaseDialog implements View.OnClickListener {
    private static final int DEFAULT_WIDTH = 280; //默认宽度 dp
    private TextView tvTitle;
    private TextView tvHint;
    private EditText etContent;
    private TextView tvPositive;
    private TextView tvNegative;

    private CharSequence title;
    private CharSequence hint;
    private CharSequence content;
    private CharSequence inputHint;
    private CharSequence mPositiveText;
    private CharSequence mNegativeText;
    private int hintTextColor;
    private int positiveTextColor;
    private int negativeTextColor;
    private int mInputMaxCharLength; //最大输入字符长度
    private OnButtonClickListener mOnButtonClickListener;

    public InputDialog() {
        setWidth(DEFAULT_WIDTH);
        setTouchOutCancel(false);
        setDialogCancelable(false);
    }

    @Override
    public int getLayoutId() {
        return R.layout.widget_dialog_input;
    }


    @Override
    protected void convertView(ViewHolder viewHolder, BaseDialog baseDialog) {

        tvTitle = viewHolder.getView(R.id.tvTitle);
        tvHint = viewHolder.getView(R.id.tvHint);
        etContent = viewHolder.getView(R.id.etContent);
        tvPositive = viewHolder.getView(R.id.tvPositive);
        tvNegative = viewHolder.getView(R.id.tvNegative);

        tvPositive.setOnClickListener(this);
        tvNegative.setOnClickListener(this);

        tvTitle.setText(title);
        tvHint.setText(hint);
        etContent.setHint(inputHint);
        etContent.setText(content);
        tvTitle.setVisibility(TextUtils.isEmpty(title) ? GONE : VISIBLE);
        tvHint.setVisibility(TextUtils.isEmpty(hint) ? GONE : VISIBLE);
        if (hintTextColor != 0) {
            tvHint.setTextColor(ContextCompat.getColor(getContext(), hintTextColor));
        }
        if (mInputMaxCharLength > 0) {
            etContent.setFilters(new InputFilter[]{new InputFilter.LengthFilter(mInputMaxCharLength)});
        }

        if (!TextUtils.isEmpty(mPositiveText) || !TextUtils.isEmpty(mNegativeText)) {
            tvPositive.setText(mPositiveText);
            tvNegative.setText(mNegativeText);
            tvPositive.setVisibility(TextUtils.isEmpty(mPositiveText) ? GONE : VISIBLE);
            tvNegative.setVisibility(TextUtils.isEmpty(mNegativeText) ? GONE : VISIBLE);

            if (positiveTextColor != 0) {
                tvPositive.setTextColor(ContextCompat.getColor(getContext(), positiveTextColor));
            }
            if (negativeTextColor != 0) {
                tvNegative.setTextColor(ContextCompat.getColor(getContext(), negativeTextColor));
            }
        } else {
            tvPositive.setText(R.string.widget_btn_confirm);
            tvNegative.setText(R.string.widget_btn_cancel);
        }
    }

    @Override
    public void onClick(View v) {
        if (mOnButtonClickListener != null) {
            int i = v.getId();
            if (i == R.id.tvPositive) {
                String content = etContent.getText().toString().trim();
                mOnButtonClickListener.onButtonClick(this, content, true);
            } else if (i == R.id.tvNegative) {
                mOnButtonClickListener.onButtonClick(this, "", false);
            }
        }
        dismissAllowingStateLoss();
    }


    public InputDialog setTitle(CharSequence title) {
        this.title = title;
        return this;
    }

    public InputDialog setHint(CharSequence hint) {
        this.hint = hint;
        return this;
    }

    public InputDialog setHint(CharSequence hint, @ColorRes int textColorInt) {
        this.hint = hint;
        this.hintTextColor = textColorInt;
        return this;
    }

    public InputDialog setContent(CharSequence content) {
        this.content = content;
        return this;
    }

    public InputDialog setInputHint(CharSequence hint) {
        this.inputHint = hint;
        return this;
    }

    /**
     * 能输入的最大字符
     *
     * @param maxCharLength
     * @return
     */
    public InputDialog setInputMaxCharLength(int maxCharLength) {
        this.mInputMaxCharLength = maxCharLength;
        return this;
    }

    /**
     * 左边的 （默认取消）
     *
     * @param negativeText
     * @return
     */
    public InputDialog setNegativeButton(CharSequence negativeText, @ColorRes int textColorInt) {
        this.mNegativeText = negativeText;
        this.negativeTextColor = textColorInt;
        return this;
    }

    public InputDialog setNegativeButton(CharSequence negativeText) {
        this.mNegativeText = negativeText;
        return this;
    }

    /**
     * 右边的 （默认确定）
     *
     * @return
     */
    public InputDialog setPositiveButton(CharSequence positiveText, @ColorRes int textColorInt) {
        this.mPositiveText = positiveText;
        this.positiveTextColor = textColorInt;
        return this;
    }

    public InputDialog setPositiveButton(CharSequence positiveText) {
        this.mPositiveText = positiveText;
        return this;
    }

    public InputDialog setOnButtonClickListener(OnButtonClickListener listener) {
        this.mOnButtonClickListener = listener;
        return this;
    }

    @Override
    public InputDialog show(FragmentActivity activity) {
        return (InputDialog) super.show(activity);
    }

    @Override
    public InputDialog show(FragmentManager manager) {
        return (InputDialog) super.show(manager);
    }

    public interface OnButtonClickListener {

        /**
         * 当窗口按钮被点击
         *
         * @param dialog
         * @param isPositiveClick true :PositiveButton点击, false :NegativeButton点击
         */
        void onButtonClick(InputDialog dialog, String content, boolean isPositiveClick);
    }
}
