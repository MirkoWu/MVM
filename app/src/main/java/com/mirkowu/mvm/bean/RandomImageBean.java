package com.mirkowu.mvm.bean;

import com.google.gson.annotations.SerializedName;

public class RandomImageBean {

    @SerializedName("mes")
    public String mes;
    @SerializedName("id")
    public Integer id;
    @SerializedName("imgurl")
    public String imgurl;
    @SerializedName("width")
    public Integer width;
    @SerializedName("height")
    public Integer height;
    @SerializedName("client_ip")
    public String clientIp;
    @SerializedName("client_lsp")
    public String clientLsp;
}
