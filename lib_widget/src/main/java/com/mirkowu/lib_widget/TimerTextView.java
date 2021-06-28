package com.mirkowu.lib_widget;

import android.content.Context;
import android.os.CountDownTimer;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.TextView;


/**
 * @author by DELL
 * @date on 2018/3/15
 * @describe 倒计时View 语言需自己处理
 */

public class TimerTextView extends TextView {
    private int COUNT_DOWN_TIME = 60; //倒计时 默认60s
    private int mInterval = 1; //间隔 默认1s
    private CountDownTimer downTimer;
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
        initView(context);
    }

    private void initView(Context context) {
        hintText = context.getString(R.string.widget_timer_hint_text);
        formatText = context.getString(R.string.widget_timer_count_format_text);
        finishText = context.getString(R.string.widget_timer_finish_text);

        setText(hintText);
        setGravity(Gravity.CENTER);
    }

    private void createTimer() {
        downTimer = new CountDownTimer(COUNT_DOWN_TIME * 1000L, mInterval * 1000L) {
            @Override
            public void onTick(long millisUntilFinished) {
                //四舍五入 优化系统误差
                setText(String.format(formatText, Math.round((double) millisUntilFinished / 1000L)));
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
    public void setHintText(String text) {
        hintText = text;
        setText(hintText);
    }

    public String getHintText() {
        return hintText;
    }

    /**
     * 倒计时显示的文本 必须有一个 %d
     *
     * @param formatStr
     */
    public void setFormatText(String formatStr) {
        formatText = formatStr;
    }

    public String getFormatText() {
        return formatText;
    }

    public void setFinishText(String text) {
        finishText = text;
    }

    public String getFinishText() {
        return finishText;
    }

    /**
     * 设置倒计时的时间，单位 秒 默认60s
     *
     * @param duration
     */
    public void setTimerTime(int duration) {
        COUNT_DOWN_TIME = duration;
    }

    public int getTimerTime() {
        return COUNT_DOWN_TIME;
    }

    /**
     * 设置倒计时间隔 单位 秒 默认 1s
     *
     * @param interval
     */
    public void setTimerInterval(int interval) {
        mInterval = interval;
    }

    public int getTimerInterval() {
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
        if (downTimer != null) {
            downTimer.cancel();
        }
        createTimer();
        downTimer.start();
    }

    private void finish() {
        setText(finishText);
        setEnabled(true);
    }

    public void cancel() {
        if (downTimer != null) downTimer.cancel();
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
