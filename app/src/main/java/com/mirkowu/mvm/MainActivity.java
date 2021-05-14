package com.mirkowu.mvm;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.mirkowu.mvm.base.BaseActivity;
import com.mirkowu.mvm.databinding.ActivityMainBinding;
import com.mirkowu.mvm.mvc.MVCActivity;
import com.mirkowu.mvm.mvp.MVPActivity;
import com.mirkowu.mvm.mvvm.MVVMActivity;
import com.mirkowu.mvm.viewbinding.ViewBindingByReflectKt;
import com.mirkowu.mvm.viewbinding.ViewBindingKt;

public class MainActivity extends BaseActivity {


    @Override
    protected void createMediator() {
//        super.createMediator();//不写中间层就不要创建了
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    protected void initialize() {
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


    public void bindingClick(View view) {
      startActivity(new Intent(this,DataBindingActivity.class));
    }
}