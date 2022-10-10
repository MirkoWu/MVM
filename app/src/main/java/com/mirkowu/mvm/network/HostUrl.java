package com.mirkowu.mvm.network;

public class HostUrl {
    public static boolean isDebug = true;
    private static final String HOST_TEST = "https://api.apiopen.top/";
    private static final String HOST_RELEASE = "https://api.apiopen.top/";
    public static String HOST = isDebug ? HOST_TEST : HOST_RELEASE;
}
//https://gank.io/api/data/福利/10/1