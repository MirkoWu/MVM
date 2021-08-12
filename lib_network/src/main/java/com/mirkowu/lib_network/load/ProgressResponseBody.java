package com.mirkowu.lib_network.load;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import androidx.annotation.NonNull;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.ResponseBody;
import okio.Buffer;
import okio.BufferedSource;
import okio.ForwardingSource;
import okio.Okio;
import okio.Source;

public class ProgressResponseBody extends ResponseBody {

    private final Handler mHandler;
    private final ResponseBody mResponseBody;
    private BufferedSource mBufferedSource;
    private OnProgressListener mProgressListener;
    private static final long INTERVAL = 100L; //回调间隔

    public ProgressResponseBody(@NonNull ResponseBody responseBody, OnProgressListener progressListener) {
        this.mResponseBody = responseBody;
        this.mProgressListener = progressListener;
        this.mHandler = new Handler(Looper.getMainLooper(), new Handler.Callback() {
            @Override
            public boolean handleMessage(@NonNull Message message) {
                if (message.obj instanceof ProgressBean) {
                    ProgressBean bean = (ProgressBean) message.obj;
                    if (progressListener != null) {
                        progressListener.onProgress(bean.readBytes, bean.totalBytes);
                    }
                }
                return false;
            }
        });
    }

    @Override
    public MediaType contentType() {
        return mResponseBody.contentType();
    }

    @Override
    public long contentLength() {
        return mResponseBody.contentLength();
    }

    @Override
    public BufferedSource source() {
        if (mBufferedSource == null) {
            mBufferedSource = Okio.buffer(source(mResponseBody.source()));
        }
        return mBufferedSource;
    }

    private Source source(Source source) {

        return new ForwardingSource(source) {
            private long totalBytesRead = 0;
            private long lastUpdateTime;

            @Override
            public long read(@NotNull Buffer sink, long byteCount) throws IOException {
                long byteRead = super.read(sink, byteCount);
                totalBytesRead += (byteRead != -1 ? byteRead : 0);

                long time = System.currentTimeMillis();
                if (time - lastUpdateTime > INTERVAL || byteRead == -1) {
                    Message message = Message.obtain();
                    message.obj = new ProgressBean(totalBytesRead, contentLength());
                    mHandler.sendMessage(message);
                    lastUpdateTime = time;
                }

                return byteRead;
            }
        };
    }
}
