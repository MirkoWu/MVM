package com.mirkowu.lib_network;

public class ErrorBean {
    private final int errorType;
    private final int code;
    private final String msg;


    public ErrorBean(int errorType, int code, String msg) {

        this.errorType = errorType;
        this.code = code;
        this.msg = msg;
    }

    public int errorType() {
        return errorType;
    }

    public int code() {
        return code;
    }

    public String msg() {
        return msg;
    }

    public boolean isNetError() {
        return errorType == ErrorType.ERROR_HTTP
                || errorType == ErrorType.ERROR_NET_CONNECT
                || errorType == ErrorType.ERROR_NET_TIMEOUT;
    }

    public boolean isBizError() {
        return errorType == ErrorType.ERROR_API || errorType == ErrorType.ERROR_BUSINESS;
    }
}
