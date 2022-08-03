package com.mirkowu.mvm.test;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ComponentName;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.HandlerThread;
import android.os.Looper;
import android.view.View;

import com.mirkowu.lib_util.LogUtil;
import com.mirkowu.mvm.R;

import java.util.Comparator;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.ToIntFunction;

import okhttp3.OkHttpClient;

public class TestActivity extends AppCompatActivity {

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
//        Comparator.comparingInt(  value -> );
//        Looper.myLooper();
//        Looper.loop();
//        Looper.myLooper().setMessageLogging();
//        HandlerThread thread=new HandlerThread("");
//        thread.start();
//        thread.getLooper()


}
}