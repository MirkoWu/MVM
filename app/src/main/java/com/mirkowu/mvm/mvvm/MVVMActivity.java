package com.mirkowu.mvm.mvvm;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.Toast;

import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.mirkowu.lib_base.widget.RefreshHelper;
import com.mirkowu.lib_network.ErrorBean;
import com.mirkowu.lib_util.LogUtil;
import com.mirkowu.lib_widget.adapter.BaseRVAdapter;
import com.mirkowu.mvm.R;
import com.mirkowu.mvm.base.BaseActivity;
import com.mirkowu.mvm.databinding.ActivityMVVMBinding;

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
        imageAdapter.setOnItemClickListener((view, item, position) -> LogUtil.i("TAG", "onItemClick: " + position));
        imageAdapter.setOnItemChildClickListener((view, item, position) -> LogUtil.i("TAG", "onItemChildClick: " + position));
        imageAdapter.setOnItemChildLongClickListener(new BaseRVAdapter.OnItemChildLongClickListener() {
            @Override
            public boolean onItemChildLongClick(View view, Object item, int position) {
                LogUtil.i("TAG", "onItemChildLongClick: " + position);
                return false;
            }
        });

//        LiveDataUtilKt.observerRequest(mMediator.mRequestImageListData, this,
//                () -> null, () -> null,
//                gankImageBeans -> {
//                    refreshHelper.setLoadMore(imageAdapter, gankImageBeans);
//                    return null;
//                }, errorBean -> {
//                    if (errorBean.isNetError()) {
//                        binding.stateview.setShowState(R.drawable.widget_svg_disconnect, errorBean.msg(), true);
//                    } else if (errorBean.isApiError()) {
//                        Toast.makeText(MVVMActivity.this, errorBean.code() + ":" + errorBean.msg(), Toast.LENGTH_SHORT).show();
//                    } else {
//                        Toast.makeText(MVVMActivity.this, errorBean.code() + ":" + errorBean.msg(), Toast.LENGTH_SHORT).show();
//                    }
//                    return null;
//                });
        mMediator.mRequestImageListData.observe(this, responseData -> {
            hideLoadingDialog();
            if (responseData.isSuccess()) {
                refreshHelper.setLoadMore(imageAdapter, responseData.data);
            } else if (responseData.isFailure()) {
                refreshHelper.finishLoad();
                ErrorBean errorBean = responseData.error;
                if (errorBean.isNetError() && refreshHelper.isFirstPage()) {
                    binding.stateview.setShowState(R.drawable.widget_svg_disconnect, errorBean.msg(), true);
                } else if (errorBean.isApiError()) {
                    Toast.makeText(MVVMActivity.this, errorBean.code() + ":" + errorBean.msg(), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MVVMActivity.this, errorBean.code() + ":" + errorBean.msg(), Toast.LENGTH_SHORT).show();
                }
            }
        });
        mMediator.mImageError.observe(this, new Observer<ErrorBean>() {
            @Override
            public void onChanged(ErrorBean errorBean) {
                hideLoadingDialog();
                refreshHelper.finishLoad();
                binding.stateview.setShowState(R.drawable.widget_svg_disconnect, errorBean.msg(), true);
            }
        });

        binding.stateview.setLoadingState(getString(R.string.widget_loading));
        //binding.stateview.setLoadingState(R.mipmap.ic_launcher, getString(R.string.widget_loading));
        binding.stateview.setOnRefreshListener(() -> refreshHelper.autoRefresh());

        refreshHelper.refresh();
    }

    @Override
    public void onLoadData(int page) {
        showLoadingDialog();
//        binding.stateview.setGoneState();
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