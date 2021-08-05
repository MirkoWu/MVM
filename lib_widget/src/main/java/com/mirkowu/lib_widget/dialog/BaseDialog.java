package com.mirkowu.lib_widget.dialog;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.SparseArray;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.annotation.FloatRange;
import androidx.annotation.IdRes;
import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StyleRes;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.mirkowu.lib_util.PermissionsUtil;
import com.mirkowu.lib_util.utilcode.util.ConvertUtils;
import com.mirkowu.lib_util.utilcode.util.ScreenUtils;
import com.mirkowu.lib_widget.R;

import java.lang.reflect.Field;


/**
 * 基础弹框
 */
public abstract class BaseDialog extends DialogFragment implements DialogInterface.OnKeyListener {
    private Context mContext;
    private float mDimAmount = 0.5f; //背景昏暗度
    private boolean mShowBottomEnable; //是否底部显示
    private boolean mShowTopEnable; //是否顶部显示
    private int mMarginHor = 0; //左右边距
    private int mMarginVer = 0; //san边距
    private int mAnimStyle = 0; //进入退出动画
    private boolean mTouchOutCancel = true; //点击外部取消
    private boolean mCancelable = true; //能否取消
    private int mWidth;
    private int mHeight;
    private boolean mKeyBack;
    private Runnable mRunnable;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_TITLE, R.style.BaseDialog);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from(mContext).inflate(getLayoutResId(), container, false);
        ViewHolder viewHolder = new ViewHolder(view);
        convertView(viewHolder, this);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        initParams();
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        if (mRunnable != null) {
            mRunnable.run();
        }
    }

    private void initParams() {
        Window window = getDialog().getWindow();
        if (window != null) {
            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            WindowManager.LayoutParams params = window.getAttributes();
            params.y = ConvertUtils.dp2px(mMarginVer);
            params.dimAmount = mDimAmount;
            if (mDimAmount == 0) {
                //为0时，取消背景色，防止状态栏变色
                window.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
            }
            if (mShowBottomEnable) {
                params.gravity = Gravity.BOTTOM;
            } else if (mShowTopEnable) {
                params.gravity = Gravity.TOP;
            }
            if (mWidth == 0) {
                params.width = ScreenUtils.getScreenWidth() - 2 * ConvertUtils.dp2px(mMarginHor);
            } else {
                params.width = ConvertUtils.dp2px(mWidth);
            }
            if (mHeight == 0) {
                params.height = WindowManager.LayoutParams.WRAP_CONTENT;
            } else {
                params.height = ConvertUtils.dp2px(mHeight);
            }
            if (mAnimStyle != 0) {
                window.setWindowAnimations(mAnimStyle);
            }
            window.setAttributes(params);
        }

        setCancelable(mCancelable);
        getDialog().setCanceledOnTouchOutside(mTouchOutCancel);
    }

    @LayoutRes
    public abstract int getLayoutResId();

    /**
     * 设置背景昏暗度
     *
     * @param dimAmount
     * @return
     */
    public BaseDialog setDimAmount(@FloatRange(from = 0, to = 1) float dimAmount) {
        mDimAmount = dimAmount;
        return this;
    }

    /**
     * 取消事件监听
     *
     * @param runnable
     * @return
     */
    public BaseDialog setDismiss(Runnable runnable) {
        mRunnable = runnable;
        return this;
    }

    /**
     * @param showBottom 是否显示底部
     */
    public BaseDialog setShowBottom(boolean showBottom) {
        mShowBottomEnable = showBottom;
        return this;
    }

    /**
     * @param showTop 是否显示顶部
     */
    public BaseDialog setShowTop(boolean showTop) {
        mShowTopEnable = showTop;
        return this;
    }

    /**
     * @param width  弹框的宽
     * @param height 弹框的高
     */
    public BaseDialog setSize(int width, int height) {
        mWidth = width;
        mHeight = height;
        return this;
    }

    public BaseDialog setWidth(int width) {
        mWidth = width;
        return this;
    }

    /**
     * @param marginHor 设置左右margin
     */
    public BaseDialog setMarginHor(int marginHor) {
        mMarginHor = marginHor;
        return this;
    }

    /**
     * @param marginVer 设置顶底部margin
     */
    public BaseDialog setMarginVer(int marginVer) {
        mMarginVer = marginVer;
        return this;
    }

    /**
     * @param animStyle 设置进入退出动画
     */
    public BaseDialog setAnimStyle(@StyleRes int animStyle) {
        mAnimStyle = animStyle;
        return this;
    }

    /**
     * @param touchOutCancel 设置是否点击外部取消
     */
    public BaseDialog setTouchOutCancel(boolean touchOutCancel) {
        mTouchOutCancel = touchOutCancel;
        return this;
    }

    /**
     * @param cancelable 设置是否点击外部取消
     */
    public BaseDialog setDialogCancelable(boolean cancelable) {
        mCancelable = cancelable;
        return this;
    }

    /**
     * @param manager 展示弹框
     */
    public BaseDialog show(FragmentManager manager) {
        show(manager, String.valueOf(System.currentTimeMillis()));
        return this;
    }


    /**
     * 防止Fragment在Activity 调用onSaveInstanceState() 后显示 造成的crash
     */
    public BaseDialog showAllowingStateLoss(FragmentManager manager, String tag) {
        try {
            Field mDismissed = DialogFragment.class.getDeclaredField("mDismissed");
            mDismissed.setAccessible(true);
            mDismissed.set(this, false);
            Field mShownByMe = DialogFragment.class.getDeclaredField("mShownByMe");
            mShownByMe.setAccessible(true);
            mShownByMe.set(this, true);
        } catch (Exception e) {
            e.printStackTrace();
        }
        FragmentTransaction ft = manager.beginTransaction();
        ft.add(this, tag);
        ft.commitAllowingStateLoss();

        return this;
    }

    public BaseDialog showAllowingStateLoss(FragmentManager manager) {
        return showAllowingStateLoss(manager, getClass().getName());
    }


    protected void convertView(ViewHolder viewHolder, BaseDialog baseDialog) {

    }

    /**
     * 权限回调
     *
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        PermissionsUtil.getInstance().onRequestPermissionsResult(this, requestCode, permissions, grantResults);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        PermissionsUtil.getInstance().onActivityResult(this, requestCode, resultCode, data);
    }

    @Override
    public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && mKeyBack) {
            dismissAllowingStateLoss();
            return true;
        }
        return true;

    }

    public BaseDialog setKeyBack(boolean keyBack) {
        mKeyBack = keyBack;
        return this;
    }


    public class ViewHolder {
        private SparseArray<View> mViews;
        private View mConvertView;

        public ViewHolder(View view) {
            mConvertView = view;
            mViews = new SparseArray<>();
        }

        public <T extends View> T getView(@IdRes int viewId) {
            View view = mViews.get(viewId);
            if (view == null) {
                view = mConvertView.findViewById(viewId);
                mViews.put(viewId, view);
            }
            return (T) view;
        }

        public ViewHolder setText(int viewId, String text) {
            TextView textView = getView(viewId);
            textView.setText(text);
            return this;
        }

        public ViewHolder setTextColor(int viewId, int colorId) {
            TextView textView = getView(viewId);
            textView.setTextColor(colorId);
            return this;
        }

        public ViewHolder setBackgroundResource(int viewId, int resId) {
            View view = getView(viewId);
            view.setBackgroundResource(resId);
            return this;
        }

        public ViewHolder setOnClickListener(int viewId, View.OnClickListener listener) {
            getView(viewId).setOnClickListener(listener);
            return this;
        }

    }


}
