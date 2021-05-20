package com.mirkowu.lib_widget;

import android.content.Context;
import android.os.CountDownTimer;
import android.util.AttributeSet;
import android.view.Gravity;

import androidx.appcompat.widget.AppCompatTextView;


/**
 * @author by DELL
 * @date on 2018/3/15
 * @describe 倒计时View 语言需自己处理
 */

public class TimerTextView extends AppCompatTextView {
    private int COUNT_DOWN_TIME = 60;
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
        downTimer = new CountDownTimer(COUNT_DOWN_TIME * 1000L, 1000L) {
            @Override
            public void onTick(long millisUntilFinished) {
                //四舍五入 优化系统误差
                setText(String.format(formatText, Math.round((double) millisUntilFinished / 1000L)));
            }

            @Override
            public void onFinish() {
                if (onCountDownListener != null) {
                    onCountDownListener.onFinish();
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
     * 设置倒计时的时间，单位 秒
     *
     * @param duration
     */
    public void setCountDownTime(int duration) {
        COUNT_DOWN_TIME = duration;
    }

    public int getCountDownTime() {
        return COUNT_DOWN_TIME;
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


    public OnCountDownListener onCountDownListener;

    public void setOnCountDownListener(OnCountDownListener listener) {
        onCountDownListener = listener;
    }

    public interface OnCountDownListener {
        void onFinish();
    }

}
