package com.mirkowu.lib_upgrade;

public interface IUpgradeInfo {
    String getTitle();

    String getContent();

    String getApkUrl();

    String getVersionName();

    /**
     * 设置文件保存路径，默认路径
     * <p>
     * 先获取 /mnt/sdcard/Android/data/com.my.app/cache
     * 获取不到上面的，再获取 /data/data/com.my.app/cache
     */
    String getSavePath();

    int getVersionCode();

    int isForceUpgrade();
}
