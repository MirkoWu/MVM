package com.mirkowu.lib_widget.stateview;

import android.content.Context;
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

    private CharSequence hintText;
    private int hintResId;
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
        animationDrawable = (AnimationDrawable) ivLoading.getBackground();
        setLoadingState();
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
                ivLoading.setImageResource(hintResId);
                ivLoading.setVisibility(VISIBLE);
                if (animationDrawable != null && !animationDrawable.isRunning()) {
                    animationDrawable.start();
                }
                if (!TextUtils.isEmpty(hintText)) {
                    tvHint.setVisibility(VISIBLE);
                    tvHint.setText(hintText);
                }
                break;
            case ViewState.SHOW:
                ivHint.setImageResource(hintResId);
                tvHint.setText(hintText);

                ivHint.setVisibility(VISIBLE);
                tvHint.setVisibility(VISIBLE);
                btnRefresh.setVisibility(isShowRefresh ? VISIBLE : GONE);

                break;
            case ViewState.GONE:
                setVisibility(GONE);
                break;

        }
    }

    public void setLoadingState() {
        setLoadingState("");
    }

    public void setLoadingState(CharSequence hint) {
        setLoadingState(0, hint);
    }

    public void setLoadingState(@DrawableRes int imgResId, CharSequence hint) {
        hintResId = imgResId;
        hintText = hint;
        setState(ViewState.LOADING);
    }

    public void setGoneState() {
        setState(ViewState.GONE);
    }

    public void setShowState(@DrawableRes int imgResId, CharSequence hint) {
        setShowState(imgResId, hint, false);
    }

    public void setShowState(@DrawableRes int imgResId, CharSequence hint, boolean showRefresh) {
        hintResId = imgResId;
        hintText = hint;
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