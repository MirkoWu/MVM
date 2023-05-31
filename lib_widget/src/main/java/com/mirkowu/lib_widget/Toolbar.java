package com.mirkowu.lib_widget;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.ColorInt;
import androidx.annotation.DrawableRes;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.core.content.ContextCompat;

import com.mirkowu.lib_util.utilcode.util.BarUtils;
import com.mirkowu.lib_util.utilcode.util.SizeUtils;

/**
 * @author: mirko
 * @date: 19-10-15
 */
public class Toolbar extends RelativeLayout {
    public static final int ELLIPSIZE_NONE = -1;
    private BoldTextView tvTitle;
    private ImageView ivBack;
    private ImageView ivClose;
    private TextView tvRight;
    private ImageView ivRight;
    private View vLine;
    private boolean mShowLine;
    private boolean mShowStatusBarHeight;
    private int mTitleColorId;
    private int mTitleTextSize;
    private int mRightColorId;
    private Drawable mBackIconDrawable;
    private Drawable mCloseIconDrawable;
    private int mTitleEllipsize;

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
        mTitleColorId = ta.getColor(R.styleable.Toolbar_titleColor, Color.parseColor("#222222"));
        mTitleTextSize = ta.getDimensionPixelSize(R.styleable.Toolbar_titleTextSize, SizeUtils.dp2px(17f));
        mRightColorId = ta.getColor(R.styleable.Toolbar_rightColor, Color.parseColor("#333333"));
        mBackIconDrawable = ta.getDrawable(R.styleable.Toolbar_backIcon);
        mCloseIconDrawable = ta.getDrawable(R.styleable.Toolbar_closeIcon);
        mTitleEllipsize = ta.getInt(R.styleable.Toolbar_titleEllipsize, ELLIPSIZE_NONE);
        mShowLine = ta.getBoolean(R.styleable.Toolbar_showLine, true);
        boolean showStatusBarHeight = ta.getBoolean(R.styleable.Toolbar_showStatusBarHeight, false);
        ta.recycle();

        View view = LayoutInflater.from(context).inflate(R.layout.widget_layout_toolbar, this, true);
        tvTitle = view.findViewById(R.id.btv_title);
        ivBack = view.findViewById(R.id.iv_back);
        ivClose = view.findViewById(R.id.iv_close);
        tvRight = view.findViewById(R.id.tv_right);
        ivRight = view.findViewById(R.id.iv_right);
        vLine = view.findViewById(R.id.v_line);

