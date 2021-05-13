package com.mirkowu.mvm.mvvm;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.TextView;

import androidx.lifecycle.Observer;


import com.mirkowu.lib_core.util.InstanceFactory;
import com.mirkowu.mvm.R;
import com.mirkowu.mvm.base.BaseBindingActivity;

public class MVVMActivity extends BaseBindingActivity<MVVMMediator, com.mirkowu.mvm.databinding.ActivityMVVMBinding> {

    public static void start(Context context) {
        Intent starter = new Intent(context, MVVMActivity.class);
//    starter.putExtra();
        context.startActivity(starter);
    }

    TextView tvTime;


    @Override
    protected int getLayoutId() {
        return R.layout.activity_m_v_v_m;
    }

    @Override
    protected void initialize() {
        tvTime = findViewById(R.id.tvTime);
        //数据监听变化
        mediator.mLiveData.observe(this, new Observer<Object>() {
            @Override
            public void onChanged(Object o) {
                tvTime.setText(o.toString());
            }
        });


        //请求数据
        mediator.getData();
    }

    @Override
    protected void createMediator() {
        if (mediator == null) {
            mediator = InstanceFactory.newViewModel(this, getClass());
        }
    }

    public void getTimeClick(View view) {
        //请求数据
        mediator.getData();
    }

}