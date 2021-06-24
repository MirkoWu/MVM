//package com.mirkowu.mvm.manager.autosize;
//
//import android.app.Activity;
//import android.app.Application;
//import android.content.res.Resources;
//
//import androidx.fragment.app.Fragment;
//
//import com.mirkowu.lib_base.view.IAutoAdapt;
//
//import java.util.Locale;
//
//import me.jessyan.autosize.AutoSize;
//import me.jessyan.autosize.AutoSizeCompat;
//import me.jessyan.autosize.AutoSizeConfig;
//import me.jessyan.autosize.internal.CustomAdapt;
//import me.jessyan.autosize.utils.AutoSizeLog;
//
//
///**
// * 屏幕适配 管理
// */
//public class AutoSizeManager {
////    private static volatile AutoSizeManager sInstance;
////
////    public static AutoSizeManager getInstance() {
////        if (sInstance == null) {
////            synchronized (AutoSizeManager.class) {
////                if (sInstance == null) {
////                    sInstance = new AutoSizeManager();
////                }
////            }
////        }
////        return sInstance;
////    }
//    /*** 设计稿尺寸 宽度*/
//    public static final int DESIGN_WIDTH_IN_DP = 375;
//    /*** 设计稿尺寸 高度*/
//    public static final int DESIGN_HEIGHT_IN_DP = 812;
//
//    private AutoSizeManager() {
//    }
//
//
//    public static void init(Application application) {
//        AutoSize.checkAndInit(application); //先初始化，防止多进程
//        AutoSizeConfig.getInstance()
//                .setBaseOnWidth(true)
//                .setDesignWidthInDp(DESIGN_WIDTH_IN_DP)
//                .setDesignHeightInDp(DESIGN_HEIGHT_IN_DP)
//                .setAutoAdaptStrategy(new CustomAutoAdaptStrategy()); //自定义策略
//    }
//
//
//    /**
//     * 适配策略里使用
//     *
//     * @param target
//     * @param activity
//     */
//    public static void autoConvertStrategy(Object target, Activity activity) {
//        //如果 target 实现 CustomAdapt 接口表示该 target 想自定义一些用于适配的参数, 从而改变最终的适配效果
//        if (target instanceof IAutoAdapt && target instanceof CustomAdapt) {
//            AutoSizeLog.d(String.format(Locale.ENGLISH, "%s implemented by %s!", target.getClass().getName(), CustomAdapt.class.getName()));
//            AutoSize.autoConvertDensityOfCustomAdapt(activity, (CustomAdapt) target);
//        } else if (target instanceof IAutoAdapt) {
//            AutoSizeLog.d(String.format(Locale.ENGLISH, "%s used the global configuration.", target.getClass().getName()));
//            AutoSize.autoConvertDensityOfGlobal(activity);
//        } else if (target instanceof CustomAdapt) {
//            AutoSizeLog.d(String.format(Locale.ENGLISH, "%s implemented by %s!", target.getClass().getName(), CustomAdapt.class.getName()));
//            AutoSize.autoConvertDensityOfCustomAdapt(activity, (CustomAdapt) target);
//        } else { //默认不适配
//            AutoSizeLog.w(String.format(Locale.ENGLISH, "%s canceled the adaptation!", target.getClass().getName()));
//            AutoSize.cancelAdapt(activity);
//        }
//    }
//
//    /**
//     * Activity getResource()中做适配使用
//     *
//     * @param superResources
//     * @param activity
//     */
//    public static void autoConvertActivity(Resources superResources, Activity activity) {
//        if (activity instanceof IAutoAdapt && activity instanceof CustomAdapt) {
//            AutoSizeCompat.autoConvertDensityOfCustomAdapt(superResources, (CustomAdapt) activity);
//        } else if (activity instanceof IAutoAdapt) {
//            AutoSizeCompat.autoConvertDensityOfGlobal(superResources);
//        } else if (activity instanceof CustomAdapt) {
//            AutoSizeCompat.autoConvertDensityOfCustomAdapt(superResources, (CustomAdapt) activity);
//        }
//    }
//
//    /**
//     * Fragment getResource()中做适配使用
//     *
//     * @param superResources
//     * @param fragment
//     */
//    public static void autoConvertFragment(Resources superResources, Fragment fragment) {
//        if (fragment instanceof IAutoAdapt && fragment instanceof CustomAdapt) {
//            AutoSizeCompat.autoConvertDensityOfCustomAdapt(superResources, (CustomAdapt) fragment);
//        } else if (fragment instanceof IAutoAdapt) {
//            AutoSizeCompat.autoConvertDensityOfGlobal(superResources);
//        } else if (fragment instanceof CustomAdapt) {
//            AutoSizeCompat.autoConvertDensityOfCustomAdapt(superResources, (CustomAdapt) fragment);
//        }
//    }
//}
