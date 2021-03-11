package com.mirkowu.mvm;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.mirkowu.mvm.mvc.MVCActivity;
import com.mirkowu.mvm.mvp.MVPActivity;
import com.mirkowu.mvm.mvvm.MVVMActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


    }

    public void mvcClick(View view) {
        MVCActivity.start(this);
    }

    public void mvpClick(View view) {
        MVPActivity.start(this);
    }

    public void mvvmClick(View view) {
        MVVMActivity.start(this);
    }


}