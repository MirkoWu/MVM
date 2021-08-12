package com.mirkowu.lib_network.load;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import androidx.annotation.NonNull;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okio.Buffer;
import okio.BufferedSink;
import okio.ForwardingSink;
import okio.Okio;
import okio.Sink;

public class ProgressRequestBody extends RequestBody {
    private OnProgressListener onProgressListener;
    private RequestBody requestBody;
    private BufferedSink bufferedSink;
    private final Handler mHandler;
    private static final long INTERVAL = 100L; //回调间隔

    public ProgressRequestBody(@NonNull RequestBody requestBody, OnProgressListener onUploadListener) {
        this.requestBody = requestBody;
        this.onProgressListener = onUploadListener;
        this.mHandler = new Handler(Looper.getMainLooper(), new Handler.Callback() {
            @Override
            public boolean handleMessage(@NonNull Message message) {
                if (message.obj instanceof ProgressBean) {
                    ProgressBean bean = (ProgressBean) message.obj;
                    if (onUploadListener != null) {
                        onUploadListener.onProgress(bean.readBytes, bean.totalBytes);
                    }
                }
                return false;
            }
        });
    }

    @Override
    public MediaType contentType() {
        return requestBody.contentType();
    }

    @Override
    public long contentLength() throws IOException {
        return requestBody.contentLength();
    }

    @Override
    public void writeTo(BufferedSink sink) throws IOException {
        if (bufferedSink == null) {
            bufferedSink = Okio.buffer(sink(sink));
        }
        //写入
        requestBody.writeTo(bufferedSink);
        //必须调用flush，否则最后一部分数据可能不会被写入
        bufferedSink.flush();
    }

    /**
     * 写入，回调进度接口
     *
     * @param sink Sink
     * @return Sink
     */
    private Sink sink(Sink sink) {
        return new ForwardingSink(sink) {

            //当前写入字节数
            private long bytesWritten = 0L;
            //总字节长度，避免多次调用contentLength()方法
            private long contentLength = 0L;
            private long lastUpdateTime = 0L;

            @Override
            public void write(Buffer source, long byteCount) throws IOException {
                super.write(source, byteCount);
                if (contentLength == 0) {
                    //获得contentLength的值，后续不再调用
                    contentLength = contentLength();
                }
                //增加当前写入的字节数
                bytesWritten += byteCount;

                long time = System.currentTimeMillis();
                if (time - lastUpdateTime > INTERVAL || bytesWritten == contentLength) {
                    Message message = Message.obtain();
                    message.obj = new ProgressBean(bytesWritten, contentLength);
                    mHandler.sendMessage(message);
                    lastUpdateTime = time;
                }
            }
        };
    }
}
