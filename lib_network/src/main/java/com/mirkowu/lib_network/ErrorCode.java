package com.mirkowu.lib_network;

public interface ErrorCode {
    //未知
    int UNKNOW = -1;

    //设置业务出错
    int ERROR_BUSINESS = -99;

    //网络连接失败
    int NET_CONNECT = -101;
    //请求超时
    int NET_TIMEOUT = -102;
    //未知HOST
    int NET_UNKNOWNHOST = -103;
}
