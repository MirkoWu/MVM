package com.mirkowu.mvm.mvvm;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.lifecycle.Observer;


import com.mirkowu.lib_network.ErrorBean;
import com.mirkowu.lib_network.ErrorType;
import com.mirkowu.lib_widget.stateview.OnRefreshListener;
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

        mMediator.mRequestImageListData.observe(this, new Observer<Object>() {
            @Override
            public void onChanged(Object o) {
                hideLoadingDialog();
                binding.stateview.setGoneState();
                binding.tvTime.setText(o.toString());
            }
        });
        mMediator.mRequestImageListError.observe(this, errorBean -> {
            hideLoadingDialog();
            if (errorBean.isNetError()) {
                binding.stateview.setShowState(R.mipmap.ic_launcher, errorBean.msg(), true);
            } else if (errorBean.isBizError()) {
                Toast.makeText(this, errorBean.code() + ":" + errorBean.msg(), Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, errorBean.code() + ":" + errorBean.msg(), Toast.LENGTH_SHORT).show();
            }
        });

        //数据监听变化
        mMediator.mLiveData.observe(this, new Observer<Object>() {
            @Override
            public void onChanged(Object o) {
                hideLoadingDialog();
                binding.stateview.setGoneState();
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
        binding.stateview.setLoadingState(R.mipmap.ic_launcher, "加载中");
        binding.stateview.setOnRefreshListener(() -> getTimeClick(null));
        //请求数据
        mMediator.getData();
    }

//    @Override
//    protected MVVMMediator initMediator() {
//        return new MVVMMediator();
//    }


    public void getTimeClick(View view) {
        showLoadingDialog();
        //请求数据
        // mMediator.getData();
        mMediator.loadImage();
    }

}