        if (getBackground() == null) {
            setBackgroundColor(Color.WHITE);
        }
        setTitle(title);
        setTitleColor(mTitleColorId);
        setTitleTextSize(TypedValue.COMPLEX_UNIT_PX, mTitleTextSize);
        setRightTextColor(mRightColorId);
        setBackIcon(mBackIconDrawable);
        setCloseIcon(mCloseIconDrawable);
        setShowLine(mShowLine);
        setShowStatusBarHeight(showStatusBarHeight);
        initTitleEllipsize();
    }

    private void initTitleEllipsize() {
        if (mTitleEllipsize > -1) {
            if (mTitleEllipsize == 0) {
                setTitleEllipsize(TextUtils.TruncateAt.START);
            } else if (mTitleEllipsize == 1) {
                setTitleEllipsize(TextUtils.TruncateAt.MIDDLE);
            } else if (mTitleEllipsize == 2) {
                setTitleEllipsize(TextUtils.TruncateAt.END);
            } else {
                setTitleEllipsize(TextUtils.TruncateAt.MARQUEE);
            }
        }
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

    /**
     * 设置标题字体
     *
     * @param sizeSp 单位sp
     * @return
     */
    public Toolbar setTitleTextSize(int sizeSp) {
        return setTitleTextSize(TypedValue.COMPLEX_UNIT_SP, sizeSp);
    }

    /**
     * 设置标题字体
     *
     * @param unit 单位
     * @param size 大小
     * @return
     */
    public Toolbar setTitleTextSize(int unit, float size) {
        tvTitle.setTextSize(unit, size);
        return this;
    }

    /**
     * 设置标题缩略格式 或 跑马灯
     *
     * @param where
     * @return
     */
    public Toolbar setTitleEllipsize(TextUtils.TruncateAt where) {
        tvTitle.setEllipsize(where);
        if (where == TextUtils.TruncateAt.MARQUEE) {
            tvTitle.setMarqueeRepeatLimit(-1);
            tvTitle.setFocusableInTouchMode(true);
            tvTitle.setFocusable(true);
            tvTitle.setSelected(true);
        }
        return this;
    }

    /**
     * 设置是否显示返回键
     *
     * @param isShow
     * @return
     */
    public Toolbar setShowBackIcon(boolean isShow) {
        if (isShow) {
            setBackIcon(mBackIconDrawable);
        } else {
            ivBack.setVisibility(GONE);
        }
        return this;
    }

    public Toolbar setBackIcon(@DrawableRes int resId) {
        return setBackIcon(ContextCompat.getDrawable(getContext(), resId));
    }

    public Toolbar setBackIcon(Drawable drawable) {
        mBackIconDrawable = drawable;
        if (drawable == null) {
            ivBack.setVisibility(GONE);
        } else {
            ivBack.setVisibility(VISIBLE);
            ivBack.setImageDrawable(drawable);
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

    /**
     * 设置是否显示关闭按钮，一般多用于WebView
     *
     * @param isShow
     * @return
     */
    public Toolbar setShowCloseIcon(boolean isShow) {
        if (isShow) {
            setCloseIcon(mCloseIconDrawable);
        } else {
            ivClose.setVisibility(GONE);
        }
        return this;
    }

    public Toolbar setCloseIcon(@DrawableRes int resId) {
        return setCloseIcon(ContextCompat.getDrawable(getContext(), resId));
    }

    public Toolbar setCloseIcon(Drawable drawable) {
        mCloseIconDrawable = drawable;
        if (drawable == null) {
            ivClose.setVisibility(GONE);
        } else {
            ivClose.setVisibility(VISIBLE);
            ivClose.setImageDrawable(drawable);
            ivClose.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (getContext() instanceof Activity) {
                        ((Activity) getContext()).finish();
                    }
                }
            });
        }
        return this;
    }

    /**
     * 设置右功能键 图片资源
     *
     * @param resId
     * @param clickListener
     * @return
     */
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

    /**
     * 设置右功能键 文字内容
     *
     * @param rightText
     * @param clickListener
     * @return
     */
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


    /**
     * 设置是否显示底部分割线
     *
     * @param showLine 是否显示
     * @param colorId  分割线颜色 默认 #10000000
     * @return
     */
    public Toolbar setShowLine(boolean showLine, @ColorInt int colorId) {
        mShowLine = showLine;
        vLine.setVisibility(showLine ? VISIBLE : GONE);
        vLine.setBackgroundColor(colorId);
        return this;
    }

    public Toolbar setShowLine(boolean showLine) {
        return setShowLine(showLine, Color.parseColor("#10000000"));
    }

    public boolean isShowLine() {
        return mShowLine;
    }

    /**
     * 设置显示状态栏高度
     *
     * @param showStatusBarHeight
     * @return
     */
    public Toolbar setShowStatusBarHeight(boolean showStatusBarHeight) {
        int paddingTop = getPaddingTop();
        int statusBarHeight = BarUtils.getStatusBarHeight();
        if (showStatusBarHeight) {
            //如果显示状态栏高度，FitsSystemWindows 就不要设置，否则会重复计算
            setFitsSystemWindows(false);
            if (!mShowStatusBarHeight) {
                paddingTop += statusBarHeight;
            }
        } else {
            if (mShowStatusBarHeight) {
                paddingTop -= statusBarHeight;
            }
        }
        setPadding(getPaddingLeft(), paddingTop, getPaddingRight(), getPaddingBottom());
        mShowStatusBarHeight = showStatusBarHeight;
        return this;
    }

    public boolean isShowStatusBarHeight() {
        return mShowStatusBarHeight;
    }
}
