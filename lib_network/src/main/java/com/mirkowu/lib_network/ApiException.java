package com.mirkowu.lib_network;

public class ApiException extends RuntimeException {
    private final int code;
    private final String msg;


    public ApiException(int code, String msg) {
        super(msg);
        this.code = code;
        this.msg = msg;
    }

    public int code() {
        return code;
    }

    public String msg() {
        return msg;
    }
}
