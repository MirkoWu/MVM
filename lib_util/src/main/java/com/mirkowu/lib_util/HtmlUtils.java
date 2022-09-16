package com.mirkowu.lib_util;

import android.os.Build;
import android.text.Html;
import android.widget.TextView;

/**
 * @author: mirko
 * @date: 20-3-31
 */
public class HtmlUtils {
    /**
     * TextView 设置Html文本 支持html格式文本
     *
     * @param textView
     * @param content
     */
    public static void setTextViewHtml(TextView textView, String content) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            textView.setText(Html.fromHtml(content, Html.FROM_HTML_MODE_COMPACT));
        } else {
            textView.setText(Html.fromHtml(content));
        }
    }
}
