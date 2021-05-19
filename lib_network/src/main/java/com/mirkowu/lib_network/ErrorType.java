package com.mirkowu.lib_network;

public interface ErrorType {
    //未知
    int ERROR_UNKNOW = -1;
    //设置业务出错
    int ERROR_API = -66;
    int ERROR_BUSINESS = -99;
    //网络问题
    int ERROR_HTTP = -100;
    //网络连接失败
    int ERROR_NET_CONNECT = -101;
    //请求超时
    int ERROR_NET_TIMEOUT = -102;
}
