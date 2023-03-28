package com.mirkowu.mvm.bean;

import com.google.gson.annotations.SerializedName;
import com.mirkowu.lib_upgrade.IUpgradeInfo;

public class UpgradeBean implements IUpgradeInfo {
    @SerializedName("version_name")
    public String versionName;
    @SerializedName("version_code")
    public int versionCode;
    @SerializedName("title")
    public String title;
    @SerializedName("content")
    public String content;
    @SerializedName("apk_url")
    public String apkUrl;
    @SerializedName("is_force_upgrade")
    public boolean isForceUpgrade;

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public String getContent() {
        return content;
    }

    @Override
    public String getApkUrl() {
        return apkUrl;
    }

    @Override
    public String getVersionName() {
        return versionName;
    }

    @Override
    public String getSavePath() {
        return null;
    }

    @Override
    public int getVersionCode() {
        return versionCode;
    }

    @Override
    public boolean isForceUpgrade() {
        return isForceUpgrade;
    }
}
