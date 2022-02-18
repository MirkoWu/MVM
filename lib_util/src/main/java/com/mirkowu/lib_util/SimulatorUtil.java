package com.mirkowu.lib_util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.SystemClock;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.RequiresApi;
import androidx.annotation.RequiresPermission;

import com.mirkowu.lib_util.utilcode.util.ShellUtils;
import com.mirkowu.lib_util.utilcode.util.Utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

import static android.Manifest.permission.ACCESS_WIFI_STATE;
import static android.Manifest.permission.INTERNET;

/**
 * @author by DELL
 * @date on 2019/8/5
 * @describe
 */
public class SimulatorUtil {
    private static final String TAG = SimulatorUtil.class.getSimpleName();

    private static String[] known_pipes = {
            "/dev/socket/qemud",
            "/dev/qemu_pipe"
    };

    /**
     * 获取设备品牌
     *
     * @return {@link Build#BRAND}
     */
    public static String getDeviceBrand() {
        return Build.BRAND;
    }

    /**
     * 获取设备的制造厂商
     *
     * @return {@link Build#MANUFACTURER}
     */
    public static String getDeviceManufacturerInfo() {
        return Build.MANUFACTURER;
    }

    /**
     * 获取手机型号
     *
     * @return {@link Build#MODEL}
     */
    public static String getDeviceModel() {
        return Build.MODEL;
    }

    /**
     * 获取设备产品名
     *
     * @return
     */
    public static String getDeviceProduct() {
        return Build.PRODUCT;
    }

    /**
     * 获取设备操作系统版本名
     *
     * @return {@link Build.VERSION#RELEASE}
     */
    public static String getDeviceOSVersionName() {
        // 2017-10-01后SDK不在直通添加 "android " 前缀
        //return "android " + Build.VERSION.RELEASE;
        return Build.VERSION.RELEASE;
    }

    /**
     * 获取设备操作系统版本号
     *
     * @return {@link Build.VERSION#SDK_INT}
     */
    public static int getDeviceOSVersionCode() {
        return Build.VERSION.SDK_INT;
    }


    /**
     * 设备序列号
     *
     * @return {@link Build#SERIAL}
     */
    public static String getDeviceSerialNumber() {
        return Build.SERIAL;
    }


    /**
     * Return the android id of device.
     *
     * @return the android id of device
     */
    @SuppressLint("HardwareIds")
    public static String getAndroidID(Context context) {
        String id = Settings.Secure.getString(
                context.getApplicationContext().getContentResolver(),
                Settings.Secure.ANDROID_ID
        );
        if ("9774d56d682e549c".equals(id)) return "";
        return id == null ? "" : id;
    }

    /**
     * 判断当前手机是否有ROOT权限
     *
     * @return {@code true}: yes<br>{@code false}: no
     */
    public static boolean isRoot() {
        String su = "su";
        String[] locations = {"/system/bin/", "/system/xbin/", "/sbin/", "/system/sd/xbin/",
                "/system/bin/failsafe/", "/data/local/xbin/", "/data/local/bin/", "/data/local/",
                "/system/sbin/", "/usr/bin/", "/vendor/bin/"};
        for (String location : locations) {
            if (new File(location + su).exists()) {
                return true;
            }
        }
        return false;
    }

    /**
     * Return whether device is tablet.
     *
     * @return {@code true}: yes<br>{@code false}: no
     */
    public static boolean isTablet() {
        return (Resources.getSystem().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK)
                >= Configuration.SCREENLAYOUT_SIZE_LARGE;
    }

