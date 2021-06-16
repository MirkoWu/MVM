package com.mirkowu.lib_widget;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.ColorInt;
import androidx.annotation.DrawableRes;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;

import com.mirkowu.lib_util.utilcode.util.SizeUtils;

/**
 * @author: mirko
 * @date: 19-10-15
 */
public class Toolbar extends FrameLayout {
    private BoldTextView tvTitle;
    private ImageView ivBack;
    private TextView tvRight;
    private ImageView ivRight;
    private View vLine;
    private boolean mShowLine;
    private int mBackIconResId;

    public Toolbar(Context context) {
        this(context, null);
    }

    public Toolbar(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public Toolbar(final Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.Toolbar);
        String title = ta.getString(R.styleable.Toolbar_title);
        mShowLine = ta.getBoolean(R.styleable.Toolbar_showLine, true);
        mBackIconResId = ta.getResourceId(R.styleable.Toolbar_backIcon, R.drawable.widget_ic_back_black);
        ta.recycle();

        View view = LayoutInflater.from(context).inflate(R.layout.widget_layout_toolbar, this);
        tvTitle = view.findViewById(R.id.btv_title);
        ivBack = view.findViewById(R.id.iv_back);
        tvRight = view.findViewById(R.id.tv_right);
        ivRight = view.findViewById(R.id.iv_right);
        vLine = view.findViewById(R.id.v_line);

        setBackIcon(mBackIconResId);
        setTitle(title);
        setShowLine(mShowLine);
    }

    public Toolbar setTitle(String title) {
        tvTitle.setText(title);
        return this;
    }

    public Toolbar setTitle(@StringRes int titleResId) {
        tvTitle.setText(titleResId);
        return this;
    }

    public Toolbar setTitleColor(@ColorInt int colorId) {
        tvTitle.setTextColor(colorId);
        return this;
    }

    public Toolbar setTitleSize(int sizeSp) {
        tvTitle.setTextSize(sizeSp);
        return this;
    }

    public Toolbar setShowBackIcon(boolean isShow) {
        return setBackIcon(isShow ? mBackIconResId : 0);
    }

    public Toolbar setBackIcon(@DrawableRes int resId) {
        mBackIconResId = resId;
        if (resId == 0) {
            ivBack.setVisibility(GONE);
        } else {
            ivBack.setVisibility(VISIBLE);
            ivBack.setImageResource(resId);
            ivBack.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (getContext() instanceof Activity) {
                        ((Activity) getContext()).onBackPressed();
                    }
                }
            });
        }
        return this;
    }

    public Toolbar setRightIcon(@DrawableRes int resId, OnClickListener clickListener) {
        ivRight.setVisibility(VISIBLE);
        ivRight.setImageResource(resId);
        ivRight.setOnClickListener(clickListener);
        return this;
    }

    public Toolbar setRightIconPadding(int paddingDp) {
        int size = SizeUtils.dp2px(paddingDp);
        ivRight.setPadding(size, size, size, size);
        return this;
    }

    public Toolbar setRightTextColor(@ColorInt int colorId) {
        tvRight.setTextColor(colorId);
        return this;
    }

    public Toolbar setRightTextSize(@ColorInt int sizeSp) {
        tvRight.setTextSize(sizeSp);
        return this;
    }

    public Toolbar setRightText(String rightText, OnClickListener clickListener) {
        tvRight.setVisibility(VISIBLE);
        tvRight.setText(rightText);
        tvRight.setOnClickListener(clickListener);
        return this;
    }

    public Toolbar setRightText(@StringRes int resId, OnClickListener clickListener) {
        tvRight.setVisibility(VISIBLE);
        tvRight.setText(resId);
        tvRight.setOnClickListener(clickListener);
        return this;
    }

    public Toolbar setShowLine(boolean showLine) {
        mShowLine = showLine;
        vLine.setVisibility(showLine ? VISIBLE : GONE);
        return this;
    }

    public Toolbar setShowLine(boolean showLine, @ColorInt int colorId) {
        mShowLine = showLine;
        vLine.setVisibility(showLine ? VISIBLE : GONE);
        vLine.setBackgroundColor(colorId);
        return this;
    }

    public boolean isShowLine() {
        return mShowLine;
    }

//
//    public void autoSizeTitleMargin() {
//        LayoutParams leftParams = (LayoutParams) ivBack.getLayoutParams();
//        LayoutParams rightParams = (LayoutParams) llMenu.getLayoutParams();
//        int leftWidth = ivBack.getWidth() + leftParams.leftMargin + leftParams.rightMargin;
//        int rightWidth = llMenu.getWidth() + rightParams.leftMargin + rightParams.rightMargin;
//        int width = Math.max(leftWidth, rightWidth);
//        int margin = 16;
//        width += margin;
//        LayoutParams params = (LayoutParams) tvToolbarTitle.getLayoutParams();
//        if (width == params.getMarginStart() && width == params.getMarginEnd()) return;
//        params.setMarginStart(width);
//        params.setMarginEnd(width);
//        tvToolbarTitle.setLayoutParams(params);
//    }
//
//    @Override
//    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
//        super.onSizeChanged(w, h, oldw, oldh);
//        autoSizeTitleMargin();
//    }
}
