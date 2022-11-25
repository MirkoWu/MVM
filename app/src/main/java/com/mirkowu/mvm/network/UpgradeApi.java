package com.mirkowu.mvm.network;

import com.mirkowu.mvm.bean.UpgradeBean;

import io.reactivex.rxjava3.core.Observable;
import retrofit2.http.GET;
import retrofit2.http.Url;

public interface UpgradeApi {
    String UPGRADE_URL = "http://rlw8qmxdu.hd-bkt.clouddn.com/upgrade/version.json";

    @GET
    Observable<UpgradeBean> getUpgradeInfo(@Url String url);
}
