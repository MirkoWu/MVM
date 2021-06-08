package com.mirkowu.lib_photo.bean;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import androidx.annotation.NonNull;


/**
 * 图片实体
 */
public class MediaBean implements Parcelable {
    @NonNull
    public Uri uri;
    @NonNull
    public String path;
    public String name;
    public String type;
    public long time;
    public long duration;

    public boolean isVideo() {
        return !TextUtils.isEmpty(type) && type.contains(MineType.VIDEO) && uri != null;
    }

    public MediaBean(@NonNull String path) {
        this.path = path;
    }

    public MediaBean(@NonNull Uri uri, @NonNull String path) {
        this.uri = uri;
        this.path = path;
    }

    public MediaBean(@NonNull Uri uri, @NonNull String path, String name, String type, long time) {
        this.uri = uri;
        this.path = path;
        this.name = name;
        this.type = type;
        this.time = time;
    }

    public MediaBean(@NonNull Uri uri, @NonNull String path, String name, String type, long time, long duration) {
        this.uri = uri;
        this.path = path;
        this.name = name;
        this.type = type;
        this.time = time;
        this.duration = duration;
    }

    protected MediaBean(Parcel in) {
        uri = in.readParcelable(Uri.class.getClassLoader());
        path = in.readString();
        name = in.readString();
        type = in.readString();
        time = in.readLong();
        duration = in.readLong();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(uri, flags);
        dest.writeString(path);
        dest.writeString(name);
        dest.writeString(type);
        dest.writeLong(time);
        dest.writeLong(duration);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<MediaBean> CREATOR = new Creator<MediaBean>() {
        @Override
        public MediaBean createFromParcel(Parcel in) {
            return new MediaBean(in);
        }

        @Override
        public MediaBean[] newArray(int size) {
            return new MediaBean[size];
        }
    };

    @Override
    public boolean equals(Object o) {
        try {
            MediaBean other = (MediaBean) o;
            return TextUtils.equals(this.path, other.path);
        } catch (ClassCastException e) {
            e.printStackTrace();
        }
        return super.equals(o);
    }

    @Override
    public String toString() {
        return "MediaBean{" +
                "uri=" + uri +
                ", path='" + path + '\'' +
                ", name='" + name + '\'' +
                ", type='" + type + '\'' +
                ", time=" + time +
                ", duration=" + duration +
                '}';
    }
}
