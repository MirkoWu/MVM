package com.mirkowu.lib_widget.stateview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.DrawableRes;
import androidx.annotation.Nullable;
import androidx.annotation.StyleableRes;
import androidx.core.content.ContextCompat;

import com.mirkowu.lib_widget.R;

/**
 * 状态View
 * 可用于加载中，空状态，加载失败
 */
public class StateView extends LinearLayout {

    protected static DefaultInitializer mDefaultInitializer;

    protected View mRootView;
    protected ImageView mIvImage;
    protected TextView mTvHint;
    protected Button mBtnRefresh;
    protected Button mBtnCheckNetwork;
    protected ProgressBar mProgressBar;
    protected OnRefreshListener mOnRefreshListener;
    protected OnCheckNetworkListener mOnCheckNetworkListener;

    protected CharSequence mLoadingText;
    protected CharSequence mShowText;
    protected CharSequence mEmptyText;
    protected CharSequence mErrorText;
    protected CharSequence mRefreshText;
    protected CharSequence mCheckNetworkText;
    @DrawableRes
    protected int mShowSrcId;
    @DrawableRes
    protected int mEmptySrcId;
    @DrawableRes
    protected int mErrorSrcId;
    protected Drawable mLoadingProgressDrawable;
    @DrawableRes
    protected int mRefreshBackgroundId;
    @DrawableRes
    protected int mCheckNetworkBackgroundId;
    protected boolean mDefaultLoading = true;
    protected ViewState mState = ViewState.GONE;

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
        mRootView = inflate(context, R.layout.widget_layout_state_view, this);
        mIvImage = mRootView.findViewById(R.id.stateIvImage);
        mTvHint = mRootView.findViewById(R.id.stateTvHint);
        mBtnRefresh = mRootView.findViewById(R.id.stateBtnRefresh);
        mBtnCheckNetwork = mRootView.findViewById(R.id.stateBtnCheckNetwork);
        mProgressBar = mRootView.findViewById(R.id.stateProgressBar);


        //设置默认值
        mLoadingText = context.getString(R.string.widget_loading);
        mRefreshText = context.getString(R.string.widget_refresh);
//        mCheckNetworkText = context.getString(R.string.widget_check_network);
        mLoadingProgressDrawable = getDefaultLoadingDrawable();
        mEmptySrcId = R.drawable.widget_svg_empty;
        mErrorSrcId = R.drawable.widget_svg_network_error;
        mRefreshBackgroundId = R.drawable.widget_refresh_bg;
        mCheckNetworkBackgroundId = R.drawable.widget_refresh_bg;

