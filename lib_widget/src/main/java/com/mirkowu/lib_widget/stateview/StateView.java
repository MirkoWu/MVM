package com.mirkowu.lib_widget.stateview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.mirkowu.lib_widget.R;

/**
 * 状态View
 * 可用于加载中，空状态，加载失败
 */
public class StateView extends LinearLayout {

    private View mRootView;
    private ImageView mIvImage;
    private TextView mTvHint;
    private Button mBtnRefresh;
    private ProgressBar mProgressBar;
    private int mState = ViewState.LOADING;
    private OnRefreshListener mOnRefreshListener;

    private CharSequence mLoadingText;
    private CharSequence mShowText;
    private CharSequence mRefreshText;
    private Integer mShowIconResId;
    private Drawable mLoadingProgressDrawable;
    private Integer mRefreshBackgroundId;

    public StateView(Context context) {
        this(context, null);
    }

    public StateView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public StateView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);

    }

    private void init(Context context, AttributeSet attrs) {
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.StateView);
        mLoadingText = ta.getString(R.styleable.StateView_sv_loadingText);
        int mLoadingProgressResId = ta.getResourceId(R.styleable.StateView_sv_loadingProgress, 0);
        mShowIconResId = ta.getResourceId(R.styleable.StateView_sv_showIcon, 0);
        mShowText = ta.getString(R.styleable.StateView_sv_showText);
        boolean defaultLoading = ta.getBoolean(R.styleable.StateView_sv_defaultLoading, true);
        mRefreshText = ta.getString(R.styleable.StateView_sv_refreshText);
        mRefreshBackgroundId = ta.getResourceId(R.styleable.StateView_sv_refreshBackground, 0);
        ta.recycle();

        mRootView = inflate(context, R.layout.widget_layout_state_view, this);
        mIvImage = mRootView.findViewById(R.id.ivImage);
        mTvHint = mRootView.findViewById(R.id.tvHint);
        mBtnRefresh = mRootView.findViewById(R.id.btnRefresh);
        mProgressBar = mRootView.findViewById(R.id.mProgressBar);

        mBtnRefresh.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mState == ViewState.SHOW) {
                    if (mOnRefreshListener != null) {
                        mOnRefreshListener.onRefresh();
                    }
                }
            }
        });
        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                //屏蔽其他点击
            }
        });

        //设置默认值
        mLoadingProgressDrawable = getDefaultLoadingDrawable();
        if (mLoadingProgressResId != 0) {
            mLoadingProgressDrawable = ContextCompat.getDrawable(context, mLoadingProgressResId);
        }

        if (TextUtils.isEmpty(mRefreshText)) {
            mRefreshText = context.getString(R.string.widget_refresh);
        }

        if (mLoadingText == null) {
            mLoadingText = context.getString(R.string.widget_loading);
        }

        //默认显示加载状态
        if (defaultLoading) {
            setLoadingState();
        } else {
            setGoneState();
        }
    }

    protected Drawable getDefaultLoadingDrawable() {
        LoadingDot drawable = new LoadingDot();
//        drawable.setBounds(0, 0, 100, 100);
        drawable.setColor(Color.parseColor("#F88E2C"));
        return drawable;
    }

    public ProgressBar getProgressBar() {
        return mProgressBar;
    }

    public ImageView getHintImageView() {
        return mIvImage;
    }

    public TextView getHintTextView() {
        return mTvHint;
    }

    public Button getRefreshButton() {
        return mBtnRefresh;
    }

    public int getState() {
        return mState;
    }

    private void setState(int state) {
        mState = state;
        setVisibility(VISIBLE);
        mIvImage.setVisibility(GONE);
        mTvHint.setVisibility(GONE);
        mBtnRefresh.setVisibility(GONE);
        mProgressBar.setVisibility(GONE);

        switch (state) {
            case ViewState.LOADING:
                mProgressBar.setVisibility(VISIBLE);
                if (!TextUtils.isEmpty(mLoadingText)) {
                    mTvHint.setVisibility(VISIBLE);
                    mTvHint.setText(mLoadingText);
                }
                break;
            case ViewState.SHOW:
                if (!TextUtils.isEmpty(mShowText)) {
                    mTvHint.setVisibility(VISIBLE);
                    mTvHint.setText(mShowText);
                }

                if (mShowIconResId != 0) {
                    mIvImage.setVisibility(VISIBLE);
                    mIvImage.setImageResource(mShowIconResId);
                }

                if (!TextUtils.isEmpty(mRefreshText)) {
                    mBtnRefresh.setVisibility(VISIBLE);
                    mBtnRefresh.setText(mRefreshText);
                }
                if (mRefreshBackgroundId != 0) {
                    mBtnRefresh.setBackgroundColor(mRefreshBackgroundId);
                }
                break;
            case ViewState.GONE:
                setVisibility(GONE);
                break;
        }
    }

    public void setGoneState() {
        setState(ViewState.GONE);
    }

    public void setLoadingState() {
        setLoadingState(mLoadingText);
    }

    public void setLoadingState(CharSequence hint) {
        setLoadingState(mLoadingProgressDrawable, hint);
    }

    public void setLoadingState(@NonNull @DrawableRes Integer progressResId, CharSequence hint) {
        setLoadingState(ContextCompat.getDrawable(getContext(), progressResId), hint);
    }

    public void setLoadingState(Drawable drawable, CharSequence hint) {
        mLoadingProgressDrawable = drawable;
        //setIndeterminateDrawable 时一定要同时setProgressDrawable 否则不生效，导致不显示
        mProgressBar.setIndeterminateDrawable(drawable);
        mProgressBar.setProgressDrawable(drawable);
        mLoadingText = hint;
        setState(ViewState.LOADING);
    }

    public void setEmptyState(CharSequence emptyHint) {
        setShowState(R.drawable.widget_svg_empty, emptyHint, null);
    }

    public void setErrorState(CharSequence errorHint) {
        setErrorState(errorHint, mRefreshText);
    }

    public void setErrorState(CharSequence errorHint, CharSequence refreshText) {
        setShowState(R.drawable.widget_svg_network_error, errorHint, refreshText);
    }

    public void setShowState(@DrawableRes Integer imgResId, CharSequence hint) {
        setShowState(imgResId, hint, mRefreshText);
    }

    /**
     * 设置展示状态
     *
     * @param imgResId    图片
     * @param hintText    提示
     * @param refreshText 刷新按钮 为空 则隐藏按钮
     */
    public void setShowState(@DrawableRes Integer imgResId, CharSequence hintText, CharSequence refreshText) {
        mShowIconResId = imgResId;
        mShowText = hintText;
        mRefreshText = refreshText;
        setState(ViewState.SHOW);
    }


    public void setOnRefreshListener(OnRefreshListener refreshListener) {
        mOnRefreshListener = refreshListener;
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
    }
}