package com.mirkowu.lib_network;

public class ErrorBean {
    private final ErrorType errorType;
    private final int code;
    private final String msg;


    public ErrorBean(ErrorType errorType, int code, String msg) {

        this.errorType = errorType;
        this.code = code;
        this.msg = msg;
    }

    public ErrorType errorType() {
        return errorType;
    }

    public int code() {
        return code;
    }

    public String msg() {
        return msg;
    }

    public boolean isNetError() {
        return errorType == ErrorType.NET;
    }

    public boolean isApiError() {
        return errorType == ErrorType.API;
    }
}
