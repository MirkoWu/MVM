package com.mirkowu.mvm.network;

public class ApiException extends Exception {
    public int code;
    public String msg;

    public ApiException(String message) {
        super(message);
    }

    public ApiException(int code, String msg) {
        super(msg);
        this.code = code;
        this.msg = msg;
    }
}
