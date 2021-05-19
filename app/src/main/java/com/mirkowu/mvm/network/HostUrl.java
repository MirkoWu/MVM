package com.mirkowu.mvm.network;

public class HostUrl {
    public static boolean isDebug = true;
    private static final String HOST_TEST = "https://gank.io/";
    private static final String HOST_RELEASE = "https://gank.io/";
    public static String HOST = isDebug ? HOST_TEST : HOST_RELEASE;
}
//https://gank.io/api/data/福利/10/1