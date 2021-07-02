package com.mirkowu.lib_webview.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import androidx.annotation.Nullable;

import com.mirkowu.lib_util.LogUtil;


/**
 * 为了提前开启多进程，防止多进程Activity跳转时白屏
 * 启动对应空Service 然后自动关闭
 */
public class EmptyService extends Service {
    private static final String TAG = EmptyService.class.getSimpleName();

    @Override
    public void onCreate() {
        super.onCreate();
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            startForeground(1, new Notification());
//        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        stopSelf();
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LogUtil.e(TAG, "EmptyService 服务已自动停止");
    }
}
