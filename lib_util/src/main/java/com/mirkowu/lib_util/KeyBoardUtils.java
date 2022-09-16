package com.mirkowu.lib_util;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.graphics.Rect;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

/**
 * @author: mirko
 * @date: 20-3-31
 */
public class KeyBoardUtils {

    /**
     * 软键盘是否显示
     *
     * @return
     */
    public static boolean isKeyboardShown(Activity activity/*,View rootView*/) {
        final int softKeyboardHeight = 100;
        Rect r = new Rect();
        View rootView = activity.findViewById(android.R.id.content);
        rootView.getWindowVisibleDisplayFrame(r);
        DisplayMetrics dm = rootView.getResources().getDisplayMetrics();
        //获取屏幕的高度
        int screenHeight = activity.getWindow().getDecorView().getRootView().getHeight();
        // int heightDiff = rootView.getBottom() - r.bottom;
        int heightDiff = screenHeight - r.bottom;
        return heightDiff > softKeyboardHeight * dm.density;
    }

    /**
     * 测量软键盘的底部距离屏幕底部大小
     *
     * @param rootView
     * @return
     */
    public static int getKeyboardToBottomhighet(View rootView) {
        final int softKeyboardHeight = 100;
        Rect r = new Rect();
        rootView.getWindowVisibleDisplayFrame(r);
        DisplayMetrics dm = rootView.getResources().getDisplayMetrics();
        int heightDiff = rootView.getBottom() - r.bottom;
        return heightDiff;
    }

    /**
     * 显示和隐藏软键盘 View ： EditText、TextView isShow : true = show , false = hide
     *
     * @param context
     * @param view
     * @param isShow
     */
    public static void popSoftKeyboard(Context context, View view,
                                       boolean isShow) {
        InputMethodManager imm = (InputMethodManager) context
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        if (isShow) {
            view.requestFocus();
            imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT);
        } else {
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    /**
     * 显示软键盘
     *
     * @param view
     */
    public static void showSoftKeyboard(View view) {
        Context context = view.getContext();
        InputMethodManager imm = (InputMethodManager) context
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        view.requestFocus();
        imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT);
    }


    /**
     * 隐藏软键盘
     *
     * @param view
     */
    public static void hideSoftKeyboard(View view) {
        Context context = view.getContext();
        InputMethodManager imm = (InputMethodManager) context
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public static void copyToClipboard(Context context, CharSequence sequence) {
        //获取剪贴板管理器：
        ClipboardManager cm = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        // 创建普通字符型ClipData
        ClipData mClipData = ClipData.newPlainText("Label", sequence);
        // 将ClipData内容放到系统剪贴板里。
        cm.setPrimaryClip(mClipData);
    }

}