        //全局初始化
        if (mDefaultInitializer != null) {
            mDefaultInitializer.init(context, this);
        }

        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.StateView);
        mDefaultLoading = ta.getBoolean(R.styleable.StateView_sv_defaultLoading, mDefaultLoading);
        mLoadingText = getString(ta, R.styleable.StateView_sv_loadingText, mLoadingText);
        int loadingProgressResId = ta.getResourceId(R.styleable.StateView_sv_loadingSrc, 0);
        if (loadingProgressResId != 0) {
            mLoadingProgressDrawable = ContextCompat.getDrawable(context, loadingProgressResId);
        }

        mShowSrcId = ta.getResourceId(R.styleable.StateView_sv_showSrc, mShowSrcId);
        mShowText = getString(ta, R.styleable.StateView_sv_showText, mShowText);

        mEmptySrcId = ta.getResourceId(R.styleable.StateView_sv_emptySrc, mEmptySrcId);
        mEmptyText = getString(ta, R.styleable.StateView_sv_emptyText, mEmptyText);

        mErrorSrcId = ta.getResourceId(R.styleable.StateView_sv_errorSrc, mErrorSrcId);
        mErrorText = getString(ta, R.styleable.StateView_sv_errorText, mErrorText);

        mRefreshText = getString(ta, R.styleable.StateView_sv_refreshText, mRefreshText);
        mCheckNetworkText = getString(ta, R.styleable.StateView_sv_checkNetworkText, mCheckNetworkText);

        mRefreshBackgroundId = ta.getResourceId(R.styleable.StateView_sv_refreshBackground, mRefreshBackgroundId);
        mCheckNetworkBackgroundId = ta.getResourceId(R.styleable.StateView_sv_checkNetworkBackground, mCheckNetworkBackgroundId);

        ta.recycle();

        mBtnRefresh.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnRefreshListener != null) {
                    mOnRefreshListener.onRefresh();
                }
            }
        });

        mBtnCheckNetwork.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnCheckNetworkListener != null) {
                    mOnCheckNetworkListener.onCheckNetwork();
                }
            }
        });

        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                //屏蔽其他点击
            }
        });

        //默认是否显示加载状态
        if (mDefaultLoading) {
            setLoadingState();
        } else {
            setGoneState();
        }
    }

    private CharSequence getString(TypedArray ta, @StyleableRes int index, CharSequence def) {
        String text = ta.getString(index);
        return text != null ? text : def;
    }

    private int getColorAccent() {
        TypedValue typedValue = new TypedValue();
        getContext().getTheme().resolveAttribute(R.attr.colorAccent, typedValue, true);
        return typedValue.data;
    }

    public Drawable getDefaultLoadingDrawable() {
        LoadingTowDot drawable = new LoadingTowDot();
//        drawable.setBounds(0, 0, 100, 100);
        drawable.setColor(getColorAccent());
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

    public Button getCheckNetworkButton() {
        return mBtnCheckNetwork;
    }

    public StateView setDefaultLoading(boolean defaultLoading) {
        mDefaultLoading = defaultLoading;
        return this;
    }

    public StateView setLoadingSrc(@DrawableRes int imgResId) {
        mLoadingProgressDrawable = ContextCompat.getDrawable(getContext(), imgResId);
        return this;
    }

    public StateView setLoadingDrawable(Drawable drawable) {
        mLoadingProgressDrawable = drawable;
        return this;
    }

    public StateView setLoadingText(CharSequence loadingText) {
        mLoadingText = loadingText;
        return this;
    }

    public StateView setEmptySrc(@DrawableRes int imgResId) {
        mEmptySrcId = imgResId;
        return this;
    }

    public StateView setEmptyText(CharSequence emptyText) {
        mEmptyText = emptyText;
        return this;
    }

    public StateView setErrorSrc(@DrawableRes int imgResId) {
        mErrorSrcId = imgResId;
        return this;
    }

    public StateView setErrorText(CharSequence errorText) {
        mEmptyText = errorText;
        return this;
    }

    public StateView setRefreshText(CharSequence refreshText) {
        mRefreshText = refreshText;
        return this;
    }

    public StateView setCheckNetworkText(CharSequence checkNetworkText) {
        mCheckNetworkText = checkNetworkText;
        return this;
    }

    public StateView setRefreshBackgroundSrc(@DrawableRes int resId) {
        mRefreshBackgroundId = resId;
        return this;
    }

    public StateView setCheckNetworkBackgroundSrc(@DrawableRes int resId) {
        mCheckNetworkBackgroundId = resId;
        return this;
    }

    /***
     * state 相关
     *
     */
    public ViewState getState() {
        return mState;
    }

    private void setState(ViewState state) {
        mState = state;
        setVisibility(VISIBLE);
        mIvImage.setVisibility(GONE);
        mTvHint.setVisibility(GONE);
        mBtnRefresh.setVisibility(GONE);
        mBtnCheckNetwork.setVisibility(GONE);
        mProgressBar.setVisibility(GONE);

        switch (state) {
            case LOADING:
                mProgressBar.setVisibility(VISIBLE);
                //setIndeterminateDrawable 时一定要同时setProgressDrawable 否则不生效，导致不显示
                mProgressBar.setIndeterminateDrawable(mLoadingProgressDrawable);
                mProgressBar.setProgressDrawable(mLoadingProgressDrawable);
                if (!TextUtils.isEmpty(mLoadingText)) {
                    mTvHint.setVisibility(VISIBLE);
                    mTvHint.setText(mLoadingText);
                }
                break;
            case EMPTY:
                if (!TextUtils.isEmpty(mEmptyText)) {
                    mTvHint.setVisibility(VISIBLE);
                    mTvHint.setText(mEmptyText);
                }

                if (mEmptySrcId != 0) {
                    mIvImage.setVisibility(VISIBLE);
                    mIvImage.setImageResource(mEmptySrcId);
                }
                break;
            case ERROR:
                if (!TextUtils.isEmpty(mErrorText)) {
                    mTvHint.setVisibility(VISIBLE);
                    mTvHint.setText(mErrorText);
                }

                if (mErrorSrcId != 0) {
                    mIvImage.setVisibility(VISIBLE);
                    mIvImage.setImageResource(mErrorSrcId);
                }

                if (!TextUtils.isEmpty(mRefreshText)) {
                    mBtnRefresh.setVisibility(VISIBLE);
                    mBtnRefresh.setText(mRefreshText);
                }
                if (!TextUtils.isEmpty(mCheckNetworkText)) {
                    mBtnCheckNetwork.setVisibility(VISIBLE);
                    mBtnCheckNetwork.setText(mCheckNetworkText);
                }
                if (mRefreshBackgroundId != 0) {
                    mBtnRefresh.setBackgroundResource(mRefreshBackgroundId);
                }
                if (mCheckNetworkBackgroundId != 0) {
                    mBtnCheckNetwork.setBackgroundResource(mCheckNetworkBackgroundId);
                }
                break;
            case SHOW:
                if (!TextUtils.isEmpty(mShowText)) {
                    mTvHint.setVisibility(VISIBLE);
                    mTvHint.setText(mShowText);
                }

                if (mShowSrcId != 0) {
                    mIvImage.setVisibility(VISIBLE);
                    mIvImage.setImageResource(mShowSrcId);
                }

                if (!TextUtils.isEmpty(mRefreshText)) {
                    mBtnRefresh.setVisibility(VISIBLE);
                    mBtnRefresh.setText(mRefreshText);
                }
                if (!TextUtils.isEmpty(mCheckNetworkText)) {
                    mBtnCheckNetwork.setVisibility(VISIBLE);
                    mBtnCheckNetwork.setText(mCheckNetworkText);
                }

                if (mRefreshBackgroundId != 0) {
                    mBtnRefresh.setBackgroundResource(mRefreshBackgroundId);
                }
                if (mCheckNetworkBackgroundId != 0) {
                    mBtnCheckNetwork.setBackgroundResource(mCheckNetworkBackgroundId);
                }
                break;
            case GONE:
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

    public void setLoadingState(CharSequence loadingText) {
        setLoadingState(mLoadingProgressDrawable, loadingText);
    }

    public void setLoadingState(@DrawableRes int loadingSrcId, CharSequence loadingText) {
        setLoadingState(ContextCompat.getDrawable(getContext(), loadingSrcId), loadingText);
    }

    public void setLoadingState(Drawable drawable, CharSequence loadingText) {
        mLoadingProgressDrawable = drawable;
        mLoadingText = loadingText;
        setState(ViewState.LOADING);
    }

    public void setEmptyState(CharSequence emptyText) {
        setEmptyState(mEmptySrcId, emptyText);
    }

    public void setEmptyState(@DrawableRes int imgResId, CharSequence emptyText) {
        mEmptySrcId = imgResId;
        mEmptyText = emptyText;
        setState(ViewState.EMPTY);
    }

    public void setErrorState(CharSequence errorText) {
        setErrorState(errorText, mRefreshText);
    }

    public void setErrorState(CharSequence errorText, CharSequence refreshText) {
        setErrorState(mErrorSrcId, errorText, refreshText);
    }

    public void setErrorState(@DrawableRes int imgResId, CharSequence errorText, CharSequence refreshText) {
        setErrorState(imgResId, errorText, refreshText, mCheckNetworkText);
    }

    public void setErrorState(@DrawableRes int imgResId, CharSequence errorText, CharSequence refreshText, CharSequence checkNetworkText) {
        mErrorSrcId = imgResId;
        mErrorText = errorText;
        mRefreshText = refreshText;
        mCheckNetworkText = checkNetworkText;
        setState(ViewState.ERROR);
    }

    public void setShowState(@DrawableRes int imgResId, CharSequence hintText) {
        setShowState(imgResId, hintText, mRefreshText, mCheckNetworkText);
    }

    public void setShowState(@DrawableRes int imgResId, CharSequence hintText, CharSequence refreshText) {
        setShowState(imgResId, hintText, refreshText, mCheckNetworkText);
    }

    /**
     * 自定义展示状态
     *
     * @param imgResId    图片
     * @param hintText    提示
     * @param refreshText 刷新按钮 为空 则隐藏按钮
     * @param refreshText 刷新按钮 为空 则隐藏按钮
     */
    public void setShowState(@DrawableRes int imgResId, CharSequence hintText, CharSequence refreshText, CharSequence checkNetworkText) {
        mShowSrcId = imgResId;
        mShowText = hintText;
        mRefreshText = refreshText;
        mCheckNetworkText = checkNetworkText;
        setState(ViewState.SHOW);
    }


    public void setOnRefreshListener(OnRefreshListener refreshListener) {
        mOnRefreshListener = refreshListener;
    }

    public void setOnCheckNetworkListener(OnCheckNetworkListener checkNetworkListener) {
        mOnCheckNetworkListener = checkNetworkListener;
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
    }


    /**
     * 全局初始化
     *
     * @param defaultInitializer
     */
    public static void setDefaultInitializer(DefaultInitializer defaultInitializer) {
        mDefaultInitializer = defaultInitializer;
    }


}