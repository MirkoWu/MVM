package com.mirkowu.lib_network.download;

import android.os.Parcel;
import android.os.Parcelable;

public class ProgressBean implements Parcelable  {
    long readBytes;
    long totalBytes;

    public ProgressBean(long readBytes, long totalBytes) {
        this.readBytes = readBytes;
        this.totalBytes = totalBytes;
    }

    protected ProgressBean(Parcel in) {
        readBytes = in.readLong();
        totalBytes = in.readLong();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(readBytes);
        dest.writeLong(totalBytes);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<ProgressBean> CREATOR = new Creator<ProgressBean>() {
        @Override
        public ProgressBean createFromParcel(Parcel in) {
            return new ProgressBean(in);
        }

        @Override
        public ProgressBean[] newArray(int size) {
            return new ProgressBean[size];
        }
    };
}
