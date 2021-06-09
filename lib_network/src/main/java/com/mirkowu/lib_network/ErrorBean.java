package com.mirkowu.lib_network;

public class ErrorBean {
    private final ErrorType type;
    private final int code;
    private final String msg;


    public ErrorBean(ErrorType type, int code, String msg) {

        this.type = type;
        this.code = code;
        this.msg = msg;
    }
//
//    public ErrorBean(int code, String msg) {
//        this.code = code;
//        this.msg = msg;
//    }

    public ErrorType type() {
        return type;
    }

    public int code() {
        return code;
    }

    public String msg() {
        return msg;
    }

    public boolean isNetError() {
        return type == ErrorType.NET;
    }
//   public boolean isNetError() {
//        return code > 0 && code < ErrorCode.NET_END;
//    }

//    public boolean isApiError() {
//        return code == ErrorCode.ERROR_BIZ;
//    }

    public boolean isApiError() {
        return type == ErrorType.API;
    }

    @Override
    public String toString() {
        return code + ":" + msg;
    }
}
