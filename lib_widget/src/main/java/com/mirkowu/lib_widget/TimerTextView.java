package com.mirkowu.lib_widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.CountDownTimer;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.mirkowu.lib_util.CheckUtils;


/**
 * @author by DELL
 * @date on 2018/3/15
 * @describe 倒计时View 语言需自己处理
 */

public class TimerTextView extends TextView {
    public static final int DEFAULT_DURATION = 60000;
    public static final int DEFAULT_INTERVAL = 1000;
    private long mDuration = DEFAULT_DURATION; //倒计时 默认60s
    private long mInterval = DEFAULT_INTERVAL; //间隔 默认1000ms
    private CountDownTimer mDownTimer;
    private String hintText;
    private String formatText;
    private String finishText;
    private boolean isEnableWhenCount = false;


    public TimerTextView(Context context) {
        this(context, null);
    }

    public TimerTextView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TimerTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context, attrs);
    }

    private void initView(Context context, AttributeSet attrs) {
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.TimerTextView);
        hintText = array.getString(R.styleable.TimerTextView_timerHintText);
        formatText = array.getString(R.styleable.TimerTextView_timerFormatText);
        finishText = array.getString(R.styleable.TimerTextView_timerFinishText);
        mDuration = array.getInteger(R.styleable.TimerTextView_timerDuration, DEFAULT_DURATION);
        mInterval = array.getInteger(R.styleable.TimerTextView_timerInterval, DEFAULT_INTERVAL);
        array.recycle();

        setTimerHintText(hintText);
        setGravity(Gravity.CENTER);
    }

    private void createTimer() {
        CheckUtils.checkArgument(formatText != null && formatText.contains("%d"), "formatText must contains '%d' !");
        mDownTimer = new CountDownTimer(mDuration, mInterval) {
            @Override
            public void onTick(long millisUntilFinished) {
                //四舍五入 优化系统误差
                setText(String.format(formatText, Math.round(millisUntilFinished / 1000.0)));
            }

            @Override
            public void onFinish() {
                if (mOnTimerListener != null) {
                    mOnTimerListener.onFinish();
                }

                finish();
            }
        };
    }

    /**
     * 默认显示文本
     *
     * @param text
     */
    public void setTimerHintText(String text) {
        hintText = text;
        setText(hintText);
    }

    public String getTimerHintText() {
        return hintText;
    }

    /**
     * 倒计时显示的文本 必须有一个 %d
     *
     * @param formatText 不能为空NUll
     */
    public void setTimerFormatText(@NonNull String formatText) {
        CheckUtils.checkArgument(formatText != null && formatText.contains("%d"), "formatText must contains %%d !");

        this.formatText = formatText;
    }

    public String getTimerFormatText() {
        return formatText;
    }

    public void setTimerFinishText(String text) {
        finishText = text;
    }

    public String getTimerFinishText() {
        return finishText;
    }

    /**
     * 设置倒计时的总时长，单位 秒 默认60 000ms
     *
     * @param duration
     */
    public void setTimerDuration(long duration) {
        mDuration = duration;
    }

    public long getTimerDuration() {
        return mDuration;
    }

    /**
     * 设置倒计时间隔 单位 秒 默认 1000ms
     *
     * @param interval
     */
    public void setTimerInterval(long interval) {
        mInterval = interval;
    }

    public long getTimerInterval() {
        return mInterval;
    }

    /**
     * 倒计时期间是否可用
     *
     * @param enableWhenCount
     */
    public void setEnableWhenCount(boolean enableWhenCount) {
        isEnableWhenCount = enableWhenCount;
    }

    public void start() {
        setEnabled(isEnableWhenCount);
        if (mDownTimer != null) {
            mDownTimer.cancel();
        }
        createTimer();
        mDownTimer.start();
    }

    private void finish() {
        setText(finishText);
        setEnabled(true);
    }

    public void cancel() {
        if (mDownTimer != null) {
            mDownTimer.cancel();
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        cancel();
        super.onDetachedFromWindow();
    }


    public OnTimerListener mOnTimerListener;

    public void setOnTimerListener(OnTimerListener listener) {
        mOnTimerListener = listener;
    }

    public interface OnTimerListener {
        void onFinish();
    }
}
