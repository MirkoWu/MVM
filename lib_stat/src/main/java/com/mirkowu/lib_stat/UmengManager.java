package com.mirkowu.lib_stat;

import android.content.Context;
import android.text.TextUtils;

import com.mirkowu.lib_util.utilcode.util.Utils;
import com.umeng.analytics.MobclickAgent;
import com.umeng.commonsdk.UMConfigure;

import java.util.HashMap;

public class UmengManager {
    private static final String EVENT_API_REQUEST_FAILED = "api_request_failed";

    /**
     * SDK预初始化函数
     * 在Applicaiton.onCreate函数中调用预初始化函数UMConfigure.preInit()，预初始化函数不会采集设备信息，也不会向友盟后台上报数据。
     * preInit预初始化函数耗时极少，不会影响App首次冷启动用户体验
     * <p>
     * !!! 如果用户不同意《隐私政策》授权，则不能调用UMConfigure.init()初始化函数。!!!
     *
     * @param context
     * @param appkey
     * @param channel
     */
    public static void preInit(Context context, String appkey, String channel, boolean isDebug) {
        UMConfigure.setLogEnabled(isDebug);
        UMConfigure.preInit(context, appkey, channel);
        // 支持在子进程中统计自定义事件
        //UMConfigure.setProcessEvent(true);
    }

    /**
     * 初始化
     *
     * @param context
     * @param pushSecret 推送的Secret
     */
    public static void init(Context context, String pushSecret) {
        init(context, UMConfigure.DEVICE_TYPE_PHONE, pushSecret);
    }

    public static void init(Context context, int deviceType, String pushSecret) {
        UMConfigure.init(context, deviceType, pushSecret);

        // 选用AUTO页面采集模式
        MobclickAgent.setPageCollectionMode(MobclickAgent.PageMode.AUTO);

        //支持子进程事件统计
        UMConfigure.setProcessEvent(true);
    }

    /**
     * 一次成对的 onPageStart -> onPageEnd 调用，对应一次非Activity页面(如：Fragment)生命周期统计。
     *
     * @param viewName
     */
    public static void onPageStart(String viewName) {
        MobclickAgent.onPageStart(viewName);
    }

    public static void onPageEnd(String viewName) {
        MobclickAgent.onPageEnd(viewName);
    }


    /**
     * @param provider 账号来源。如果用户通过第三方账号登陆，可以调用此接口进行统计。支持自定义，
     *                 * 不能以下划线”_”开头，使用大写字母和数字标识，长度小于32 字节; 如果是上市
     *                 * 公司，建议使用股票代码。
     * @param id
     */
    public static void onProfileSignIn(String provider, String id) {
        MobclickAgent.onProfileSignIn(provider, id);
    }

    public static void onProfileSignIn(String id) {
        MobclickAgent.onProfileSignIn(id);
    }

    public static void onProfileSignOff() {
        MobclickAgent.onProfileSignOff();
    }

    /**
     * 如果开发者调用kill或者exit之类的方法杀死进程，请务必在此
     * <p>
     * 之前
     * <p>
     * 调用onKillProcess方法，用来保存统计数据。
     */
    public static void onKillProcess() {
        Context context = Utils.getApp();
        MobclickAgent.onKillProcess(context);
    }

    /**
     * 自定义上报异常
     *
     * @param e
     */
    public static void reportError(Throwable e) {
        Context context = Utils.getApp();
        MobclickAgent.reportError(context, e);
    }

    public static void reportError(String error) {
        Context context = Utils.getApp();
        MobclickAgent.reportError(context, error);
    }

    public static void onEvent(String event) {
        onEvent(event, "");
    }

    public static void onEvent(String event, String label) {
        try {
            Context context = Utils.getApp();
            if (!TextUtils.isEmpty(label)) {
                HashMap<String, String> params = new HashMap<>();
                params.put("label", label);
                MobclickAgent.onEvent(context, event, params);
            } else {
                MobclickAgent.onEvent(context, event);
            }
        } catch (Throwable e) {
            reportError(e);
        }
    }

    public static void onEvent(String event, HashMap<String, String> map) {
        try {
            Context context = Utils.getApp();
            if (map != null && !map.isEmpty()) {
                MobclickAgent.onEvent(context, event, map);
            } else {
                MobclickAgent.onEvent(context, event);
            }
        } catch (Throwable e) {
            reportError(e);
        }
    }

    /**
     * 统计Api接口请求失败事件
     *
     * @param api
     * @param code
     */
    public static void onApiRequestFailedEvent(String api, String code) {
        HashMap<String, String> params = new HashMap<>();
        params.put("api", api);
        params.put("code", code);
        params.put("code_api", code + "_" + api);
        onEvent(EVENT_API_REQUEST_FAILED, params);
    }
}
