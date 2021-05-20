package com.mirkowu.mvm.bean;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class GankImageBean {
    /**
     * _id : 5e959250808d6d2fe6b56eda
     * author : 鸢媛
     * category : Girl
     * createdAt : 2020-05-25 08:00:00
     * desc : 与其安慰自己平凡可贵，
     不如拼尽全力活得漂亮。 ​ ​​​​
     * images : ["http://gank.io/images/f4f6d68bf30147e1bdd4ddbc6ad7c2a2"]
     * likeCounts : 7
     * publishedAt : 2020-05-25 08:00:00
     * stars : 1
     * title : 第96期
     * type : Girl
     * url : http://gank.io/images/f4f6d68bf30147e1bdd4ddbc6ad7c2a2
     * views : 16343
     */

    @SerializedName("_id")
    public String id;
    @SerializedName("author")
    public String author;
    @SerializedName("category")
    public String category;
    @SerializedName("createdAt")
    public String createdAt;
    @SerializedName("desc")
    public String desc;
    @SerializedName("likeCounts")
    public int likeCounts;
    @SerializedName("publishedAt")
    public String publishedAt;
    @SerializedName("stars")
    public int stars;
    @SerializedName("title")
    public String title;
    @SerializedName("type")
    public String type;
    @SerializedName("url")
    public String url;
    @SerializedName("views")
    public int views;
    @SerializedName("images")
    public List<String> images;

}
