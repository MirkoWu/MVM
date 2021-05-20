package com.mirkowu.lib_util;

import android.Manifest;
import android.app.Service;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyManager;

import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

/**
 * @author by DELL
 * @date on 2019/8/17
 * @describe
 */
public class SimCardUtil {
    public static final String TAG = "SimCardUtil";

    /**
     * 获取手机卡槽数量
     *
     * @param context
     * @return
     */
    @RequiresApi(api = Build.VERSION_CODES.M)
    public static int getPhoneCardCount(Context context) {
        TelephonyManager tm = (TelephonyManager) context.getApplicationContext().getSystemService(Context.TELEPHONY_SERVICE);
        return tm.getPhoneCount();
    }

    /**
     * 当前SIM卡数量
     *
     * @param context
     * @return
     */
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP_MR1)
    public static int getSimCardCount(Context context) {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            return 0;
        }
        return SubscriptionManager.from(context).getActiveSubscriptionInfoCount();

    }

    /**
     * 是否有sim卡
     *
     * @param context
     * @return
     */
    public static boolean isHasSimCard(Context context) {
        TelephonyManager telMgr = (TelephonyManager)
                context.getApplicationContext().getSystemService(Context.TELEPHONY_SERVICE);
        int simState = telMgr.getSimState();
        boolean result = true;
        switch (simState) {
            case TelephonyManager.SIM_STATE_ABSENT:
                result = false; // 没有SIM卡
                break;
            case TelephonyManager.SIM_STATE_UNKNOWN:
                result = false;
                break;
        }
        LogUtil.d(TAG, result ? "有SIM卡" : "无SIM卡");
        return result;
    }

