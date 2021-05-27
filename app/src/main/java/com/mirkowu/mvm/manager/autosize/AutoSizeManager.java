package com.mirkowu.mvm.manager.autosize;

import android.app.Application;

import me.jessyan.autosize.AutoSize;
import me.jessyan.autosize.AutoSizeConfig;


/**
 * 屏幕适配 管理
 */
public class AutoSizeManager {
    private static volatile AutoSizeManager sInstance;

    public static AutoSizeManager getInstance() {
        if (sInstance == null) {
            synchronized (AutoSizeManager.class) {
                if (sInstance == null) {
                    sInstance = new AutoSizeManager();
                }
            }
        }
        return sInstance;
    }

    private AutoSizeManager() {

    }


    public void setConfig(Application application) {
        AutoSize.checkAndInit(application); //先初始化，防止多进程
        AutoSizeConfig.getInstance()
                .setBaseOnWidth(true)
                .setDesignWidthInDp(375)
                .setDesignHeightInDp(812)
                .setAutoAdaptStrategy(new CustomAutoAdaptStrategy()); //自定义策略
    }

}
