package com.mirkowu.lib_widget.dialog;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.ColorRes;
import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;

import com.mirkowu.lib_util.ScreenUtil;
import com.mirkowu.lib_widget.R;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

public class CommonHintDialog extends DialogFragment implements View.OnClickListener {
    protected ImageView ivIcon;
    protected TextView tvTitle;
    protected TextView tvContent;
    protected TextView tvPositive;
    protected TextView tvNegative;

    private int icon;
    private String title;
    private String content;
    private boolean cancelable;
    private String positiveLabel;
    private String negativeLabel;
    private int positiveTextColor;
    private int negativeTextColor;
    private boolean useDefaultButton;
    protected OnButtonClickListener listener;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.widget_dialog_common_hint, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initialize();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (!isAdded()) return;
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable());
        setStyle(DialogFragment.STYLE_NO_INPUT, android.R.style.Theme_Light_NoTitleBar_Fullscreen);
        int width = (int) (ScreenUtil.getScreenWidth(getContext()) * 0.75);//设定宽度为屏幕宽度的0.75
        getDialog().getWindow().setLayout(width, ViewGroup.LayoutParams.WRAP_CONTENT);
    }


    protected void initialize() {
        ivIcon = getView().findViewById(R.id.ivIcon);
        tvTitle = getView().findViewById(R.id.tvTitle);
        tvContent = getView().findViewById(R.id.tvContent);
        tvPositive = getView().findViewById(R.id.tvPositive);
        tvNegative = getView().findViewById(R.id.tvNegative);

        tvPositive.setOnClickListener(this);
        tvNegative.setOnClickListener(this);

        if (icon > 0) {
            ivIcon.setImageResource(icon);
            ivIcon.setVisibility(VISIBLE);
        } else {
            ivIcon.setVisibility(GONE);
        }

        tvTitle.setText(title);
        tvContent.setText(content);
        tvTitle.setVisibility(TextUtils.isEmpty(title) ? GONE : VISIBLE);
        tvContent.setVisibility(TextUtils.isEmpty(content) ? GONE : VISIBLE);

        if (!useDefaultButton) {
            tvPositive.setText(positiveLabel);
            tvNegative.setText(negativeLabel);
            tvPositive.setVisibility(TextUtils.isEmpty(positiveLabel) ? GONE : VISIBLE);
            tvNegative.setVisibility(TextUtils.isEmpty(negativeLabel) ? GONE : VISIBLE);

            if (positiveTextColor != 0)
                tvPositive.setTextColor(ContextCompat.getColor(getContext(), positiveTextColor));
            if (negativeTextColor != 0)
                tvNegative.setTextColor(ContextCompat.getColor(getContext(), negativeTextColor));
        } else {
            tvPositive.setText(R.string.widget_btn_confirm);
            tvNegative.setText(R.string.widget_btn_cancel);
        }

        setCancelable(cancelable);

    }

    @Override
    public void onClick(View v) {
        if (listener != null) {
            int i = v.getId();
            if (i == R.id.tvPositive) {
                listener.onButtonClick(this, true);
            } else if (i == R.id.tvNegative) {
                listener.onButtonClick(this, false);
            }
        }
        dismiss();
    }

    public CommonHintDialog setIcon(@DrawableRes int icon) {
        this.icon = icon;
        return this;
    }

    public CommonHintDialog setTitle(String title) {
        this.title = title;
        return this;
    }

    public CommonHintDialog setContent(String content) {
        this.content = content;
        return this;
    }

    public CommonHintDialog setDialogCancelable(boolean cancelable) {
        this.cancelable = cancelable;
        return this;
    }

    public CommonHintDialog useDefButton() {
        useDefaultButton = true;
        return this;
    }

    /**
     * 左边的 （默认取消）
     *
     * @param negativeLabel
     * @return
     */
    public CommonHintDialog setNegativeButton(String negativeLabel, @ColorRes int textColorInt) {
        this.negativeLabel = negativeLabel;
        this.negativeTextColor = textColorInt;
        return this;
    }


    public CommonHintDialog setNegativeButton(String negativeLabel) {
        this.negativeLabel = negativeLabel;
        return this;
    }

    /**
     * 右边的 （默认确定）
     *
     * @return
     */
    public CommonHintDialog setPositiveButton(String positiveLabel, @ColorRes int textColorInt) {
        this.positiveLabel = positiveLabel;
        this.positiveTextColor = textColorInt;
        return this;
    }


    public CommonHintDialog setPositiveButton(String positiveLabel) {
        this.positiveLabel = positiveLabel;
        return this;
    }


    public CommonHintDialog setOnButtonClickListener(OnButtonClickListener listener) {
        this.listener = listener;
        return this;
    }

    /**
     * 显示Dialog
     */
    public void show(AppCompatActivity activity) {
        this.show(activity.getSupportFragmentManager(), getClass().getName());
    }

    public interface OnButtonClickListener {

        /**
         * 当窗口按钮被点击
         *
         * @param dialog
         * @param isPositiveClick true :PositiveButton点击, false :NegativeButton点击
         */
        void onButtonClick(CommonHintDialog dialog, boolean isPositiveClick);
    }
}