//    /**
//     * 获取数据卡
//     * 返回值1是卡一返回2是卡2，24以上不需要使用反射
//     *
//     * @param context
//     * @return
//     */
//    public static int getDefalutDataID(Context context) {
//        SubscriptionManager subscriptionManager = (SubscriptionManager) context.getApplicationContext().getSystemService(Context.TELEPHONY_SUBSCRIPTION_SERVICE);
//        int subscriberId = 0;
//        if (Build.VERSION.SDK_INT > 24) {
//            subscriberId = SubscriptionManager.getDefaultDataSubscriptionId();
//        } else {
//            try {
//                Class cls = SubscriptionManager.class.getClass();
//                Method method = cls.getDeclaredMethod("getDefaultDataSubId");
//                subscriberId = (Integer) method.invoke(subscriptionManager);
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
//        return subscriberId;
//    }

    /**
     * 获取sim卡信息，
     * <p>
     * 0中获取到的sim卡subId为参数，获取对应的SubscriptionInfo实例，可以获取到sim卡的信息
     *
     * @param slotId 0 是卡1 ; 1是卡2
     * @return
     */
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP_MR1)
    public static SubscriptionInfo getSIMInfo(Context context, int slotId) {
        SubscriptionManager subscriptionManager = (SubscriptionManager) context.getApplicationContext().getSystemService(Context.TELEPHONY_SUBSCRIPTION_SERVICE);
        List<SubscriptionInfo> infos = subscriptionManager.getActiveSubscriptionInfoList();
        if (infos == null) return null;
        SubscriptionInfo infoRet = null;
        for (SubscriptionInfo info : infos) {
            // Log.e("xubaipei", info.toString());
            if (info.getSimSlotIndex() == slotId) {
                infoRet = info;
            }
        }
        return infoRet;
    }

    /**
     * 获取是哪个SIM卡 上网
     *
     * @param context
     * @return 返回 0 是SIM卡1 ；1 是SIM卡2
     */
    public static Integer getDefaultDataSubId(Context context) {
        Integer id = -1;
        try {
            SubscriptionManager sm = null;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
                sm = SubscriptionManager.from(context.getApplicationContext().getApplicationContext());
                Method getSubId = sm.getClass().getMethod("getDefaultDataSubId");
                if (getSubId != null) {
                    id = (int) getSubId.invoke(sm);
                }
            }
        } catch (NoSuchMethodException e) {
            try {
                SubscriptionManager sm = null;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
                    sm = SubscriptionManager.from(context.getApplicationContext().getApplicationContext());
                    Method getSubId = sm.getClass().getMethod("getDefaultDataSubscrptionId");
                    if (getSubId != null) {
                        id = (int) getSubId.invoke(sm);
                    }
                }
            } catch (NoSuchMethodException e1) {
                try {
                    SubscriptionManager sm = null;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
                        sm = SubscriptionManager.from(context.getApplicationContext().getApplicationContext());
                        Method getSubId = sm.getClass().getMethod("getDefaultDataPhoneId");
//            Method getSubId = Class.forName("android.telephony.SubscriptionManager").getDeclaredMethod("getSubId", new Class[]{Integer.TYPE});
                        if (getSubId != null) {
                            id = (int) getSubId.invoke(sm);
                            // Log.v("", (int) getSubId.invoke(sm) + "");
                        }
                    }
                } catch (NoSuchMethodException e2) {
                    e.printStackTrace();
                } catch (IllegalAccessException e2) {
                    e.printStackTrace();
                } catch (InvocationTargetException e2) {
                    e.printStackTrace();
                }
            } catch (IllegalAccessException e1) {
                e.printStackTrace();
            } catch (InvocationTargetException e1) {
                e.printStackTrace();
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return id;

    }

    /**
     * 获取上网卡运营商名称
     *
     * @param context
     * @return
     */
    public static String getNetSimCardProvidersName(Context context) {
        TelephonyManager telMgr = (TelephonyManager)
                context.getApplicationContext().getSystemService(Context.TELEPHONY_SERVICE);
        String str = "Unknown";
        //官方文档只支持5.1及其之后的系统提供双卡API。
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP_MR1) {
            return str;
        }

        try {
            // String imsi = telMgr.getSubscriberId();
            String imsi = telMgr.getSimOperator();
            System.out.println(imsi);
            switch (imsi) {
                case "46000":
                case "46002":
                case "46004":
                case "46007":
                    str = "中国移动";
                    break;
                case "46001":
                case "46006":
                case "46009":
                    str = "中国联通";
                    break;
                case "46003":
                case "46005":
                case "46011":
                    str = "中国电信";
                    break;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return str;
    }

    /**
     * 获取默认通话卡运营商名称
     *
     * @param context
     * @return
     */
    public static String getCallSimCardProvidersName(Context context) {
        TelephonyManager telMgr = (TelephonyManager)
                context.getApplicationContext().getSystemService(Context.TELEPHONY_SERVICE);
        String str = "Unknown";
        //官方文档只支持5.1及其之后的系统提供双卡API。
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP_MR1) {
            return str;
        }

        try {
            // String imsi = telMgr.getSubscriberId();
            String imsi = telMgr.getNetworkOperator();
            System.out.println(imsi);
            switch (imsi) {
                case "46000":
                case "46002":
                case "46004":
                case "46007":
                    str = "中国移动";
                    break;
                case "46001":
                case "46006":
                case "46009":
                    str = "中国联通";
                    break;
                case "46003":
                case "46005":
                case "46011":
                    str = "中国电信";
                    break;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return str;
    }

    /**
     * 获取IMEI 1
     *
     * @param context
     * @return
     */
    public static String getDeviceIMEI(Context context) {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            return "";
        }
        TelephonyManager tm = (TelephonyManager) context.getApplicationContext().getSystemService(Service.TELEPHONY_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            return tm.getImei();
        } else {
            return tm.getDeviceId();
        }

    }

    /**
     * 获取IMEI
     * IMEI国际移动设备识别码（IMEI：International Mobile Equipment Identification Number）是区别移动设备的标志，
     * 储存在移动设备中，可用于监控被窃或无效的移动设备。 目前GSM和WCDMA手机终端需要使用IMEI号码
     *
     * @param slotId slotId为卡槽Id，它的值为 0、1；
     * @return
     */
    public static String getIMEI(Context context, int slotId) {
        try {
            TelephonyManager manager = (TelephonyManager) context.getApplicationContext().getSystemService(Context.TELEPHONY_SERVICE);
            Method method = manager.getClass().getMethod("getImei", int.class);
            String imei = (String) method.invoke(manager, slotId);
            return imei;
        } catch (Exception e) {
            return "";
        }
    }

    /**
     * MEID移动设备识别码(Mobile Equipment Identifier)是CDMA手机的身份识别码，也是每台手机有唯一的识别码。
     * 通过这个识别码，网络端可以对该手机进行跟踪和监管。用于CDMA制式的手机。MEID的数字范围是十六进制的，和IMEI的格式类似。
     *
     * @param context
     * @param slotId
     * @return
     */
    public static String getMEID(Context context, int slotId) {
        try {
            TelephonyManager manager = (TelephonyManager) context.getApplicationContext().getSystemService(Context.TELEPHONY_SERVICE);
            Method method = manager.getClass().getMethod("getMeid", int.class);
            String imei = (String) method.invoke(manager, slotId);
            return imei;
        } catch (Exception e) {
            return "";
        }
    }

    /**
     * IMSI国际移动用户识别码(IMSI International Mobile Subscriber Identification Number)国际上为唯一识别一个移动用户所分配的号码,
     * 是区别移动用户的标志，储存在SIM卡中，可用于区别移动用户的有效信息。
     *
     * @param context
     * @param slotId
     * @return
     */
    public static String getIMSI(Context context, int slotId) {

        return "";
    }


}
