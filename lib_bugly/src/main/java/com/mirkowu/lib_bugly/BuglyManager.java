package com.mirkowu.lib_bugly;

import android.content.Context;
import android.text.TextUtils;

import com.mirkowu.lib_util.LogUtil;
import com.tencent.bugly.Bugly;
import com.tencent.bugly.beta.Beta;
import com.tencent.bugly.beta.UpgradeInfo;
import com.tencent.bugly.beta.download.DownloadListener;
import com.tencent.bugly.beta.ui.UILifecycleListener;
import com.tencent.bugly.beta.upgrade.UpgradeListener;
import com.tencent.bugly.beta.upgrade.UpgradeStateListener;
import com.tencent.bugly.crashreport.CrashReport;

import java.io.File;
import java.util.List;

public class BuglyManager {
    public static final String CHANNEL = "bugly";
    public static final String TAG = BuglyManager.class.getSimpleName();
    private static OnUpgradeListener sOnUpgradeListener;
    private static UpgradeStateListener sUpgradeStateListener;

    /**
     * 初始化
     *
     * @param context
     * @param appId
     * @param isDebug 是否开启debug模式，true表示打开debug模式，false表示关闭调试模式
     */
    public static void init(Context context, String appId, boolean isDebug) {
        init(context, appId, CHANNEL, isDebug);
    }

    public static void init(Context context, String appId, String channel, boolean isDebug) {
        //注意这些设置在bugly init之前
        Beta.strToastYourAreTheLatestVersion = null; //取消 最新版本的提示
        Beta.upgradeDialogLayoutId = R.layout.up_dialog_upgrade;
        BuglyManager.setUpgradeListener(new UpgradeListener() {
            @Override
            public void onUpgrade(int i, UpgradeInfo upgradeInfo, boolean isManual, boolean isSilence) {
                LogUtil.e(TAG, "onUpgrade: 是否有新版本 =" + (upgradeInfo != null));

                if (sOnUpgradeListener != null) {
                    sOnUpgradeListener.onUpgrade(upgradeInfo != null, upgradeInfo);
                }
            }
        });
        Beta.upgradeStateListener = new UpgradeStateListener() {
            @Override
            public void onUpgradeFailed(boolean b) {
                LogUtil.e(TAG, "onUpgradeFailed !");
                if (sOnUpgradeListener != null) {
                    sOnUpgradeListener.onError();
                }
                if (sUpgradeStateListener != null) {
                    sUpgradeStateListener.onUpgradeFailed(b);
                }

            }

            @Override
            public void onUpgradeSuccess(boolean b) {
                if (sUpgradeStateListener != null) {
                    sUpgradeStateListener.onUpgradeSuccess(b);
                }
            }

            @Override
            public void onUpgradeNoVersion(boolean b) {
                if (sOnUpgradeListener != null) {
                    sOnUpgradeListener.onUpgrade(false, null);
                }
                if (sUpgradeStateListener != null) {
                    sUpgradeStateListener.onUpgradeNoVersion(b);
                }

            }

            @Override
            public void onUpgrading(boolean b) {
                if (sUpgradeStateListener != null) {
                    sUpgradeStateListener.onUpgrading(b);
                }
            }

            @Override
            public void onDownloadCompleted(boolean b) {
                if (sUpgradeStateListener != null) {
                    sUpgradeStateListener.onDownloadCompleted(b);
                }
            }
        };
        Beta.autoCheckUpgrade = false; //是否自动检查更新
//        Beta.initDelay = 5000L; //设置启动延时为5s（默认延时3s），APP启动5s后初始化SDK，避免影响APP启动速度;

        // 设置是否为上报进程
        CrashReport.UserStrategy strategy = new CrashReport.UserStrategy(context);
        if (!TextUtils.isEmpty(channel)) {
            strategy.setAppChannel(channel);  //设置渠道
        }
        //
        //strategy.setUploadProcess(ProcessUtils.isMainProcess());

        Bugly.init(context, appId, isDebug, strategy);
    }


    public static void registerDownloadListener(DownloadListener downloadListener) {
        Beta.registerDownloadListener(downloadListener);
    }

    public static void unregisterDownloadListener() {
        Beta.unregisterDownloadListener();
    }

    public static void startDownloadTask() {
        Beta.startDownload();
    }

    public static void cancelDownload() {
        Beta.cancelDownload();
    }


    /**
     * 检测更新
     *
     * @param isManual          用户手动点击检查，非用户点击操作请传false
     * @param isSilence         是否静默更新，不显示弹窗等交互，[true:没有弹窗和toast] [false:有弹窗或toast]
     * @param onUpgradeListener 回调结果
     */
    public static void checkUpgrade(OnUpgradeListener onUpgradeListener) {
        sOnUpgradeListener = onUpgradeListener;
        Beta.checkUpgrade(true, true);
    }

    public interface OnUpgradeListener {
        /**
         * @param hasNewVersion 是否有新版本
         * @param upgradeInfo
         */
        void onUpgrade(boolean hasNewVersion, UpgradeInfo upgradeInfo/*, boolean isManual*/);

        //        void onNoVersion();

        default void onError() {
        }
    }


    /*
     * true表示app启动自动初始化升级模块;
     * false不会自动初始化;
     * 开发者如果担心sdk初始化影响app启动速度，可以设置为false，
     * 在后面某个时刻手动调用Beta.init(getApplicationContext(),false);
     */
    public void setAutoInit(boolean autoInit) {
        Beta.autoInit = autoInit;
    }

