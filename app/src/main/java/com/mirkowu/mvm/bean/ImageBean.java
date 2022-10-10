package com.mirkowu.mvm.bean;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ImageBean {
    @SerializedName("id")
    public String id;
    @SerializedName("title")
    public String title;
    @SerializedName("type")
    public String type;
    @SerializedName("url")
    public String url;

}
