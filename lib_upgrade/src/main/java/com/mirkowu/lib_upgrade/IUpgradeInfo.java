package com.mirkowu.lib_upgrade;

public interface IUpgradeInfo {
    String getTitle();

    String getContent();

    String getApkUrl();

    String getVersionName();

    int getVersionCode();

    int isForceUpgrade();
}