    /**
     * 获取本地已有升级策略（非实时，可用于界面红点展示）
     * public String id = "";//唯一标识
     * public String title = "";//升级提示标题
     * public String newFeature = "";//升级特性描述
     * public long publishTime = 0;//升级发布时间,ms
     * public int publishType = 0;//升级类型 0测试 1正式
     * public int upgradeType = 1;//升级策略 1建议 2强制 3手工
     * public int popTimes = 0;//提醒次数
     * public long popInterval = 0;//提醒间隔
     * public int versionCode;
     * public String versionName = "";
     * public String apkMd5;//包md5值
     * public String apkUrl;//APK的CDN外网下载地址
     * public long fileSize;//APK文件的大小
     * pubilc String imageUrl; // 图片url
     *
     * @return
     */
    public static UpgradeInfo getUpgradeInfo() {
        return Beta.getUpgradeInfo();
    }

    /**
     * true表示初始化时自动检查升级;
     * false表示不会自动检查升级,需要手动调用Beta.checkUpgrade()方法;
     */
    public void setAutoCheckUpgrade(boolean autoCheckUpgrade) {
        Beta.autoCheckUpgrade = autoCheckUpgrade;
    }

    /**
     * 只允许在MainActivity上显示更新弹窗，其他activity上不显示弹窗;
     * 不设置会默认所有activity都可以显示弹窗;
     */
    public void setCanShowUpgradeActs(List<Class> activities) {
        if (activities != null) {
            for (Class c : activities) {
                Beta.canShowUpgradeActs.add(c);
            }
        }
    }

    /**
     * 设置自定义升级对话框UI布局
     * 注意：因为要保持接口统一，需要用户在指定控件按照以下方式设置tag，否则会影响您的正常使用：
     * 标题：beta_title，如：android:tag="beta_title"
     * 升级信息：beta_upgrade_info  如： android:tag="beta_upgrade_info"
     * 更新属性：beta_upgrade_feature 如： android:tag="beta_upgrade_feature"
     * 取消按钮：beta_cancel_button 如：android:tag="beta_cancel_button"
     * 确定按钮：beta_confirm_button 如：android:tag="beta_confirm_button"
     * 详见layout/upgrade_dialog.xml
     */
    public void setUpgradeDialogLayout(int layoutId) {
        Beta.upgradeDialogLayoutId = layoutId;
    }

    /**
     * 设置升级检查周期为60s(默认检查周期为0s)，60s内SDK不重复向后台请求策略);
     */
    public void setUpgradeCheckPeriod(int checkTimeDuration) {
        Beta.upgradeCheckPeriod = checkTimeDuration;
    }

    /**
     * 设置启动延时为1s（默认延时3s），APP启动1s后初始化SDK，避免影响APP启动速度;
     */
    public void setInitDelay(int initDelay) {
        Beta.initDelay = initDelay;
    }

    /**
     * 设置通知栏大图标，largeIconId为项目中的图片资源;
     */
    public void setNotificationBigIcon(int bigIcon) {
        Beta.largeIconId = bigIcon;
    }

    /**
     * 设置状态栏小图标，smallIconId为项目中的图片资源Id;
     */
    public void setNotificationSmallIcon(int smallIcon) {
        Beta.smallIconId = smallIcon;
    }

    /**
     * 设置更新弹窗默认展示的banner，defaultBannerId为项目中的图片资源Id;
     * 当后台配置的banner拉取失败时显示此banner，默认不设置则展示“loading“;
     */
    public void setDefaultBannerId(int defaultBannerId) {
        Beta.defaultBannerId = defaultBannerId;
    }

    /**
     * 设置sd卡的Download为更新资源保存目录;
     * 后续更新资源会保存在此目录，需要在manifest中添加WRITE_EXTERNAL_STORAGE权限;
     */
    public void setDownloadDir(File downFile) {
        Beta.storageDir = downFile;
    }

    /**
     * 点击过确认的弹窗在APP下次启动自动检查更新时会再次显示;
     */
    public void setShowInterruptedStrategy(boolean isShow) {
        Beta.showInterruptedStrategy = isShow;
    }

    /**
     * 设置升级对话框生命周期回调接口,onCreate、onStart等
     *
     * @param listener
     */
    public static void setUILifecycleListener(UILifecycleListener listener) {
        Beta.upgradeDialogLifecycleListener = listener;
    }

    /**
     * 在application中初始化时设置监听，监听策略的收取
     * <p>
     * <p>
     * * 接收到更新策略
     * * @param ret  0:正常 －1:请求失败
     * * @param strategy 更新策略
     * * @param isManual true:手动请求 false:自动请求
     * * @param isSilence true:不弹窗 false:弹窗
     * * @return 是否放弃SDK处理此策略，true:SDK将不会弹窗，策略交由app自己处理
     *
     * @param listener
     */
    public static void setUpgradeListener(UpgradeListener listener) {
        Beta.upgradeListener = listener;
    }

    /**
     * 设置更新状态回调接口
     *
     * @param listener
     */
    public static void setUpgradeStateListener(UpgradeStateListener listener) {
        sUpgradeStateListener = listener;
    }

    public static void removeUpgradeStateListener() {
        sUpgradeStateListener = null;
    }

    public static void removeUpgradeListener() {
        sOnUpgradeListener = null;
    }
}
