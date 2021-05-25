package com.mirkowu.mvm.mvvm;

import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.mirkowu.lib_base.widget.RefreshHelper;
import com.mirkowu.mvm.R;
import com.mirkowu.mvm.base.BaseActivity;
import com.mirkowu.mvm.bean.GankImageBean;
import com.mirkowu.mvm.databinding.ActivityMVVMBinding;

import java.util.List;

public class MVVMActivity extends BaseActivity<MVVMMediator> implements RefreshHelper.OnRefreshListener {

    public static void start(Context context) {
        Intent starter = new Intent(context, MVVMActivity.class);
//    starter.putExtra();
        context.startActivity(starter);
    }

    private ActivityMVVMBinding binding;
    private RefreshHelper refreshHelper;
    private ImageAdapter imageAdapter;

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
        refreshHelper = new RefreshHelper(binding.mRefresh, binding.rvImage, this);

        imageAdapter = new ImageAdapter();
        binding.rvImage.setAdapter(imageAdapter);
        binding.rvImage.setLayoutManager(new LinearLayoutManager(this));

        mMediator.mRequestImageListData.observe(this, new Observer<List<GankImageBean>>() {
            @Override
            public void onChanged(List<GankImageBean> gankImageBeans) {
                hideLoadingDialog();
                refreshHelper.setLoadMore(imageAdapter, gankImageBeans);
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


        binding.stateview.setLoadingState(R.mipmap.ic_launcher, "加载中");
        binding.stateview.setOnRefreshListener(() -> refreshHelper.autoRefresh());

        refreshHelper.refresh();
    }

    @Override
    public void onLoadData(int page) {
        //showLoadingDialog();
        mMediator.loadImage(page, refreshHelper.getPageCount());
    }

    @Override
    public void onLoadNoMore() {

    }

    @Override
    public void onEmptyChange(boolean isEmpty) {
        if (isEmpty) {
            binding.stateview.setShowState(R.mipmap.ic_launcher, "暂无数据");
        } else {
            binding.stateview.setGoneState();
        }
    }
}