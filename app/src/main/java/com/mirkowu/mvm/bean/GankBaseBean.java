package com.mirkowu.mvm.bean;

import com.google.gson.annotations.SerializedName;


public class GankBaseBean<T> {
    /**
     * error : false
     * results :   }
     */

    @SerializedName("code")
    public int code;
    @SerializedName("message")
    public String message;
    @SerializedName("result")
    public T data;

    public boolean isSuccess() {
        return code == 200;
    }
}
