package com.mirkowu.lib_crash;

import android.content.Context;
import android.text.TextUtils;

import com.mirkowu.lib_bugly.BuglyManager;
import com.mirkowu.lib_util.utilcode.util.ProcessUtils;
import com.mirkowu.lib_util.utilcode.util.Utils;
import com.tencent.bugly.crashreport.CrashReport;

/**
 * Bugly异常上报
 */
public class CrashManager {
    /**
     * 初始化
     * 如果使用了 版本更新SDK 直接用 {@link BuglyManager} 初始化就行
     *
     * @param context
     * @param appId   申请的应用ID
     * @param isDebug 是否debug模式
     */
    @Deprecated
    public static void init(Context context, String appId, boolean isDebug) {
        init(context, appId, null, isDebug);
    }

    /**
     * 初始化
     * 如果使用了 版本更新SDK 直接用 {@link BuglyManager} 初始化就行
     *
     * @param context
     * @param appId   申请的应用ID
     * @param channel 渠道
     * @param isDebug 是否debug模式
     */
    @Deprecated
    public static void init(Context context, String appId, String channel, boolean isDebug) {

        // 设置是否为上报进程
        CrashReport.UserStrategy strategy = new CrashReport.UserStrategy(context);
        if (!TextUtils.isEmpty(channel)) {
            strategy.setAppChannel(channel);  //设置渠道
        }
        strategy.setUploadProcess(ProcessUtils.isMainProcess());
        //初始化
        CrashReport.initCrashReport(context, appId, isDebug, strategy);
    }

    public static void setUserId(Context context, String userId) {
        CrashReport.setUserId(context, userId);
    }

    /**
     * 主动上报异常信息
     *
     * @param e
     */
    public static void reportError(Throwable e) {
        CrashReport.postCatchedException(e);
    }


    public static void reportError(BuglyException e) {
        CrashReport.postCatchedException(e);
    }

    public static void reportError(String message, Throwable e) {
        CrashReport.postCatchedException(new BuglyException(message, e));
    }

    /**
     * 自定义Map参数可以保存发生Crash时的一些自定义的环境信息。在发生Crash时会随着异常信息一起上报并在页面展示。
     * <p>
     * 最多可以有9对自定义的key-value（超过则添加失败）；
     * key限长50字节，value限长200字节，过长截断；
     * key必须匹配正则：[a-zA-Z[0-9]]+。
     *
     * @param key
     * @param value
     */
    public static void putUserData(String key, String value) {
        CrashReport.putUserData(Utils.getApp(), key, value);
    }
}
