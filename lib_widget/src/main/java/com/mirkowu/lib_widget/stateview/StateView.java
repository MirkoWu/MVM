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

public class StateView extends LinearLayout {

    private View rootView;
    private ImageView ivLoading;
    private ImageView ivHint;
    private TextView tvHint;
    private Button btnRefresh;
    private int mState = ViewState.LOADING;
    private OnRefreshListener onRefreshListener;

    private CharSequence loadingText;
    private CharSequence showText;
    private CharSequence refreshText;
    private Integer hintImgResId;
    private Integer loadingImgResId;
    private Integer loadingAnimResId;
    private Integer refreshBackgroundId;
    private boolean isShowRefresh;
    private AnimationDrawable animationDrawable;

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
        loadingImgResId = ta.getResourceId(R.styleable.StateView_sv_loadingIcon, 0);
        int loadingAnimResId = ta.getResourceId(R.styleable.StateView_sv_loadingAnim, 0);
        hintImgResId = ta.getResourceId(R.styleable.StateView_sv_showIcon, 0);
        loadingText = ta.getString(R.styleable.StateView_sv_loadingText);
        showText = ta.getString(R.styleable.StateView_sv_showText);
        boolean defaultLoading = ta.getBoolean(R.styleable.StateView_sv_defaultLoading, false);
        refreshText = ta.getString(R.styleable.StateView_sv_refreshText);
        refreshBackgroundId = ta.getResourceId(R.styleable.StateView_sv_refreshBackground, 0);
        ta.recycle();

        rootView = inflate(context, R.layout.widget_layout_state_view, this);
        ivLoading = rootView.findViewById(R.id.ivLoading);
        ivHint = rootView.findViewById(R.id.ivHint);
        tvHint = rootView.findViewById(R.id.tvHint);
        btnRefresh = rootView.findViewById(R.id.btnRefresh);

        btnRefresh.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isShowRefresh && mState == ViewState.SHOW) {
                    if (onRefreshListener != null) {
                        onRefreshListener.onRefresh();
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
            ivLoading.setBackgroundResource(loadingAnimResId);
            if (ivLoading.getBackground() instanceof AnimationDrawable) {
                animationDrawable = (AnimationDrawable) ivLoading.getBackground();
            }
        }

        //默认显示加载状态
        if (defaultLoading) {
            setLoadingState();
        }
    }

    public int getState() {
        return mState;
    }

    private void setState(int state) {
        mState = state;
        setVisibility(VISIBLE);
        ivLoading.setVisibility(GONE);
        ivHint.setVisibility(GONE);
        tvHint.setVisibility(GONE);
        btnRefresh.setVisibility(GONE);

        if (animationDrawable != null && animationDrawable.isRunning()) {
            animationDrawable.stop();
        }

        switch (state) {
            case ViewState.LOADING:
                ivLoading.setImageResource(loadingImgResId);
                ivLoading.setVisibility(VISIBLE);

                if (animationDrawable != null && !animationDrawable.isRunning()) {
                    animationDrawable.start();
                }
                if (!TextUtils.isEmpty(loadingText)) {
                    tvHint.setVisibility(VISIBLE);
                    tvHint.setText(loadingText);
                }
                break;
            case ViewState.SHOW:
                tvHint.setText(showText);
                tvHint.setVisibility(VISIBLE);

                if (hintImgResId != 0) {
                    ivHint.setImageResource(hintImgResId);
                    ivHint.setVisibility(VISIBLE);
                }

                btnRefresh.setVisibility(isShowRefresh ? VISIBLE : GONE);
                if (!TextUtils.isEmpty(refreshText)) {
                    btnRefresh.setText(refreshText);
                }
                if (refreshBackgroundId != 0) {
                    btnRefresh.setBackgroundColor(refreshBackgroundId);
                }
                break;
            case ViewState.GONE:
                setVisibility(GONE);
                break;

        }
    }

    public void setLoadingState() {
        setLoadingState(loadingText);
    }

    public void setLoadingState(CharSequence hint) {
        setLoadingState(loadingImgResId, hint);
    }

    public void setLoadingState(@DrawableRes Integer loadingResId, CharSequence hint) {
        loadingImgResId = loadingResId;
        loadingText = hint;
        setState(ViewState.LOADING);
    }

    public void setGoneState() {
        setState(ViewState.GONE);
    }

    public void setShowState(@DrawableRes Integer imgResId, CharSequence hint) {
        setShowState(imgResId, hint, false);
    }

    public void setShowState(@DrawableRes Integer imgResId, CharSequence hint, boolean showRefresh) {
        hintImgResId = imgResId;
        showText = hint;
        isShowRefresh = showRefresh;
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
        onRefreshListener = refreshListener;
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (animationDrawable != null && animationDrawable.isRunning()) {
            animationDrawable.stop();
        }
    }
}