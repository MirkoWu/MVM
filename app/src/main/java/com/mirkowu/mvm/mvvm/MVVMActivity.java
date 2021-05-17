package com.mirkowu.mvm.mvvm;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.lifecycle.Observer;


import com.mirkowu.lib_core.util.InstanceFactory;
import com.mirkowu.mvm.R;
import com.mirkowu.mvm.base.BaseActivity;
import com.mirkowu.mvm.databinding.ActivityMVVMBinding;
import com.mirkowu.mvm.viewbinding.ViewUtilKt;

import kotlin.Unit;
import kotlin.jvm.functions.Function1;

public class MVVMActivity extends BaseActivity<MVVMMediator> {

    public static void start(Context context) {
        Intent starter = new Intent(context, MVVMActivity.class);
//    starter.putExtra();
        context.startActivity(starter);
    }

  //  TextView tvTime;

    private ActivityMVVMBinding binding;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_m_v_v_m;
    }

    @Override
    protected void bindContentView() {
        binding = ActivityMVVMBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
    }

    @Override
    protected void initialize() {



      //  tvTime = findViewById(R.id.tvTime);
        //数据监听变化
        mediator.mLiveData.observe(this, new Observer<Object>() {
            @Override
            public void onChanged(Object o) {
                binding.tvTime.setText(o.toString());
            }
        });
        ViewUtilKt.click(binding.btnTest, new Function1<View, Unit>() {
            @Override
            public Unit invoke(View view) {
                Log.d("TAG", "invoke: ");
                return null;
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