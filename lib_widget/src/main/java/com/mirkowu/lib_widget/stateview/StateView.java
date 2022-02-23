package com.mirkowu.lib_widget.stateview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.AnimationDrawable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.DrawableRes;
import androidx.annotation.Nullable;

import com.mirkowu.lib_widget.R;

/**
 * 状态View
 * 可用于加载中，空状态，加载失败
 */
public class StateView extends LinearLayout {

    private View mRootView;
    private ImageView mIvLoading;
    private ImageView mIvHint;
    private TextView mTvHint;
    private Button mBtnRefresh;
    private int mState = ViewState.LOADING;
    private OnRefreshListener mOnRefreshListener;

    private CharSequence mLoadingText;
    private CharSequence mShowText;
    private CharSequence mRefreshText;
    private Integer mShowIconResId;
    private Integer mLoadingImgResId;
    private Integer mRefreshBackgroundId;
    private boolean mIsShowRefresh;
    private AnimationDrawable mAnimationDrawable;

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
        mLoadingImgResId = ta.getResourceId(R.styleable.StateView_sv_loadingIcon, 0);
        int loadingAnimResId = ta.getResourceId(R.styleable.StateView_sv_loadingAnim, 0);
        mShowIconResId = ta.getResourceId(R.styleable.StateView_sv_showIcon, 0);
        mLoadingText = ta.getString(R.styleable.StateView_sv_loadingText);
        mShowText = ta.getString(R.styleable.StateView_sv_showText);
        boolean defaultLoading = ta.getBoolean(R.styleable.StateView_sv_defaultLoading, true);
        mRefreshText = ta.getString(R.styleable.StateView_sv_refreshText);
        mRefreshBackgroundId = ta.getResourceId(R.styleable.StateView_sv_refreshBackground, 0);
        ta.recycle();

        mRootView = inflate(context, R.layout.widget_layout_state_view, this);
        mIvLoading = mRootView.findViewById(R.id.ivLoading);
        mIvHint = mRootView.findViewById(R.id.ivHint);
        mTvHint = mRootView.findViewById(R.id.tvHint);
        mBtnRefresh = mRootView.findViewById(R.id.btnRefresh);

        mBtnRefresh.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mIsShowRefresh && mState == ViewState.SHOW) {
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

        if (loadingAnimResId != 0) {
            mIvLoading.setBackgroundResource(loadingAnimResId);
            if (mIvLoading.getBackground() instanceof AnimationDrawable) {
                mAnimationDrawable = (AnimationDrawable) mIvLoading.getBackground();
            }
        }

        //默认显示加载状态
        if (defaultLoading) {
            if (mLoadingText == null) {
                mLoadingText = context.getString(R.string.widget_loading);
            }
            setLoadingState();
        }
    }

    public int getState() {
        return mState;
    }

    public ImageView getLoadingView() {
        return mIvLoading;
    }

    public Button getRefreshButton() {
        return mBtnRefresh;
    }


    private void setState(int state) {
        mState = state;
        setVisibility(VISIBLE);
        mIvLoading.setVisibility(GONE);
        mIvHint.setVisibility(GONE);
        mTvHint.setVisibility(GONE);
        mBtnRefresh.setVisibility(GONE);

        if (mAnimationDrawable != null && mAnimationDrawable.isRunning()) {
            mAnimationDrawable.stop();
        }

        switch (state) {
            case ViewState.LOADING:
                mIvLoading.setImageResource(mLoadingImgResId);
                mIvLoading.setVisibility(VISIBLE);

                if (mAnimationDrawable != null && !mAnimationDrawable.isRunning()) {
                    mAnimationDrawable.start();
                }
                if (!TextUtils.isEmpty(mLoadingText)) {
                    mTvHint.setVisibility(VISIBLE);
                    mTvHint.setText(mLoadingText);
                }
                break;
            case ViewState.SHOW:
                mTvHint.setText(mShowText);
                mTvHint.setVisibility(VISIBLE);

                if (mShowIconResId != 0) {
                    mIvHint.setImageResource(mShowIconResId);
                    mIvHint.setVisibility(VISIBLE);
                }

                mBtnRefresh.setVisibility(mIsShowRefresh ? VISIBLE : GONE);
                if (!TextUtils.isEmpty(mRefreshText)) {
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

    public void setLoadingState() {
        setLoadingState(mLoadingText);
    }

    public void setLoadingState(CharSequence hint) {
        setLoadingState(mLoadingImgResId, hint);
    }

    public void setLoadingState(@DrawableRes Integer loadingResId, CharSequence hint) {
        mLoadingImgResId = loadingResId;
        mLoadingText = hint;
        setState(ViewState.LOADING);
    }

    public void setGoneState() {
        setState(ViewState.GONE);
    }

    public void setShowState() {
        setState(ViewState.SHOW);
    }

    public void setShowState(@DrawableRes Integer imgResId, CharSequence hint) {
        setShowState(imgResId, hint, false);
    }

    public void setShowState(@DrawableRes Integer imgResId, CharSequence hint, boolean showRefresh) {
        mShowIconResId = imgResId;
        mShowText = hint;
        mIsShowRefresh = showRefresh;
        setState(ViewState.SHOW);
    }

//    public void setEmpty() {
//        setShowState(R.drawable.box, getContext().getString(R.string.common_nothing_text), false);
//    }
//
//    public void setEmpty(CharSequence hint) {
//        setShowState(R.drawable.box, hint, false);
//    }
//
//
//    public void setError() {
//        setShowState(R.drawable.rockets, getContext().getString(R.string.hint_load_failed), true);
//    }

    public void setOnRefreshListener(OnRefreshListener refreshListener) {
        mOnRefreshListener = refreshListener;
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (mAnimationDrawable != null && mAnimationDrawable.isRunning()) {
            mAnimationDrawable.stop();
        }
    }
}