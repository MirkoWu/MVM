package com.mirkowu.lib_util;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;

import java.util.HashMap;

/**
 * @author by DELL
 * @date on 2020/5/17
 * @describe
 */
public class MarketUtil {

//    腾讯应用宝 com.tencent.android.qqdownloader
//    360手机助手 com.qihoo.appstore
//    百度手机助手 com.baidu.appsearch
//    小米应用商店 com.xiaomi.market
//    华为应用商店 com.huawei.appmarket
//    Google Play Store com.android.vending
//    魅族应用市场 com.meizu.mstore
//    豌豆荚 com.wandoujia.phoenix2
//    91手机助手 com.dragon.android.pandaspace
//    PP手机助手 com.pp.assistant
//    OPPO应用商店 com.oppo.market
//    VIVO应用商店 com.bbk.appstore
//    搜狗应用市场 com.sogou.androidtool
//    三星应用商店 com.sec.android.app.samsungapps
//    联想应用商店 com.lenovo.leos.appstore
//    中兴应用商店 zte.com.market
//    安智应用商店 com.hiapk.marketpho
//    应用汇 com.yingyonghui.market
//    机锋应用市场 com.mappn.gfan
//    安卓市场 com.hiapk.marketpho
//    GO商店 cn.goapk.market
//    酷派应用商店 com.yulong.android.coolmart
//    酷市场 com.coolapk.market
//    金立软件商店 com.gionee.aora.market


    public static final String MARKET_COOL = "com.coolapk.market";//  酷安
    public static final String MARKET_YYB = "com.tencent.android.qqdownloader";//  应用宝
    public static final String MARKET_BAIDU = "com.baidu.appsearch";//  百度
    public static final String MARKET_HUAWEI = "com.huawei.appmarket";//  华为
    public static final String MARKET_XIAOMI = "com.xiaomi.market";//  小米
    public static final String MARKET_OPPO = "com.oppo.market";//  oppo
    public static final String MARKET_VIVO = "com.bbk.appstore";//  vivo
    public static final String MARKET_MEIZU = "com.meizu.mstore";//  魅族
    public static final String MARKET_360 = "com.qihoo.appstore";//  360手机助手
    public static final String MARKET_ANZHI = "cn.goapk.market";//  安智市场
    public static final String MARKET_ANZHUO = "com.hiapk.marketpho";//  安卓市场

    public static final String MARKET_SAMSUNG = "com.sec.android.app.samsungapps";//  三星
    public static final String MARKET_WDJ = "com.wandoujia.phoenix2";//  豌豆荚
    public static final String MARKET_91 = "com.dragon.android.pandaspace";//  91助手
    public static final String MARKET_SOGOU = "com.sogou.androidtool";//  搜狗
    public static final String MARKET_GOOGLE = "com.android.vending";//  google
    public static final String MARKET_TB = "com.taobao.appcenter";//  淘宝手机助手

    public static final String CHANNEL_COOL = "coolapk";
    public static final String CHANNEL_YYB = "yyb";
    public static final String CHANNEL_BAIDU = "baidu";
    public static final String CHANNEL_XIAOMI = "xiaomi";
    public static final String CHANNEL_HUAWEI = "huawei";
    public static final String CHANNEL_OPPO = "oppo";
    public static final String CHANNEL_VIVO = "vivo";
    public static final String CHANNEL_MEIZU = "meizu";
    public static final String CHANNEL_360 = "360";
    public static final String CHANNEL_ANZHI = "anzhi";

    public static HashMap<String, String> channelMarket = new HashMap<>();

    static {
        channelMarket.put(CHANNEL_COOL, MARKET_COOL);
        channelMarket.put(CHANNEL_YYB, MARKET_YYB);
        channelMarket.put(CHANNEL_BAIDU, MARKET_BAIDU);
        channelMarket.put(CHANNEL_XIAOMI, MARKET_XIAOMI);
        channelMarket.put(CHANNEL_HUAWEI, MARKET_HUAWEI);
        channelMarket.put(CHANNEL_OPPO, MARKET_OPPO);
        channelMarket.put(CHANNEL_VIVO, MARKET_VIVO);
        channelMarket.put(CHANNEL_MEIZU, MARKET_MEIZU);
        channelMarket.put(CHANNEL_360, MARKET_360);
        channelMarket.put(CHANNEL_ANZHI, MARKET_ANZHI);
    }

    public static boolean startMarketDetailByChannel(Context context, String channel, String appPkg) {
        if (channelMarket.containsKey(channel)) {
            String marketPkg = channelMarket.get(channel);
            if (AppInfoUtil.isInstallApp(context, marketPkg)) {
                return startMarketDetail(context, marketPkg, appPkg);
            }
        }

        //自动匹配
       // return startMarketDetail(context, null, appPkg);
        return false;
    }

    /**
     * 打开应用市场 应用详情页
     *
     * @return
     */
    public static boolean startMarketDetail(Context context, String marketPkg, String appPkg) {
        if (TextUtils.isEmpty(marketPkg)) return false;
        if (TextUtils.isEmpty(appPkg)) return false;
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            //intent.setData(Uri.parse("market://details"));//market://details?id=
            intent.setData(Uri.parse("market://details?id=" + appPkg));//market://details?id=
            intent.setPackage(marketPkg);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
            return true;
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 应用市场 搜索关键词
     *
     * @param context
     * @return
     */
    public static boolean startMarketSearch(Context context, String marketPkg, String keyword) {
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse("market://search?q=" + keyword));
            intent.setPackage(marketPkg);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
            return true;
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return false;
    }
}