    /**
     * Return an ordered list of ABIs supported by this device. The most preferred ABI is the first
     * element in the list.
     *
     * @return an ordered list of ABIs supported by this device
     */
    public static String[] getABIs() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            return Build.SUPPORTED_ABIS;
        } else {
            if (!TextUtils.isEmpty(Build.CPU_ABI2)) {
                return new String[]{Build.CPU_ABI, Build.CPU_ABI2};
            }
            return new String[]{Build.CPU_ABI};
        }
    }

    /**
     * 是否是模拟器 等 异常设备
     *
     * @return {@code true}: yes<br>{@code false}: no
     */
    public static boolean isEmulator(Context context) {

        if (isIllegalProperty(context)) return true;

        if (checkIllegalOperatorName(context)) return true;

        if (checkDialIllegal(context)) return true;

        if (!checkPhoneCpu(context)) return true;

        if ((checkPipes())) return true;


        return false;
    }
    /**
     * Return whether device is emulator.
     *
     * @return {@code true}: yes<br>{@code false}: no
     */
    public static boolean isIllegalProperty(Context context) {
        boolean checkProperty = Build.FINGERPRINT.startsWith("generic")
                || Build.FINGERPRINT.toLowerCase().contains("vbox")
                || Build.FINGERPRINT.toLowerCase().contains("test-keys")
                || Build.MODEL.contains("google_sdk")
                || Build.MODEL.contains("Emulator")
                || Build.MODEL.contains("Android SDK built for x86")
                || Build.MANUFACTURER.contains("Genymotion")
                || (Build.BRAND.startsWith("generic") && Build.DEVICE.startsWith("generic"))
                || "google_sdk".equals(Build.PRODUCT)
                || "iPhone".equals(Build.PRODUCT) //部分用户通过刷高仿iPhone系统，将Product、imei等参数进行修改，混过了检测
                || "iPhone".equals(Build.MANUFACTURER);

        return checkProperty;
    }

    /**
     * 运营商名称是否异常
     * @param context
     * @return
     */
    public static boolean checkIllegalOperatorName(Context context) {
        return getOperatorName(context).toLowerCase().equals("android");
    }

    /**
     * 获取运营商名称
     * @param context
     * @return
     */
    public static String getOperatorName(Context context) {
        String operatorName = "";
        try {
            TelephonyManager tm = (TelephonyManager) context.getApplicationContext().getSystemService(Context.TELEPHONY_SERVICE);
            if (tm != null) {
                String name = tm.getNetworkOperatorName();
                if (name != null) {
                    operatorName = name;
                }
            }
        } catch (Exception e) {

        }
        return operatorName;
    }

    /**
     * 是否能打电话
     * @param context
     * @return
     */
    public static boolean checkDialIllegal(Context context) {
        try {
            String url = "tel:" + "123456";
            Intent intent = new Intent();
            intent.setData(Uri.parse(url));
            intent.setAction(Intent.ACTION_DIAL);
            boolean checkDial = intent.resolveActivity(context.getApplicationContext().getPackageManager()) == null;
            return checkDial;
        } catch (Exception e) {
            return true;
        }
    }

    /**
     * 判断cpu是否为电脑来判断 模拟器
     *
     * @return true 为模拟器
     */
    public static boolean checkPhoneCpu(Context context) {
        String cpuInfo = readCpuInfo();
        if ((cpuInfo.contains("intel") || cpuInfo.contains("amd"))) {
            return false;
        }
        return true;
    }

    public static String readCpuInfo() {
        String result = "";
        try {
            String[] args = {"/system/bin/cat", "/proc/cpuinfo"};
            ProcessBuilder cmd = new ProcessBuilder(args);

            Process process = cmd.start();
            StringBuffer sb = new StringBuffer();
            String readLine = "";
            BufferedReader responseReader = new BufferedReader(new InputStreamReader(process.getInputStream(), "utf-8"));
            while ((readLine = responseReader.readLine()) != null) {
                sb.append(readLine);
            }
            responseReader.close();
            result = sb.toString().toLowerCase();
        } catch (IOException ex) {
        }
        return result;
    }


    /**
     * 检测是否有 “/dev/socket/qemud”，“/dev/qemu_pipe”这两个通道  有说明是模拟器
     * @return
     */
    public static boolean checkPipes() {
        for (int i = 0; i < known_pipes.length; i++) {
            String pipes = known_pipes[i];
            File qemu_socket = new File(pipes);
            if (qemu_socket.exists()) {
                Log.v("Result:", "Find pipes!");
                return true;
            }
        }
        Log.i("Result:", "Not Find pipes!");
        return false;
    }
    /**
     * Return the MAC address.
     * <p>Must hold {@code <uses-permission android:name="android.permission.ACCESS_WIFI_STATE" />},
     * {@code <uses-permission android:name="android.permission.INTERNET" />}</p>
     *
     * @return the MAC address
     */
    @RequiresPermission(allOf = {ACCESS_WIFI_STATE, INTERNET})
    public static String getMacAddress(Context context) {
        String macAddress = getMacAddress(context, (String[]) null);
        //如果可以控制wifi 就使用注释的代码，否则setWifiEnabled会弹窗，体验不好
//        if (!macAddress.equals("") || getWifiEnabled(context)) return macAddress;
//        setWifiEnabled(context, true);
//        setWifiEnabled(context, false);
        return getMacAddress(context, (String[]) null);
    }

    private static boolean getWifiEnabled(Context context) {
        @SuppressLint("WifiManagerLeak")
        WifiManager manager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        if (manager == null) return false;
        return manager.isWifiEnabled();
    }

    private static void setWifiEnabled(Context context, final boolean enabled) {
        @SuppressLint("WifiManagerLeak")
        WifiManager manager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        if (manager == null) return;
        manager.setWifiEnabled(enabled);
    }

    /**
     * Return the MAC address.
     * <p>Must hold {@code <uses-permission android:name="android.permission.ACCESS_WIFI_STATE" />},
     * {@code <uses-permission android:name="android.permission.INTERNET" />}</p>
     *
     * @return the MAC address
     */
    @RequiresPermission(allOf = {ACCESS_WIFI_STATE, INTERNET})
    public static String getMacAddress(Context context, final String... excepts) {
        String macAddress = getMacAddressByNetworkInterface();
        if (isAddressNotInExcepts(macAddress, excepts)) {
            return macAddress;
        }
        macAddress = getMacAddressByInetAddress();
        if (isAddressNotInExcepts(macAddress, excepts)) {
            return macAddress;
        }
        macAddress = getMacAddressByWifiInfo(context);
        if (isAddressNotInExcepts(macAddress, excepts)) {
            return macAddress;
        }
        macAddress = getMacAddressByFile();
        if (isAddressNotInExcepts(macAddress, excepts)) {
            return macAddress;
        }
        return "";
    }

    private static boolean isAddressNotInExcepts(final String address, final String... excepts) {
        if (excepts == null || excepts.length == 0) {
            return !"02:00:00:00:00:00".equals(address);
        }
        for (String filter : excepts) {
            if (address.equals(filter)) {
                return false;
            }
        }
        return true;
    }

    @SuppressLint({"MissingPermission", "HardwareIds"})
    private static String getMacAddressByWifiInfo(Context context) {
        try {
            final WifiManager wifi = (WifiManager) context
                    .getApplicationContext().getSystemService(Context.WIFI_SERVICE);
            if (wifi != null) {
                final WifiInfo info = wifi.getConnectionInfo();
                if (info != null) return info.getMacAddress();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "02:00:00:00:00:00";
    }

    private static String getMacAddressByNetworkInterface() {
        try {
            Enumeration<NetworkInterface> nis = NetworkInterface.getNetworkInterfaces();
            while (nis.hasMoreElements()) {
                NetworkInterface ni = nis.nextElement();
                if (ni == null || !ni.getName().equalsIgnoreCase("wlan0")) continue;
                byte[] macBytes = ni.getHardwareAddress();
                if (macBytes != null && macBytes.length > 0) {
                    StringBuilder sb = new StringBuilder();
                    for (byte b : macBytes) {
                        sb.append(String.format("%02x:", b));
                    }
                    return sb.substring(0, sb.length() - 1);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "02:00:00:00:00:00";
    }

    private static String getMacAddressByInetAddress() {
        try {
            InetAddress inetAddress = getInetAddress();
            if (inetAddress != null) {
                NetworkInterface ni = NetworkInterface.getByInetAddress(inetAddress);
                if (ni != null) {
                    byte[] macBytes = ni.getHardwareAddress();
                    if (macBytes != null && macBytes.length > 0) {
                        StringBuilder sb = new StringBuilder();
                        for (byte b : macBytes) {
                            sb.append(String.format("%02x:", b));
                        }
                        return sb.substring(0, sb.length() - 1);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "02:00:00:00:00:00";
    }

    private static InetAddress getInetAddress() {
        try {
            Enumeration<NetworkInterface> nis = NetworkInterface.getNetworkInterfaces();
            while (nis.hasMoreElements()) {
                NetworkInterface ni = nis.nextElement();
                // To prevent phone of xiaomi return "10.0.2.15"
                if (!ni.isUp()) continue;
                Enumeration<InetAddress> addresses = ni.getInetAddresses();
                while (addresses.hasMoreElements()) {
                    InetAddress inetAddress = addresses.nextElement();
                    if (!inetAddress.isLoopbackAddress()) {
                        String hostAddress = inetAddress.getHostAddress();
                        if (hostAddress.indexOf(':') < 0) return inetAddress;
                    }
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static String getMacAddressByFile() {
        ShellUtils.CommandResult result = ShellUtils.execCmd("getprop wifi.interface", false);
        if (result.result == 0) {
            String name = result.successMsg;
            if (name != null) {
                result = ShellUtils.execCmd("cat /sys/class/net/" + name + "/address", false);
                if (result.result == 0) {
                    String address = result.successMsg;
                    if (address != null && address.length() > 0) {
                        return address;
                    }
                }
            }
        }
        return "02:00:00:00:00:00";
    }

    /**
     * 获取电池容量
     *
     * @param context 上下文
     * @return mAh
     */
    public static double getBatteryCapacity(Context context) {
        try {
            final String POWER_PROFILE_CLASS = "com.android.internal.os.PowerProfile";
            Object mPowerProfile_ = Class.forName(POWER_PROFILE_CLASS).getConstructor(Context.class).newInstance(context);
            return (double) Class.forName(POWER_PROFILE_CLASS)
                    .getMethod("getAveragePower", String.class)
                    .invoke(mPowerProfile_, "battery.capacity");
        } catch (Throwable e) {
            LogUtil.e(TAG, e.toString());
        }
        return -1;
    }

    /**
     * 获取操作系统描述
     *
     * @return 获取到 或者 null
     */
    public static String getSystemDescription() {
        try {
            String desc = getSystemProp("ro.build.description", "");
            if (TextUtils.isEmpty(desc)) {
                desc = getSystemProp("ro.build.fingerprint", "");
            }
            return desc;
        } catch (Throwable e) {
            LogUtil.e(TAG, e.toString());
        }
        return null;
    }

    /**
     * 传入指定的key值即可获取指定的系统信息
     *
     * @param key      key
     * @param defValue 获取不到时返回的默认值
     * @return 获取到的值或者 defValue
     */
    public static String getSystemProp(String key, String defValue) {

        // 方案一：采用反射的方法直接调用javaapi进行获取prop的指定数据
        try {
            Class<?> c = Class.forName("android.os.Build");
            Method m = c.getDeclaredMethod("getString", String.class);
            m.setAccessible(true);
            Object result = m.invoke(c, key);
            if (result != null) {
                return result.toString();
            }
        } catch (Throwable e) {
            LogUtil.e(TAG, e.toString());
        }
        return defValue;

        // 方案二：采用getprop的命令方式进行获取

        // 具体实现需要依靠process类来进行，但是实际使用过程中，发现在一些机型上（如：Alcatel One Touch
        // 986（360出品）），运行几次之后就不能继续创建process的实例，并且还会堵塞，因此这个方法暂时注释

        // String prop = Util_System_Process.execute("getprop", key);
        // if (!Basic_StringUtil.isNullOrEmpty(prop)) {
        // return prop;
        // }
        // return defValue;

    }

    /**
     * 获取系统开机时间戳（秒）
     * <p>
     * 原理 ： 当前时间戳 - 开机启动到现在运行的总时间（包括睡眠时间）
     *
     * @return 开机时刻的时间戳（秒）
     */
    public static long getSystemBootTime() {
        return (System.currentTimeMillis() - SystemClock.elapsedRealtime()) / 1000;
    }

    /**
     * Whether user has enabled development settings.
     *
     * @return whether user has enabled development settings.
     */
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    public static boolean isDevelopmentSettingsEnabled() {
        return Settings.Global.getInt(
                Utils.getApp().getContentResolver(),
                Settings.Global.DEVELOPMENT_SETTINGS_ENABLED, 0
        ) > 0;
    }

    /**
     * @return 手机是否开启了开发者调试模式，如果获取不到，就返回false
     */
    public static boolean isAdbEnabled() {
        try {
            return Settings.Global.getInt(Utils.getApp().getContentResolver(),
                    Settings.Global.ADB_ENABLED, 0) > 0;
        } catch (Throwable e) {
            LogUtil.e(TAG, e.toString());
        }
        return false;
    }
}
