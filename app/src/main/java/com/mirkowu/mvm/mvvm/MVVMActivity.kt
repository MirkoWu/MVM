package com.mirkowu.mvm.mvvm;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.Toast;

import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.mirkowu.lib_base.widget.RefreshHelper;
import com.mirkowu.lib_network.ErrorBean;
import com.mirkowu.lib_network.state.ResponseData;
import com.mirkowu.lib_util.LogUtil;
import com.mirkowu.lib_widget.adapter.BaseRVAdapter;
import com.mirkowu.mvm.R;
import com.mirkowu.mvm.base.BaseActivity;
import com.mirkowu.mvm.bean.GankImageBean;
import com.mirkowu.mvm.bean.RandomImageBean;
import com.mirkowu.mvm.databinding.ActivityMVVMBinding;

import java.util.ArrayList;
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
        refreshHelper = new RefreshHelper(binding.mRefresh, binding.mRecyclerView, this);

        imageAdapter = new ImageAdapter();
        binding.mRecyclerView.setAdapter(imageAdapter);
        binding.mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
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
//                        binding.mStateView.setShowState(R.drawable.widget_svg_disconnect, errorBean.msg(), true);
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
                    //  binding.mStateView.setShowState(R.drawable.widget_svg_disconnect, errorBean.msg(), true);
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
                binding.mStateView.setErrorState(errorBean.msg());
            }
        });

        mMediator.mImageData.observe(this, new Observer<ResponseData<List<RandomImageBean>>>() {
            @Override
            public void onChanged(ResponseData<List<RandomImageBean>> data) {
                hideLoadingDialog();
                refreshHelper.finishLoad();
                if (data.isSuccess()) {
                    if (data.data != null && !data.data.isEmpty()) {
                        String imgUrl = data.data.get(0).imgurl;
                        GankImageBean bean = new GankImageBean();
                        bean.url = imgUrl;
                        List<GankImageBean> list = new ArrayList<>();
                        list.add(bean);

                        refreshHelper.setLoadMore(imageAdapter, list);
                    }
                } else {
                    binding.mStateView.setErrorState(data.error.msg());
                }
            }
        });
        binding.mStateView.setLoadingState();
//        binding.mStateView.setLoadingState("拼命加载中...");
//        binding.mStateView.setLoadingState(getString(R.string.widget_loading));
//        binding.mStateView.setEmptyState(getString(R.string.widget_loading));
//        binding.mStateView.setErrorState("家再说吧");
        //binding.stateview.setLoadingState(R.mipmap.ic_launcher, getString(R.string.widget_loading));
        binding.mStateView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.mStateView.setErrorState("加载失败");
            }
        });
        binding.mStateView.setOnRefreshListener(() ->{
            binding.mStateView.setLoadingState(R.drawable.anim_loading,"sss");
            refreshHelper.autoRefresh();
        });

        refreshHelper.setPageCount(1);
        refreshHelper.refresh();
    }

    @Override
    public void onLoadData(int page) {
       // showLoadingDialog();
        mMediator.loadImage(page, refreshHelper.getPageCount());
    }

    @Override
    public void onLoadNoMore() {

    }

    @Override
    public void onEmptyChange(boolean isEmpty) {
        if (isEmpty) {
            binding.mStateView.setShowState(R.mipmap.ic_launcher, "暂无数据");
        } else {
            binding.mStateView.setGoneState();
        }
    }
}