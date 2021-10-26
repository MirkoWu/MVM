package com.mirkowu.lib_screen;

import android.app.Activity;
import android.app.Application;

import com.mirkowu.lib_screen.external.ExternalAdaptInfo;
import com.mirkowu.lib_screen.internal.CancelAdapt;
import com.mirkowu.lib_screen.utils.AutoSizeLog;

import java.util.Locale;


/**
 * ================================================
 * 屏幕适配逻辑策略默认实现类, 可通过 {@link AutoSizeConfig#init(Application, boolean, AutoAdaptStrategy)}
 * 和 {@link AutoSizeConfig#setAutoAdaptStrategy(AutoAdaptStrategy)} 切换策略
 *
 * @see AutoAdaptStrategy
 * Created by JessYan on 2018/8/9 15:57
 * <a href="mailto:jess.yan.effort@gmail.com">Contact me</a>
 * <a href="https://github.com/JessYanCoding">Follow me</a>
 * ================================================
 */
public class CustomAutoAdaptStrategy implements AutoAdaptStrategy {
    @Override
    public void applyAdapt(Object target, Activity activity) {

        //检查是否开启了外部三方库的适配模式, 只要不主动调用 ExternalAdaptManager 的方法, 下面的代码就不会执行
        if (AutoSizeConfig.getInstance().getExternalAdaptManager().isRun()) {
            if (AutoSizeConfig.getInstance().getExternalAdaptManager().isCancelAdapt(target.getClass())) {
                AutoSizeLog.i(String.format(Locale.ENGLISH, "%s canceled the adaptation!", target.getClass().getName()));
                AutoSize.cancelAdapt(activity);
                return;
            } else {
                ExternalAdaptInfo info = AutoSizeConfig.getInstance().getExternalAdaptManager()
                        .getExternalAdaptInfoOfActivity(target.getClass());
                if (info != null) {
                    AutoSizeLog.d(String.format(Locale.ENGLISH, "%s used %s for adaptation!", target.getClass().getName(), ExternalAdaptInfo.class.getName()));
                    AutoSize.autoConvertDensityOfExternalAdaptInfo(activity, info);
                    return;
                }
            }
        }

        //如果 target 实现 CancelAdapt 接口表示放弃适配, 所有的适配效果都将失效
        if (target instanceof CancelAdapt) {
            AutoSizeLog.i(String.format(Locale.ENGLISH, "%s canceled the adaptation!", target.getClass().getName()));
            AutoSize.cancelAdapt(activity);
            return;
        }


        // 这是默认的逻辑，项目中要改一下，做出主动适配
//        if (target instanceof CustomAdapt) {
//            AutoSizeLog.d(String.format(Locale.ENGLISH, "%s implemented by %s!", target.getClass().getName(), CustomAdapt.class.getName()));
//            AutoSize.autoConvertDensityOfCustomAdapt(activity, (CustomAdapt) target);
//        } else {
//            AutoSizeLog.d(String.format(Locale.ENGLISH, "%s used the global configuration.", target.getClass().getName()));
//            AutoSize.autoConvertDensityOfGlobal(activity);
//        }

        /**
         * 自定义策略
         */
        AutoSizeManager.autoConvertStrategy(target, activity);
    }


}

