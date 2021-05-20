package com.mirkowu.mvm.bean;

import com.google.gson.annotations.SerializedName;


public class GankBaseBean<T> {
    /**
     * error : false
     * results :   }
     */

    @SerializedName("status")
    public int status;
    @SerializedName("data")
    public T data;

    public boolean isSuccess() {
        return status == 100;
    }
}
