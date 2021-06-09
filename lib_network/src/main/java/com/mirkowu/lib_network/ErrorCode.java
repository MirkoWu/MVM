package com.mirkowu.lib_network;

public interface ErrorCode {
    //网络连接失败
    int NET_CONNECT = 1001;
    //请求超时
    int NET_TIMEOUT = 1002;
    //未知HOST
    int NET_UNKNOWNHOST = 1003;
    //1-1999 为网络错误码
    int NET_END = 1999;


    //设置业务出错
    int ERROR_BIZ = 2001;
    //未知
    int UNKNOW = 9999;
}
